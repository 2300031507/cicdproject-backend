# Project Purple - Backend

This is the backend for the Project Purple Budget Application. It's built with Spring Boot, Maven, and MySQL.

## Requirements
- Java 17 or higher
- Maven 3.6.3 or higher
- MySQL 8.0 or higher

## Setup Instructions

### 1. Create MySQL Database
First, you need to create a MySQL database for the application:

```sql
CREATE DATABASE project_purple;
CREATE USER 'root'@'localhost' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON project_purple.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

Note: If you want to use a different username and password, make sure to update the `application.properties` file accordingly.

### 2. Build the Project
Navigate to the project directory and build the project using Maven:

```bash
cd project-backend
mvn clean install
```

### 3. Run the Application
You can run the application using Maven or by running the generated JAR file:

Using Maven:
```bash
mvn spring-boot:run
```

Using the JAR file:
```bash
java -jar target/project-backend-0.0.1-SNAPSHOT.jar
```

### 4. Sample Data
The application will automatically load some sample data into the database when it starts for the first time. This includes:
- Two sample users: john.doe@example.com (password: password123) and jane.smith@example.com (password: password456)
- Sample expenses and savings goals for the first user

## API Endpoints

### Authentication
- POST `/api/auth/register` - Register a new user
- POST `/api/auth/login` - Login a user
- POST `/api/auth/logout` - Logout the current user
- GET `/api/auth/me` - Get the current authenticated user

### Budget Management
- GET `/api/budget/categories` - Get all expense categories

#### Expenses (requires authentication)
- GET `/api/budget/expenses` - Get all expenses for the current user
- GET `/api/budget/expenses/{id}` - Get a specific expense by ID
- POST `/api/budget/expenses` - Add a new expense
- PUT `/api/budget/expenses/{id}` - Update an existing expense
- DELETE `/api/budget/expenses/{id}` - Delete an expense

#### Savings Goals (requires authentication)
- GET `/api/budget/savings-goals` - Get all savings goals for the current user
- GET `/api/budget/savings-goals/{id}` - Get a specific savings goal by ID
- POST `/api/budget/savings-goals` - Add a new savings goal
- PUT `/api/budget/savings-goals/{id}` - Update an existing savings goal
- DELETE `/api/budget/savings-goals/{id}` - Delete a savings goal

#### Budget Summary (requires authentication)
- GET `/api/budget/summary` - Get a summary of the user's budget, including total expenses, total savings, and expenses by category

## Technologies Used
- Spring Boot
- Spring Data JPA
- Spring Security
- MySQL
- Maven
- Lombok

## Frontend Integration
The backend is configured to allow CORS requests from the frontend running on `http://localhost:5173`.

## License
This project is for educational purposes only.