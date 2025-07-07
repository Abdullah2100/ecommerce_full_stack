
CREATE OR REPLACE FUNCTION fun_calculate_distance_between_user_and_stors(orderId UUID)
RETURNS BOOLEAN 
AS $$
DECLARE 
distance NUMERIC;
userLong NUMERIC;
userLat NUMERIC;
BEGIN

SELECT o.latitude,o.longitude
into userLat,userLong
FROM  "Orders" o
WHERE o.id = orderId;

SELECT COALESCE(SUM(
 ST_DistanceSphere(
    ST_MakePoint(storelat, storelong),
    ST_MakePoint(userlat, userlong)
) / 1000,0.0)
)  INTO distance FROM(
SELECT DISTINCT "storeId",
ad.latitude AS storelat,
ad.longitude AS  storelong
FROM  "OrderItems" oi 
WHERE oi."orderId" = orderId
)  stors
INNER JOIN "Address" ad ON  ad."ownerId" = stors."storeId";

RETURN TRUE;

EXCEPTION
WHEN OTHERS THEN RAISE EXCEPTION 'Something went wrong: %',
RETURN FALSE;
END ;
$$ LANGUAGE plpgsql;