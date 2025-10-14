CREATE EXTENSION postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
   
insert into "GeneralSettings"("Id","Name","Value","CreatedAt") 
VALUES(uuid_generate_v4(),'one_kilo_price',150,CURRENT_TIMESTAMP);

--triger for prevent deleted the orderItems  after it is complated received
CREATE OR REPLACE FUNCTION Fun_prevent_delete_orderItem()
RETURNS Trigger As $$
DECLARE
orderItemStatus INT :=0;
BEGIN
 SELECT "Status" into orderItemStatus FROM "OrdersItems" where "Id"=OLD."Id";
 if orderStatus>3 THEN
   RETURN NULL;
 END IF;
 return OLD;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_prevent_delete_orderItem
BEFORE DELETE ON "OrdersItems" FOR EACH ROW EXECUTE FUNCTION Fun_prevent_delete_orderItem();

--triger for prevent uppdate the order after it is complated
CREATE OR REPLACE FUNCTION fun_prevent_update_orderItem_to_less_state_of_previes()
RETURNS Trigger As $$
DECLARE
orderStatus INT :=0;
BEGIN
  if(OLD."Status">New."Status")
   return null;
 return NEW;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_prevent_delete_orderItem 
BEFORE UPDATE ON "OrdersItems"   FOR EACH ROW EXECUTE FUNCTION fun_prevent_update_orderItem_to_less_state_of_previes();




--triger for prevent deleted the order after it is complated
CREATE OR REPLACE FUNCTION Fun_prevent_delete_order()
RETURNS Trigger As $$
DECLARE
orderStatus INT :=0;
BEGIN
 SELECT "Status" into orderStatus FROM "Orders" where "Id"=OLD."Id";
 if(orderStatus>3)
   return null;
 return OLD;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_prevent_delete_order 
BEFORE DELETE ON "Orders"   FOR EACH ROW EXECUTE FUNCTION Fun_prevent_delete_order();

--triger for prevent uppdate the order after it is complated
CREATE OR REPLACE FUNCTION fun_prevent_update_order_to_less_state_of_previes()
RETURNS Trigger As $$
DECLARE
orderStatus INT :=0;
BEGIN
  if(OLD."Status">New."Status")
   return null;
 return NEW;
END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER tr_prevent_delete_order 
BEFORE UPDATE ON "Orders"   FOR EACH ROW EXECUTE FUNCTION fun_prevent_update_order_to_less_state_of_previes()



CREATE OR REPLACE FUNCTION calculate_order_item_price(OrderItemId UUID,product_price NUMERIC )
RETURNS NUMERIC AS $$
DECLARE
    order_product_varient      RECORD;
    precentage_holder NUMERIC  ;
    price   NUMERIC := product_price;
BEGIN
    

     FOR order_product_varient IN
        SELECT 
             "ProductVarientId"
        FROM "OrdersProductsVarients"
         
    LOOP
        SELECT "Precentage" FROM "ProductVarients" INTO precentage_holder;
   
       price := price*precentage_holder;
    END LOOP;

    RETURN price;
 
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION Calculate_distance_from_store_to_order_location(orderId UUID,storeId UUID)
RETURNS Int AS $$
DECLARE
    total_distance_km DOUBLE PRECISION := 0.0;
    user_long         NUMERIC;
    user_lat          NUMERIC;
    store_long         NUMERIC;
    store_lat          NUMERIC;
    distance_to_user_from_store_lat          NUMERIC;
    kilo_price        NUMERIC;
    store_coords      RECORD;
    store_distance    DOUBLE PRECISION;
