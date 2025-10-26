# E-commerce Full Stack Project Documentation

This document provides an overview of the e-commerce full stack project, including its structure and key components.

## Project Structure

The project is divided into the following main directories:

- `api`: Contains the backend API for the e-commerce platform.
- `dashboard`: Contains the admin dashboard for managing the e-commerce platform.
- `eccorce_app`: Contains the Android application for the e-commerce platform.
- `ecommerce-delivery-man`: Contains the Android application for the delivery man.

## `api` Directory

The `api` directory contains the backend for the e-commerce platform, built with .NET 9.0. It handles all core business logic, including product management, user authentication, order processing, and delivery coordination.

### Features

- Full CRUD operations for Products, Categories, Stores, and Users.
- Secure JWT-based user and delivery authentication.
- Geospatial capabilities for handling location data.
- Real-time communication with SignalR for updates.
- Complete order management workflow.

### Technology Stack

- .NET 9.0 (ASP.NET Core)
- Npgsql.EntityFrameworkCore.PostgreSQL
- NetTopologySuite
- JWT Bearer Authentication
- SignalR
- Swashbuckle.AspNetCore
- FirebaseAdmin

### API Endpoints

The API provides endpoints for managing users, products, categories, subcategories, orders, stores, banners, and variants. A detailed documentation of all the endpoints, including request and response examples, is available in the `api/API_DOCUMENTATION.md` file.

## `dashboard` Directory

The `dashboard` directory contains the admin dashboard for the e-commerce platform. It is a [Next.js](https://nextjs.org/) project bootstrapped with [`create-next-app`](https://nextjs.org/docs/app/api-reference/cli/create-next-app).

To get started with the dashboard, run the development server:

```bash
pnpm dev
```

Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

## `eccorce_app` Directory

The `eccorce_app` directory contains the Android application for the e-commerce platform. It follows a standard Android project structure.

### Technology Stack

- Kotlin

### Directory Structure

- `app/src/main/java/com/example/eccomerce_app`: Contains the source code for the Android application.
    - `di`: Contains the dependency injection modules.
    - `ui`: Contains the user interface components, such as activities, fragments, and adapters.
    - `dto`: Contains the data transfer objects.
    - `data`: Contains the data layer, including repositories and data sources.
    - `util`: Contains utility classes.
    - `model`: Contains the data models.
    - `services`: Contains background services.
    - `viewModel`: Contains the view models.
    - `MainActivity.kt`: The main activity of the application.
    - `MyApplication.kt`: The application class.
    - `MyFirebaseMessagingService.kt`: The Firebase messaging service.

## `ecommerce-delivery-man` Directory

The `ecommerce-delivery-man` directory contains the Android application for delivery personnel. It follows a standard Android project structure, similar to the `eccorce_app`.

### Technology Stack

- Kotlin
