# Hospitality Management System

A Java Swing application for managing hotels, rooms, guests, and reservations with MySQL database backend.

## Features

- Hotel management (add, edit, delete, view)
- Room management (add, edit, delete, view)
- Guest management (add, edit, delete, view)
- Reservation management (create, edit, cancel, view)
- Room availability checking

## Prerequisites

- Java JDK 8 or later
- MySQL Server
- MySQL Connector/J (included in most Java IDEs)

## Setup

1. Create the database by running the SQL script in `database/hospitality_db.sql`
2. Update the database connection details in `DatabaseConnector.java` if needed
3. Compile and run the application from `MainFrame.java`

## Usage

1. Launch the application
2. Use the tabs to navigate between different management sections
3. Use the buttons to perform CRUD operations

## Project Structure

- `database/`: Contains SQL scripts for database setup
- `src/entities/`: Entity classes representing database tables
- `src/dao/`: Data Access Objects for database operations
- `src/gui/`: Swing GUI components