BEGIN
    -- Fetch per-km price with error handling
    SELECT "Value" INTO kilo_price
    FROM "GeneralSettings"
    WHERE "Name" = 'one_kilo_price';
    
    IF kilo_price IS NULL THEN
        RAISE EXCEPTION 'one_kilo_price not found in GeneralSettings';
    END IF;

    -- Get user coordinates with validation
    SELECT "Longitude", "Latitude" 
    INTO user_long, user_lat
    FROM "Orders" WHERE "Id" = orderId;

    
    SELECT "Longitude", "Latitude" 
    INTO store_long, store_lat
    FROM "Address" WHERE "OwnerId" = store_id;

    
    store_distance := ST_Distance(
        ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::GEOGRAPHY,
        ST_SetSRID(ST_MakePoint(store_long, store_lat), 4326)::GEOGRAPHY
    ) / 1000.0;


    total_distance_km := total_distance_km + GREATEST(1, CEIL(store_distance));

    RETURN total_distance_km::INT;
 
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION fun_remove_user_orderItem(userId UUID, OrderItemId UUID)
RETURNS BOOLEAN 
AS $$
DECLARE
 order_id      UUID;
 store_id     UUID;
 order_item_price  NUMERIC:=0;
 order_items_count  Int;
 order_items_store_count  Int;

 order_itmes_price  NUMERIC;
 distance_to_user_from_store   INT :=0;

 order_distance     Int;
 order_distance_fee     Int;
 order_fee          NUMERIC;
 is_block_user          BOOLEAN;


BEGIN

    SELECT IsBlocked INTO is_block_user WHERE "Id" = userId;
    IF IsBlocked THEN 
        RAISE EXCEPTION 'user is Blocked';
        RETURN FALSE;
    END IF;

    SELECT "OrderId","StoreId","Price" FROM "OrderItems" INTO order_id,store_id,order_item_price WHERE "Id" = OrderItemId;

    IF order_item_record."Status"!=0 THEN 
        RAISE EXCEPTION 'order is not in progress';
        RETURN FALSE;
    END IF;
    
     -- this to  get the order items 
    SELECT count(*) FROM "OrderItems" INTO order_items_count WHERE "OrderId" = order_id;

 
    IF  order_items_count = 1 THEN 
      DELETE FROM "Orders" WHERE "Id" = order_id;
      RETURN true;
    END IF;

    -- #this to save the origin distance order and fee 
    SELECT "DistanceToUser","DistanceToUser","DistanceFee","TotalPrice" 
			FROM "Orders" INTO order_distance,order_distance,order_distance_fee,order_fee 
			WHERE "OrderId" = order_id;
			

    SELECT count(*) 
			FROM "OrderItems" INTO order_items_store_count 
			WHERE "OrderId" = order_id AND "StoreId" = store_id;

    IF order_items_store_count=1 THEN
        SELECT * FROM Calculate_distance_from_store_to_order_location(order_id,store_id) INTO   distance_to_user_from_store ;    
    END IF;
    
    -- # this to calculate the total price for order item with the product varients 
    order_itmes_price := calculate_order_item_price(OrderItemId, order_item_price);
    
	DELETE FROM "OrderItems" WHERE "Id" = OrderItemId;
    UPDATE "Orders" SET  
           "DistanceToUser" = order_distance-distance_to_user_from_store,
           "DistanceFee" = DistanceFee-((order_distance_fee/order_distance)*distance_to_user_from_store),
           "TotalPrice"= order_fee - order_itmes_price;
    RETURN TRUE;

EXCEPTION 
    WHEN OTHERS THEN
        RAISE WARNING 'this error from remove user orderItems %: %', orderId, SQLERRM;
        RETURN FALSE;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION fun_calculate_distance_between_user_and_stores(orderId UUID)
RETURNS BOOLEAN AS $$
DECLARE
    total_distance_km DOUBLE PRECISION := 0.0;
    user_long         NUMERIC;
    user_lat          NUMERIC;
    kilo_price        NUMERIC;
    store_coords      RECORD;
    store_distance    DOUBLE PRECISION;
