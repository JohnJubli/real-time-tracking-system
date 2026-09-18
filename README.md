Real-Time Tracking System

A full-stack real-time GPS tracking application built with Java,
Spring Boot, React, MySQL, STOMP WebSocket, SockJS, and
Leaflet/OpenStreetMap.

The application allows an authenticated tracker to share live GPS
coordinates and allows an administrator to monitor the latest location
in real time on a map.

📌 Project Overview

This project demonstrates a complete real-time web application flow:

Tracker Browser
      |
      | Browser GPS
      v
navigator.geolocation.watchPosition()
      |
      | STOMP + WebSocket
      v
/app/sendLocation
      |
      v
Spring WebSocket Controller
      |
      v
LocationService
      |
      v
LocationRepository
      |
      v
MySQL
      |
      v
SimpMessagingTemplate
      |
      v
/topic/locations
      |
      v
Admin Browser
      |
      v
React State
      |
      v
Leaflet + OpenStreetMap

✨ Features

Authentication & Authorization

User registration

User login

Admin login

JWT-based authentication

BCrypt password hashing

Role-based authorization

Protected React routes

Backend authorization for USER and ADMIN

Real-Time Tracking

Browser GPS location using the Geolocation API

Start/Stop tracking

STOMP over WebSocket

SockJS fallback transport

JWT authentication during STOMP connection

Real-time location broadcast

Admin live location monitoring

Database

MySQL persistence

User and location relationships

Location history

Latest location lookup

Date-range location history

Map

Leaflet

OpenStreetMap tiles

Live marker updates

Popup information for tracker/location

🛠️ Technology Stack

Backend

Technology          Purpose

Java 21             Programming language
Spring Boot 3.2.5   Backend framework
Spring Web          REST APIs
Spring Security     Authentication and authorization
Spring Data JPA     Database access
Hibernate           ORM
MySQL 8             Relational database
JWT                 Stateless authentication
BCrypt              Password hashing
STOMP               WebSocket messaging protocol
SockJS              WebSocket fallback
Maven               Build/dependency management

Frontend

Technology      Purpose

React           UI
React Router    Routing
Axios           REST API communication
STOMP.js        WebSocket client
SockJS Client   WebSocket fallback
Leaflet         Interactive maps
React-Leaflet   Leaflet integration
OpenStreetMap   Map tiles
jwt-decode      Reading JWT claims

Development Tools

IntelliJ IDEA

Visual Studio Code

Postman

MySQL Workbench / MySQL CLI

Git

GitHub

Chrome / Edge Developer Tools

🏗️ System Architecture

                         +----------------------+
                         |      React App       |
                         |                      |
                         |  Login / Tracker /   |
                         |      Admin UI        |
                         +----------+-----------+
                                    |
                  +-----------------+------------------+
                  |                                    |
                  | REST / Axios                       | STOMP / SockJS
                  v                                    v
          +---------------+                    +--------------------+
          | AuthController|                    | WebSocket Endpoint |
          | Controllers   |                    |       /ws          |
          +-------+-------+                    +---------+----------+
                  |                                      |
                  v                                      v
          +---------------+                    +--------------------+
          |    Service    |                    | WebSocket Auth     |
          |     Layer     |                    |    Interceptor     |
          +-------+-------+                    +---------+----------+
                  |                                      |
                  +------------------+-------------------+
                                     |
                                     v
                           +-------------------+
                           | Spring Security   |
                           | JWT + Roles       |
                           +---------+---------+
                                     |
                                     v
                           +-------------------+
                           |    Repository     |
                           |       JPA         |
                           +---------+---------+
                                     |
                                     v
                           +-------------------+
                           |      MySQL        |
                           | tracking_db       |
                           +-------------------+

       Real-time broadcast:
       Tracker -> /app/sendLocation -> Backend -> MySQL
                                           |
                                           v
                                  /topic/locations
                                           |
                                           v
                                         Admin

