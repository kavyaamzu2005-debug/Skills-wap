# SkillSwap – Skill Exchange Web Application

SkillSwap is a web-based skill exchange platform that connects people who want to learn with people who are willing to teach. Users can create accounts, manage their profiles, add skills, discover suitable skill partners, and send connection requests.

## Features

- User registration
- User login and authentication
- Session-based user identification
- Profile management
- Add and manage skills
- Find suitable skill matches
- Send connection requests
- View connections
- Accept or reject connection requests
- Dynamic homepage statistics
  - Skills
  - Registered Learners
  - Successful Skill Swaps

## Technology Stack

### Frontend
- HTML5
- CSS3
- JavaScript

### Backend
- Java
- Java Servlets
- JDBC

### Database
- MySQL
- Relational database design

### Server
- Apache Tomcat 10.1

### Development Tools
- Visual Studio Code
- Git
- GitHub

## Application Flow

```text
User
  ↓
HTML / CSS / JavaScript
  ↓
Java Servlet
  ↓
JDBC
  ↓
MySQL
  ↓
Response
  ↓
Frontend
```

## Main Modules

### Registration
Users submit their name, email, and password through the registration form. The registration servlet processes the request and stores the user information in MySQL.

### Login
Users log in using their registered credentials. On successful authentication, the application creates a session so the logged-in user can be identified across requests.

### Profile
Users can view and manage their profile information.

### Skills
Users can add skills and use the application to discover people based on skills they want to learn or share.

### Matching
The matching feature helps users discover suitable skill partners.

### Connections
A user can send a connection request to another user. New requests are initially stored with a `pending` status.

When the receiver accepts the request, the status changes to `connected`.

```text
Connect
   ↓
pending
   ↓
Accept
   ↓
connected
```

## Database

The application uses a MySQL database named `skillswap`.

Main tables:

```text
users
skills
connections
```

### Users
Stores registered user information.

### Skills
Stores skill-related information used by the application.

### Connections
Stores relationships between users.

Important fields include:

```text
id
sender_id
receiver_id
status
```

The `status` field is used to represent the state of a connection request, such as:

```text
pending
connected
```

## Dynamic Homepage Statistics

The homepage statistics are retrieved from the database instead of being permanently hardcoded.

### Learners

The number of registered learners is calculated from the `users` table.

```sql
SELECT COUNT(*) FROM users;
```

### Skill Swaps

Successful skill swaps are counted from connected relationships.

```sql
SELECT COUNT(*)
FROM connections
WHERE status = 'connected';
```

The frontend requests the statistics through JavaScript, the existing servlet retrieves the values from MySQL, and JavaScript updates the corresponding HTML elements.

## Servlet Endpoints

The application uses Java Servlets mapped to URL patterns such as:

/connect
/connections
/connection-requests
/accept-request


Examples:

- `/connect` → creates a connection request
- `/connections` → loads user connections and can provide homepage statistics
- `/connection-requests` → loads pending requests
- `/accept-request` → accepts a pending connection

## JDBC Flow

The backend communicates with MySQL using JDBC.


MySQL Driver
     ↓
Connection
     ↓
PreparedStatement
     ↓
SQL Query
     ↓
ResultSet / Update


`PreparedStatement` is used for parameterized SQL queries.

## Project Structure

A simplified structure is:


SkillSwap
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── skillswap
│       │           ├── RegisterServlet.java
│       │           ├── LoginServlet.java
│       │           ├── ConnectServlet.java
│       │           ├── ConnectionsServlet.java
│       │           ├── ConnectionRequestsServlet.java
│       │           ├── AcceptServlet.java
│       │           └── ...
│       │
│       └── webapp
│           ├── index.html
│           ├── register.html
│           ├── login.html
│           ├── dashboard.html
│           ├── profile.html
│           ├── css
│           │   └── style.css
│           ├── js
│           └── WEB-INF
│
└── .gitignore


## Requirements

Before running the project, install/configure:

- Java 17
- Apache Tomcat 10.1
- MySQL
- MySQL Connector/J
- A Java-compatible development environment such as VS Code

## Database Configuration

Create the MySQL database:

CREATE DATABASE skillswap;

Create/configure the required tables (`users`, `skills`, and `connections`) according to the project schema.

## Running the Project

1. Start MySQL.
2. Make sure the `skillswap` database is available.
3. Configure the MySQL Connector/J driver for the application.
4. Compile the Java servlet classes.
5. Deploy the web application to Apache Tomcat.
6. Start Tomcat.
7. Open:
http://localhost:8080/webapp/index.html


## GitHub

The project can be maintained using Git and GitHub for version control.

Typical commands:

git init
git add .
git commit -m "Complete SkillSwap web application"
git branch -M main
git remote add origin <YOUR_GITHUB_REPOSITORY_URL>
git push -u origin main


## Future Enhancements

- Password hashing and stronger authentication
- Secure environment-based database configuration
- Better skill recommendation and matching
- Search and filtering
- User profile images
- Notifications
- Improved validation and error handling
- Responsive UI improvements