BEGIN
    -- Fetch per-km price with error handling
    SELECT "Value" INTO kilo_price
    FROM "GeneralSettings"
    WHERE "Name" = 'one_kilo_price';
    
    IF kilo_price IS NULL THEN
        RAISE EXCEPTION 'one_kilo_price not found in GeneralSettings';
    END IF;

    -- Get user coordinates with validation
    SELECT "Longitude", "Latitude" 
    INTO user_long, user_lat
    FROM "Orders" WHERE "Id" = orderId;
    
    IF user_long IS NULL OR user_lat IS NULL THEN
        RAISE EXCEPTION 'NULL coordinates for order %', orderId;
    END IF;

     FOR store_coords IN
        (SELECT 
            a."Longitude" AS store_long,
            a."Latitude" AS store_lat
        FROM "OrderItems" oi
        JOIN "Address" a 
          ON a."OwnerId" = oi."StoreId"
        WHERE oi."OrderId" = orderId
          AND a."Longitude" IS NOT NULL
          AND a."Latitude" IS NOT NULL)
    LOOP
        -- Calculate distance in meters, convert to km
        store_distance := ST_Distance(
            ST_SetSRID(ST_MakePoint(user_long, user_lat), 4326)::GEOGRAPHY,
            ST_SetSRID(ST_MakePoint(store_coords.store_long, store_coords.store_lat), 4326)::GEOGRAPHY
        ) / 1000.0;

   
        total_distance_km := total_distance_km + GREATEST(1, CEIL(store_distance));
    END LOOP;

    -- Update order with calculated fee
    UPDATE "Orders"
    SET "DistanceFee" = kilo_price * total_distance_km,
	"DistanceToUser"=total_distance_km
    WHERE "Id" = orderId;

    RETURN TRUE;
EXCEPTION 
    WHEN OTHERS THEN
        RAISE WARNING 'Calculation failed for order %: %', orderId, SQLERRM;
        RETURN FALSE;
END;
$$ LANGUAGE plpgsql;




CREATE OR REPLACE FUNCTION get_delivery_fee_info(
 deleiveryId UUID)
RETURNS TABLE (
dayFee DECIMAL,
weekFee DECIMAL,
monthFee DECIMAL,
dayOrder INT,
weekOrder Int)
AS $$
DECLARE
	dayFee DECIMAL:=0.0;
	weekFee DECIMAL :=0.0;
	monthFee DECIMAL :=0.0;
	weekDate DATE := now()::DATE-7;
	monthDate DATE := now()::DATE-3;
	orders   RECORD;
	dayOrder INT:=0;
    weekOrder Int:=0;
BEGIN

   FOR orders IN
	   (SELECT "Orders"."DistanceFee" as fee,
	           "Orders"."CreatedAt" as createdDate
	   FROM "Orders" 
       WHERE "Orders"."Id"=deleiveryId
	   AND "Orders"."Status">=5
	   AND ("Orders"."CreatedAt"<=now()::DATE OR 
	   "Orders"."CreatedAt"<=monthDate)
	   )
   LOOP 
    IF orders.createdDate=now()::DATE THEN 
	  dayFee:= dayFee + orders.fee;
	  dayOrder:= dayOrder+1;
	END IF;

	IF orders.createdDate<=now()::DATE AND orders.createdDate>=weekDate THEN 
	  weekFee := weekFee +  orders.fee;
	  weekOrder:= weekOrder+1;
	END IF ;
     monthFee := monthFee + orders.fee;
   END LOOP;

   RETURN  QUERY SELECT dayFee,weekFee,monthFee,dayOrder,weekOrder;
EXCEPTION
   WHEN OTHERS THEN 
   RAISE EXCEPTION 'Something went wrong: %',SQLERRM;
   RETURN 	 QUERY SELECT NULL,NULL,NULL,NULL,NULL;
END 
$$ LANGUAGE plpgsql;


SELECT * FROM get_delivery_fee_info('3b7406e2-7b35-4cd7-9c5a-d1788555b5b3')