🧱 Backend Architecture

The backend follows a layered architecture:

Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL

Controller Layer

Receives HTTP/WebSocket requests and returns responses.

Examples:

AuthController
AdminController
UserController
LocationController
HistoryController
WebSocketLocationController

Service Layer

Contains business logic.

Examples:

AuthService
LocationService
CustomUserDetailsService

Repository Layer

Communicates with MySQL using Spring Data JPA.

UserRepository
LocationRepository

Security Layer

JwtService
JwtAuthFilter
CustomUserDetailsService

Configuration Layer

SecurityConfig
WebSocketConfig
WebSocketAuthInterceptor
DataInitializer

🎨 Frontend Architecture

src/
├── components/
│   ├── Navbar.jsx
│   ├── ProtectedRoute.jsx
│   ├── LocationMap.jsx
│   └── LiveStatus.jsx
│
├── hooks/
│   └── useLiveLocation.js
│
├── pages/
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── TrackerDashboard.jsx
│   └── AdminDashboard.jsx
│
├── services/
│   ├── api.js
│   ├── authService.js
│   └── websocketService.js
│
├── App.jsx
├── main.jsx
└── index.css

Responsibilities

File                     Responsibility

api.js                 Axios configuration and JWT request handling
authService.js         Login, registration, JWT and role handling
websocketService.js    STOMP/WebSocket connection
useLiveLocation.js     WebSocket lifecycle and live location state
TrackerDashboard.jsx   Browser GPS start/stop
AdminDashboard.jsx     Admin monitoring UI
LocationMap.jsx        Leaflet map and marker
LiveStatus.jsx         Connection status display
ProtectedRoute.jsx     Role-based frontend routing

🔐 Authentication Flow

User
 ↓
POST /api/auth/login
 ↓
AuthController
 ↓
AuthService
 ↓
AuthenticationManager
 ↓
UserDetailsService
 ↓
BCrypt password verification
 ↓
JwtService
 ↓
JWT token
 ↓
React localStorage

The frontend sends the JWT on protected REST requests:

Authorization: Bearer <JWT>

The backend validates the JWT before allowing protected operations.

🔒 Role-Based Authorization

Two roles are used:

ROLE_USER
ROLE_ADMIN

Example:

