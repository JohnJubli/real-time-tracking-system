Real-Time GPS Tracking System

A full-stack real-time GPS tracking application built using Java, Spring Boot, React, MySQL, STOMP WebSocket, SockJS, and Leaflet.

The application allows authenticated users to share their live GPS locations while administrators monitor their locations in real time on an interactive map.

🚀 Features

Authentication & Authorization

- User registration and login
- Admin login
- JWT-based authentication
- BCrypt password encryption
- Role-based authorization (USER and ADMIN)
- Protected frontend routes

Real-Time GPS Tracking

- Browser-based GPS location using Geolocation API
- Start and Stop tracking functionality
- Real-time communication using STOMP WebSocket
- SockJS fallback support
- JWT authentication for WebSocket connections
- Live location updates for administrators

Location Management

- Store GPS coordinates in MySQL
- Maintain user-based location history
- Retrieve the latest user location
- View location history by date range

Interactive Map

- Leaflet and OpenStreetMap integration
- Live marker updates
- Location popup information
- Admin location monitoring

🛠️ Technology Stack

Category| Technologies
Backend| Java 21, Spring Boot 3.2.5
Backend APIs| Spring Web, REST APIs
Security| Spring Security, JWT, BCrypt
Database| MySQL 8, Spring Data JPA, Hibernate
Real-Time Communication| STOMP, WebSocket, SockJS
Frontend| React.js, JavaScript, HTML, CSS
Routing| React Router
API Communication| Axios
Map| Leaflet, React-Leaflet, OpenStreetMap
Build Tool| Maven
Development Tools| IntelliJ IDEA, VS Code, Postman, MySQL Workbench
Version Control| Git, GitHub

🏗️ System Architecture

The application follows a layered architecture using Controller, Service, and Repository layers.

                 +----------------------+
                 |      React App       |
                 |                      |
                 | Login / Tracker /     |
                 | Admin Dashboard      |
                 +----------+-----------+
                            |
              +-------------+-------------+
              |                           |
          REST API                   WebSocket
           Axios                    STOMP/SockJS
              |                           |
              v                           v
      +---------------+          +------------------+
      | REST           |          | WebSocket        |
      | Controllers    |          | Controller       |
      +-------+--------+          +--------+---------+
              |                            |
              v                            v
      +---------------+          +------------------+
      | Service Layer |          | Location Service |
      +-------+-------+          +--------+---------+
              |                            |
              +-------------+--------------+
                            |
                            v
                   +----------------+
                   | Spring Security|
                   | JWT + Roles    |
                   +-------+--------+
                           |
                           v
                   +----------------+
                   | Repository     |
                   | Spring Data JPA|
                   +-------+--------+
                           |
                           v
                   +----------------+
                   | MySQL Database |
                   | tracking_db    |
                   +----------------+

🔄 Real-Time Tracking Workflow

The application uses WebSocket communication to share GPS coordinates between the tracker and administrator.

1. The user logs in and authenticates using JWT.
2. The tracker starts GPS tracking through the browser Geolocation API.
3. The browser captures GPS coordinates using "navigator.geolocation.watchPosition()".
4. Coordinates are sent to the backend through the STOMP endpoint "/app/sendLocation".
5. The Spring WebSocket Controller receives the location data.
6. The LocationService processes and saves the coordinates using LocationRepository.
7. MySQL stores the location information.
8. SimpMessagingTemplate broadcasts updates to "/topic/locations".
9. The administrator receives live updates and sees the marker move on the Leaflet map.

Tracker Browser
      |
      v
Browser GPS API
      |
      v
STOMP /app/sendLocation
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
MySQL Database
      |
      v
SimpMessagingTemplate
      |
      v
/topic/locations
      |
      v
Admin Dashboard
      |
      v
React State + Leaflet Map

📸 Application Screenshots

Explore the application's user interface and key features through the screenshots below.

🔐 Authentication

Secure login interfaces for Admin and Tracker users.

<table>
  <tr>
    <td align="center"><b>Tracker Login</b></td>
    <td align="center"><b>Admin Login</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/e11df995-1b7f-4d11-8e60-3fae100155cf" width="100%" alt="Tracker Login"/></td>
    <td><img src="https://github.com/user-attachments/assets/4b431b68-f4bb-4aee-bf6b-f73d32545ae6" width="100%" alt="Admin Login"/></td>
  </tr>
</table>

👨‍💻 Dashboards

Dedicated dashboards for managing users and monitoring tracking activities.

<table>
  <tr>
    <td align="center"><b>Admin Dashboard</b></td>
    <td align="center"><b>Tracker Dashboard</b></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/4d1ece32-defd-42eb-933a-9767d5befbd7" width="100%" alt="Admin Dashboard"/></td>
    <td><img src="https://github.com/user-attachments/assets/87b2a447-c552-4a59-8958-95c4aa5bc519" width="100%" alt="Tracker Dashboard"/></td>
  </tr>
</table>

📍 Live GPS Tracking

Real-time GPS location monitoring with an interactive map interface.

<p align="center">
  <img src="https://github.com/user-attachments/assets/50ab9976-f9b4-4082-a925-d9eed55e10e4" width="95%" alt="Live GPS Tracking"/>
</p>

📊 Location History

View and monitor previously recorded GPS locations and tracking activity.

<p align="center">
  <img src="https://github.com/user-attachments/assets/ba83df61-c1ac-436c-8c34-646ae10512d9" width="95%" alt="Location History"/>
</p>

🔗 REST API Endpoints
Method| Endpoint| Description
POST| "/api/auth/register"| Register a new user
POST| "/api/auth/login"| Authenticate user
POST| "/api/user/location"| Save user location (if implemented as REST)
GET| "/api/user/location/history"| Retrieve location history
GET| "/api/admin/locations"| Retrieve user locations

WebSocket Configuration

Endpoint| Description
"/ws"| WebSocket connection endpoint
"/app/sendLocation"| Send GPS location
"/topic/locations"| Receive real-time location updates

Note: Verify these endpoint paths against your actual backend implementation before publishing.

🗄️ Database Design

Database: "tracking_db"

Main entities:

- User: Stores user details, login credentials, and roles.
- Location: Stores GPS latitude, longitude, timestamps, and the associated user.

Relationship:

       User
        |
        | One-to-Many
        |
        v
     Location

Each user can have multiple location records, allowing the application to maintain location history.

⚙️ Installation and Setup

Prerequisites

Install the following:

- Java 21
- Maven
- MySQL 8
- Node.js and npm
- Git

1. Clone the Repository

git clone https://github.com/JohnJubli/real-time-tracking-system.git
cd real-time-tracking-system

2. Configure MySQL

Create the database:

CREATE DATABASE tracking_db;

Configure your database username and password in the backend "application.properties" file.

3. Run the Spring Boot Backend

cd backend
mvn spring-boot:run

4. Run the React Frontend

Open another terminal:

cd frontend
npm install
npm run dev

5. Open the Application

Open the frontend URL displayed by Vite in your browser.

Register a user, log in, start GPS tracking, and use the administrator account to monitor live locations.

🔮 Future Enhancements

- Multiple tracker monitoring
- GPS tracking analytics
- Distance and route calculation
- Tracking notifications
- Mobile application integration

🤝 Contribution
Contributions are welcome! Feel free to fork this repository and submit pull requests.

💼 LinkedIn: [John Jubli Simhadri](https://www.linkedin.com/in/john-jubli-simhadri-bb36a02a0/)
