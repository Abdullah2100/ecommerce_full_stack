# E-Commerce API Documentation

## Table of Contents
1. [Authentication](#authentication)
2. [Users](#users)
3. [Products](#products)
4. [Categories](#categories)
5. [Subcategories](#subcategories)
6. [Orders](#orders)
7. [Stores](#stores)
8. [Banners](#banners)
9. [Variants](#variants)
10. [General Settings](#general-settings)

---

## Authentication

### Login
- **URL**: `/api/User/login`
- **Method**: `POST`
- **Auth Required**: No
- **Request Body**:
  ```json
  {
    "email": "user@example.com",
    "password": "password123"
  }
  ```
- **Success Response**:
  - **Code**: 200
  - **Content**:
    ```json
    {
      "token": "jwt_token_here",
      "user": {
        "id": "user_id",
        "name": "User Name",
        "email": "user@example.com"
      }
    }
    ```

### Register
- **URL**: `/api/User/signup`
- **Method**: `POST`
- **Auth Required**: No
- **Request Body**:
  ```json
  {
    "name": "User Name",
    "email": "user@example.com",
    "password": "password123",
    "phone": "+1234567890"
  }
  ```
- **Success Response**:
  - **Code**: 201
  - **Content**: User details with verification token

---

## Products

### Get Products by Store
- **URL**: `/api/Product/store/{storeId}/{pageNumber}`
- **Method**: `GET`
- **Auth Required**: Yes
- **URL Parameters**:
  - `storeId`: GUID of the store
  - `pageNumber`: Page number (starts from 1)
- **Success Response**:
  - **Code**: 200
  - **Content**: List of products

### Create Product
- **URL**: `/api/Product`
- **Method**: `POST`
- **Auth Required**: Yes (Store Owner)
- **Request Body**:
  ```json
  {
    "name": "Product Name",
    "description": "Product Description",
    "price": 99.99,
    "subcategoryId": "subcategoryId_here",
    "storeId": "store_id_here",
    "thumbnail": "base64_encoded_image",
    "images": ["base64_encoded_image1", "base64_encoded_image2"]
  }
  ```
- **Success Response**:
  - **Code**: 201
  - **Content**: Created product details

### Delete Product
- **URL**: `/api/Product/{storeId}/{productId}`
- **Method**: `DELETE`
- **Auth Required**: Yes (Store Owner)
- **URL Parameters**:
  - `storeId`: GUID of the store
  - `productId`: GUID of the product to delete
- **Success Response**:
  - **Code**: 200
  - **Content**: Success message

---

## Categories

### Get All Categories
- **URL**: `/api/Category/all/{pageNumber}`
- **Method**: `GET`
- **Auth Required**: No
- **URL Parameters**:
  - `pageNumber`: Page number (starts from 1)
- **Success Response**:
  - **Code**: 200
  - **Content**: List of categories

### Create Category
- **URL**: `/api/Category`
- **Method**: `POST`
- **Auth Required**: Yes (Admin)
- **Request Body**:
  ```json
  {
    "name": "Category Name",
    "image": "base64_encoded_image"
  }
  ```
- **Success Response**:
  - **Code**: 201
  - **Content**: Created category details

---

## Orders

### Create Order
- **URL**: `/api/Order`
- **Method**: `POST`
- **Auth Required**: Yes
- **Request Body**:
  ```json
  {
    "items": [
      {
        "productId": "product_id_here",
        "quantity": 2,
        "variantId": "variant_id_here"
      }
    ],
    "shippingAddressId": "address_id_here",
    "paymentMethod": "CASH"
  }
  ```
- **Success Response**:
  - **Code**: 201
  - **Content**: Created order details

### Get User Orders
- **URL**: `/api/Order/user`
- **Method**: `GET`
- **Auth Required**: Yes
- **Query Parameters**:
  - `page`: Page number (default: 1)
  - `status`: Order status (optional)
- **Success Response**:
  - **Code**: 200
  - **Content**: List of user's orders

---

## Stores

### Create Store
- **URL**: `/api/Store`
- **Method**: `POST`
- **Auth Required**: Yes
- **Request Body**:
  ```json
  {
    "name": "Store Name",
    "description": "Store Description",
    "smallImage": "base64_encoded_image",
    "wallpaperImage": "base64_encoded_image"
  }
  ```
- **Success Response**:
  - **Code**: 201
  - **Content**: Created store details

### Get Store by ID
- **URL**: `/api/Store/{storeId}`
- **Method**: `GET`
- **Auth Required**: No
- **URL Parameters**:
  - `storeId`: GUID of the store
- **Success Response**:
  - **Code**: 200
  - **Content**: Store details

---

## Error Responses

### Common Error Responses

#### 400 Bad Request
```json
{
  "success": false,
  "message": "Error message here"
}
```

#### 401 Unauthorized
```json
{
  "success": false,
  "message": "Authentication failed"
}
```

#### 403 Forbidden
```json
{
  "success": false,
  "message": "Access denied"
}
```

#### 404 Not Found
```json
{
  "success": false,
  "message": "Resource not found"
}
```

#### 500 Internal Server Error
```json
{
  "success": false,
  "message": "Internal server error"
}
```

---

## Rate Limiting
- All endpoints are rate limited to 100 requests per minute per IP address.
- Authentication endpoints have a lower limit of 10 requests per minute.

## Authentication
- All endpoints (except login/register) require a valid JWT token in the Authorization header:
  ```
  Authorization: Bearer your_jwt_token_here
  ```

## Versioning
- The API is currently at version 1.
- Versioning is handled through the URL path (e.g., `/api/v1/...`).

## Pagination
- List endpoints support pagination using `page` and `pageSize` parameters.
- Default page size is 20 items.
- Response includes pagination metadata:
  ```json
  {
    "data": [],
    "page": 1,
    "pageSize": 20,
    "totalCount": 100,
    "totalPages": 5
  }
  ```

## Data Validation
- All input is validated on the server.
- Validation errors return a 400 status code with details:
  ```json
  {
    "success": false,
    "message": "Validation failed",
    "errors": [
      "Name is required",
      "Price must be greater than 0"
    ]
  }
  ```
