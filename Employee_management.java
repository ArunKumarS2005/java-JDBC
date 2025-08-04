package employee_management;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Employee_management {
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/employee_management";
    private static final String USER        = "root";
    // Be sure you configure secure password handling in real apps.
    private static final String PASS        = "Arunkumar@2005";

    private Connection connection;
    private Scanner scanner;

    public Employee_management() {
        try {
            Class.forName(JDBC_DRIVER);
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database successfully!");
            scanner = new Scanner(System.in);
        }
        catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            System.err.println("JDBC Driver not found. Make sure mysql-connector‑j‑x.x.x.jar is in your classpath.");
        }
        catch (SQLException se) {
            se.printStackTrace();
            System.err.println("Database connection failed. Check your DB_URL, USER, PASS, and ensure MySQL is running.");
        }
    }

    public void start() {
        if (connection == null) {
            System.err.println("Application cannot start without a database connection.");
            return;
        }
        int choice;
        do {
            displayMenu();
            System.out.print("Enter your choice: ");
            while (!scanner.hasNextInt()) {
                scanner.nextLine();
                System.out.print("  Please enter a number: ");
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: addEmployee();     break;
                case 2: viewAllDetails();  break;
                case 3: updateDetails();   break;
                case 4: deleteDetails();   break;
                case 5:
                    System.out.println("Exiting Employee Management Application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\n------------------------------------\n");
        } while (choice != 5);

        closeResources();
    }

    private void displayMenu() {
        System.out.println("--- Simple Employee Management ---");
        System.out.println("1. Add Employee Details");
        System.out.println("2. View All Employees");
        System.out.println("3. Update Employee Details");
        System.out.println("4. Delete Employee Details");
        System.out.println("5. Exit");
    }

    // CREATE
    private void addEmployee() {
        System.out.println("\n--- Add New Employee Details ---");
        System.out.print("Enter Employee name: ");
        String employee_name = scanner.nextLine();

        System.out.print("Enter Role: ");
        String role = scanner.nextLine();

        System.out.print("Enter Salary: ");
        while (!scanner.hasNextDouble()) {
            scanner.nextLine();
            System.out.print("  Please enter a valid salary (e.g. 35000.50): ");
        }
        double salary = scanner.nextDouble();
        scanner.nextLine();

        String sql = "INSERT INTO employee_details (employee_name, role, salary) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, employee_name);
            pstmt.setString(2, role);
            pstmt.setDouble(3, salary);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Employee '" + employee_name + "' added successfully.");
            } else {
                System.out.println("Failed to add employee.");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    // READ
    private void viewAllDetails() {
        System.out.println("\n--- All Employees ---");
        String sql = "SELECT id, employee_name, role, salary FROM employee_details";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (!rs.isBeforeFirst()) {
                System.out.println("No employees found.");
                return;
            }
            while (rs.next()) {
                int    id = rs.getInt("id");
                String name = rs.getString("employee_name");
                String role = rs.getString("role");
                double sal  = rs.getDouble("salary");
                System.out.printf("ID: %d, Name: %s, Role: %s, Salary: %.2f%n",
                                  id, name, role, sal);
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    // UPDATE
    private void updateDetails() {
        System.out.println("\n--- Update Employee Details ---");
        System.out.print("Enter Employee ID to update: ");
        while (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.print("  Please enter a valid numeric ID: ");
        }
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter new Role: ");
        String newRole = scanner.nextLine();

        System.out.print("Enter new Salary: ");
        while (!scanner.hasNextDouble()) {
            scanner.nextLine();
            System.out.print("  Please enter a valid salary: ");
        }
        double newSalary = scanner.nextDouble();
        scanner.nextLine();

        String sql = "UPDATE employee_details SET role = ?, salary = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newRole);
            pstmt.setDouble(2, newSalary);
            pstmt.setInt(3, id);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Employee with ID " + id + " updated successfully.");
            } else {
                System.out.println("No Employee found with ID " + id + ".");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    // DELETE
    private void deleteDetails() {
        System.out.println("\n--- Delete Employee ---");
        System.out.print("Enter Employee ID to delete: ");
        while (!scanner.hasNextInt()) {
            scanner.nextLine();
            System.out.print("  Please enter a valid numeric ID: ");
        }
        int id = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM employee_details WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                System.out.println("Employee with ID " + id + " deleted successfully.");
            } else {
                System.out.println("No Employee found with ID " + id + ".");
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private void closeResources() {
        try {
            if (scanner != null) scanner.close();
            if (connection != null && !connection.isClosed()) connection.close();
            System.out.println("Resources closed successfully.");
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Employee_management().start();
    }
}

