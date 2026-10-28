# Password Strength Checker 🔐

## About the Project
The Password Strength Checker is a Java-based desktop application that evaluates the strength of a password and helps users create stronger, more secure passwords.

## Features
- Checks password strength.
- Provides feedback on password security.
- Uses Java Swing to create a graphical user interface.
- Connects to a MySQL database using JDBC.
- Stores relevant data in a database.

## Technologies Used
- **Java** – Application development
- **Java Swing** – Graphical user interface
- **MySQL** – Database management
- **JDBC** – Database connectivity
- **Git & GitHub** – Version control

## Requirements
- Java JDK
- MySQL Server
- MySQL Connector/J
- An IDE such as VS Code

## How to Run
1. Clone or download this repository.
2. Create the required MySQL database and configure the database connection.
3. Set the `DB_PASSWORD` environment variable to your MySQL password.
4. Compile and run the Java application with the MySQL Connector/J library included in the classpath.

## Security Note
Never store database passwords directly in the source code. Use environment variables and keep credentials private.

## Screenshot
![Password Strength Checker](screenshots/password-checker.png)

## Author
Developed as a Java project.
