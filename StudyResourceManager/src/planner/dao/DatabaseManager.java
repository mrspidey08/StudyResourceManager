package planner.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private final String DB_URL = "jdbc:sqlite:studymanager.db";

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(DB_URL);
            initializeSchema();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void initializeSchema() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");

            stmt.execute("CREATE TABLE IF NOT EXISTS subjects (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "code TEXT UNIQUE, " +
                    "name TEXT);");

            stmt.execute("CREATE TABLE IF NOT EXISTS modules (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "subject_id INTEGER, " +
                    "module_no INTEGER, " +
                    "name TEXT, " +
                    "FOREIGN KEY(subject_id) REFERENCES subjects(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS topics (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "module_id INTEGER, " +
                    "title TEXT, " +
                    "exam_weightage REAL, " +
                    "has_notes INTEGER, " +
                    "has_pyq INTEGER, " +
                    "FOREIGN KEY(module_id) REFERENCES modules(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS resources (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "topic_id INTEGER, " +
                    "title TEXT, " +
                    "resource_type TEXT, " +
                    "uri_pointer TEXT, " +
                    "FOREIGN KEY(topic_id) REFERENCES topics(id));");

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "role TEXT, " +
                    "full_name TEXT);");

            stmt.execute("INSERT OR IGNORE INTO users (id, username, password, role, full_name) " +
                    "VALUES (1, 'admin', 'admin123', 'CONTRIBUTOR', 'Lead Contributor');");
            stmt.execute("INSERT OR IGNORE INTO users (id, username, password, role, full_name) " +
                    "VALUES (2, 'student', 'stud123', 'STUDENT', 'Enrolled Student');");

            // Seed initial sample curriculum data if subjects table is empty
            try (java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM subjects;")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO subjects (id, code, name) VALUES (1, 'CS201', 'Data Structures & Algorithms');");
                    stmt.execute("INSERT INTO modules (id, subject_id, module_no, name) VALUES (1, 1, 1, 'Linear Data Structures');");
                    stmt.execute("INSERT INTO modules (id, subject_id, module_no, name) VALUES (2, 1, 2, 'Trees & Hierarchical Structures');");
                    stmt.execute("INSERT INTO topics (id, module_id, title, exam_weightage, has_notes, has_pyq) VALUES (1, 1, 'Arrays and Dynamic Lists', 15.0, 1, 1);");
                    stmt.execute("INSERT INTO topics (id, module_id, title, exam_weightage, has_notes, has_pyq) VALUES (2, 1, 'Linked Lists & Applications', 20.0, 1, 1);");
                    stmt.execute("INSERT INTO topics (id, module_id, title, exam_weightage, has_notes, has_pyq) VALUES (3, 1, 'Stack & Queue Implementations', 15.0, 1, 0);");
                    stmt.execute("INSERT INTO topics (id, module_id, title, exam_weightage, has_notes, has_pyq) VALUES (4, 2, 'Binary Search Trees & AVL Trees', 25.0, 1, 0);");
                    stmt.execute("INSERT INTO topics (id, module_id, title, exam_weightage, has_notes, has_pyq) VALUES (5, 2, 'Graph Traversals (BFS & DFS)', 25.0, 0, 0);");
                    stmt.execute("INSERT INTO resources (topic_id, title, resource_type, uri_pointer) VALUES (1, 'Array Data Structure Documentation', 'WEB_URL', 'https://en.wikipedia.org/wiki/Array_data_structure');");
                    stmt.execute("INSERT INTO resources (topic_id, title, resource_type, uri_pointer) VALUES (2, 'Linked List Visualization Tool', 'WEB_URL', 'https://visualgo.net/en/list');");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}