/api/user/**   → USER
/api/admin/**  → ADMIN
/api/auth/**   → Public
/ws/**         → WebSocket handshake endpoint

Important security principle:

Frontend role checks are used for navigation and UI. The backend
remains the authoritative security boundary.

🔌 WebSocket Architecture

The application uses:

STOMP
SockJS
Spring WebSocket

Connection

React
  |
  | SockJS
  v
/ws
  |
  v
Spring WebSocket
  |
  v
STOMP CONNECT
  |
  | Authorization: Bearer JWT
  v
WebSocketAuthInterceptor
  |
  v
Authenticated Principal

Tracker sends location

Tracker
   |
   | publish
   v
/app/sendLocation
   |
   v
WebSocketLocationController
   |
   v
LocationService
   |
   v
LocationRepository
   |
   v
MySQL

Admin receives location

MySQL save
     |
     v
SimpMessagingTemplate
     |
     v
/topic/locations
     |
     v
Admin STOMP subscription
     |
     v
React state
     |
     v
Leaflet marker

📍 GPS Tracking Flow

The tracker browser uses:

navigator.geolocation.watchPosition()

The browser obtains:

latitude
longitude
accuracy

The application sends:

{
  "latitude": 16.197302,
  "longitude": 81.163537
}

The backend associates the location with the authenticated user instead
of trusting a username supplied by the client.

🗄️ Database Design

Database:

tracking_db

Users

users
--------------------------------
id          BIGINT
username    VARCHAR
password    VARCHAR
role        VARCHAR

Locations

locations
--------------------------------
id          BIGINT
latitude    DOUBLE
longitude   DOUBLE
timestamp   DATETIME
user_id     BIGINT

Relationship:

User 1 ─────────── * Location

One user can have many location records.

🌐 REST API Documentation

Authentication

Register

POST /api/auth/register

Example:

{
  "username": "john",
  "password": "john123"
}

Login

POST /api/auth/login

Example:

{
  "username": "user",
  "password": "user123"
}

Response:

{
  "token": "<JWT>"
}

User APIs

Send location through REST

POST /api/user/location
Authorization: Bearer <USER_JWT>

Example:

{
  "latitude": 16.5062,
  "longitude": 80.6480
}

User history

GET /api/user/history
Authorization: Bearer <USER_JWT>

Admin APIs

Get user history

GET /api/admin/history/{userId}
Authorization: Bearer <ADMIN_JWT>

Get latest location

GET /api/admin/latest/{userId}
Authorization: Bearer <ADMIN_JWT>

Get location history by time range

GET /api/admin/history/{userId}/range?start=<START>&end=<END>
Authorization: Bearer <ADMIN_JWT>

📡 WebSocket Destinations

STOMP endpoint

/ws

Tracker application destination

/app/sendLocation

Admin subscription

/topic/locations

Example message:

{
  "latitude": 16.197302,
  "longitude": 81.163537,
  "userId": 2,
  "username": "user",
  "timestamp": "2026-09-18T20:52:50.805038700"
}

📁 Recommended Repository Structure

real-time-tracking-system/
│
├── backend/
│   ├── pom.xml
│   └── src/
│
├── frontend/
│   └── tracking-frontend/
│       ├── package.json
│       ├── public/
│       └── src/
│
├── database/
│   └── schema.sql
│
├── screenshots/
│   ├── 01-backend-project-structure.png
│   ├── 02-frontend-project-structure.png
│   ├── 03-admin-login.png
│   ├── 04-user-login.png
│   ├── 05-tracker-live-gps.png
│   ├── 06-tracker-stopped.png
│   ├── 07-admin-websocket-console.png
│   └── 08-admin-leaflet-map.png
│
├── .gitignore
├── README.md
└── LICENSE

⚙️ Local Setup

1. Clone the repository

git clone <YOUR_GITHUB_REPOSITORY_URL>
cd real-time-tracking-system

2. Create MySQL database

Open MySQL:

CREATE DATABASE tracking_db;

Then configure the backend database credentials.

Example:

spring.datasource.url=jdbc:mysql://localhost:3306/tracking_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

Do not commit real passwords.

🔑 Environment Variables

Backend

Use environment variables for:

DB_USERNAME
DB_PASSWORD
JWT_SECRET

Example:

spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
jwt.secret=${JWT_SECRET}

Frontend

Create:

frontend/tracking-frontend/.env

Example:

REACT_APP_API_URL=http://localhost:8080
REACT_APP_WS_URL=http://localhost:8080

Do not commit .env.

Commit an example file instead:

.env.example

▶️ Run the Backend

Windows:

cd /d D:\real-time-tracking-system\backend
mvnw.cmd clean spring-boot:run

Backend:

http://localhost:8080

▶️ Run the Frontend

Open another terminal:

cd /d D:\real-time-tracking-system\frontend\tracking-frontend
npm install
npm start

Frontend:

http://localhost:3000

🧪 End-to-End Testing --- Senior Developer Gate Checklist

The project should be tested layer by layer instead of only checking the
UI.

Gate 1 --- MySQL

Verify:

USE tracking_db;

SHOW TABLES;

Expected:

users
locations

Gate 2 --- Backend Startup

Run:

mvnw.cmd clean spring-boot:run

Expected:

Started BackendApplication

Also verify there are no database connection errors.

Gate 3 --- JWT Login

Admin:

username: admin
password: admin123

User:

username: user
password: user123

Expected:

HTTP 200
JWT returned

Gate 4 --- React Compilation

Run:

npm start

Expected:

Compiled successfully

Gate 5 --- User WebSocket

Login as:

user / user123

Expected:

WebSocket Status: CONNECTED

and:

● Live

Gate 6 --- GPS

Click:

Start Tracking

Allow browser location permission.

Expected console:

GPS Location:
{
  latitude: ...,
  longitude: ...,
  accuracy: ...
}

Gate 7 --- Location Sent

Expected:

Location sent:
{
  latitude: ...,
  longitude: ...
}

Gate 8 --- Spring Receives Location

Backend should process:

/app/sendLocation

and execute:

WebSocketLocationController
        ↓
LocationService

Gate 9 --- MySQL INSERT

Run:

SELECT
    id,
    latitude,
    longitude,
    timestamp,
    user_id
FROM locations
ORDER BY id DESC;

A new row should appear.

Gate 10 --- Admin WebSocket

Open another browser or Incognito window.

Login:

admin / admin123

Expected:

WebSocket Status: CONNECTED

and:

● Live

The admin subscribes to:

/topic/locations

Gate 11 --- Admin Receives Location

Keep both browsers open.

Tracker:

Tracking: Active

Admin console should show:

Location received:
{
  latitude: ...,
  longitude: ...,
  userId: 2,
  username: "user",
  timestamp: "..."
}

Gate 12 --- Leaflet Marker

The admin dashboard should display the Leaflet map.

When a new location is received:

WebSocket message
      ↓
React state
      ↓
LocationMap
      ↓
Leaflet marker

The marker should move when new coordinates arrive.

🧭 Controlled Marker Test

If real GPS movement is inconvenient, temporarily test the WebSocket
flow using controlled coordinates.

For example:

{
  "latitude": 16.5062,
  "longitude": 80.6480
}

Then test another coordinate:

{
  "latitude": 16.5075,
  "longitude": 80.6500
}

The admin marker should move between the two positions.

Use this only as a development test. The normal tracker flow uses
browser GPS.


🧪 Test Matrix

Test                         Expected Result

Valid admin login            200 + JWT
Valid user login             200 + JWT
Invalid password             Authentication failure
No JWT on protected API      401
USER accessing ADMIN API     403
ADMIN accessing USER API     403
User WebSocket connection    CONNECTED
Admin WebSocket connection   CONNECTED
GPS permission granted       Coordinates received
GPS permission denied        User-friendly error
Start Tracking               watchPosition() active
Stop Tracking                clearWatch() executed
Location sent                STOMP publish succeeds
Location persisted           MySQL row created
Admin subscription           Location message received
Marker update                Leaflet marker moves
Logout                       JWT/session cleared
Browser refresh              Protected route behavior remains correct

🔐 Security Considerations

The project uses several security practices:

BCrypt password hashing

JWT-based stateless authentication

Spring Security authorization

Role-based access control

JWT validation for WebSocket CONNECT

Authenticated Principal for WebSocket identity

Backend does not trust a client-supplied username for location
ownership

Secrets should be supplied through environment variables

For production deployment, additionally consider:

HTTPS/WSS

Short-lived access tokens

Refresh tokens

Secret management

CORS restricted to the deployed frontend

Rate limiting

Input validation

Database indexes

WebSocket authorization per destination

Centralized logging

Monitoring and alerting



📦 Project Status

Backend                  ✅
Spring Security          ✅
JWT Authentication      ✅
Role Authorization       ✅
REST APIs                ✅
MySQL Persistence        ✅
STOMP WebSocket          ✅
JWT WebSocket Auth       ✅
Browser GPS              ✅
React Tracker            ✅
React Admin              ✅
Leaflet Map              ✅
Real-Time Broadcast      ✅
Location History         ✅

👨‍💻 Author

John Jubli Simhadri

Hyderabad, Telangana, India

Java Developer | Backend Developer | Full-Stack Java Developer

📄 License

This project is intended for learning, portfolio demonstration, and
interview preparation.

Add a formal open-source license if you decide to distribute the project
publicly.
