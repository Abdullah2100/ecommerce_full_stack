drop function fun_calculate_distance_between_user_and_stores;


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
    SELECT "value" INTO kilo_price
    FROM "GeneralSettings"
    WHERE "name" = 'one_kilo_price';
    
    IF kilo_price IS NULL THEN
        RAISE EXCEPTION 'one_kilo_price not found in GeneralSettings';
    END IF;

    -- Get user coordinates with validation
    SELECT longitude, latitude 
    INTO user_long, user_lat
    FROM "Orders" WHERE id = orderId;
    
    IF user_long IS NULL OR user_lat IS NULL THEN
        RAISE EXCEPTION 'NULL coordinates for order %', orderId;
    END IF;

    -- Calculate distance per store
    FOR store_coords IN
        SELECT 
            a.longitude AS store_long,
            a.latitude AS store_lat
        FROM "OrderItems" oi
        JOIN "Address" a 
          ON a."ownerId" = oi."storeId"
        WHERE oi."orderId" = orderId
          AND a.longitude IS NOT NULL
          AND a.latitude IS NOT NULL
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
    SET "distanceFee" = kilo_price * total_distance_km,
	"distanceToUser"=total_distance_km
    WHERE id = orderId;

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
	   (SELECT "Orders"."distanceFee" as fee,
	           "Orders"."createdAt" as createdDate
	   FROM "Orders" 
       WHERE "Orders".id=deleiveryId
	   AND "Orders".status>=5
	   AND ("Orders"."createdAt"<=now()::DATE OR 
	   "Orders"."createdAt"<=monthDate)
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