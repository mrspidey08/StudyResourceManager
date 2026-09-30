package planner.dao;

import planner.model.*;
import planner.exception.CurriculumException;

import java.sql.*;
import java.util.Vector;

public class CurriculumDAO {
    private Connection getConnection() {
        return DatabaseManager.getInstance().getConnection();
    }

    public void addSubject(String code, String name) throws CurriculumException {
        String sql = "INSERT INTO subjects (code, name) VALUES (?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CurriculumException("Failed to add subject: " + e.getMessage(), e);
        }
    }

    public Vector<Subject> getAllSubjects() {
        Vector<Subject> list = new Vector<Subject>();
        String sql = "SELECT * FROM subjects ORDER BY code";
        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Subject(rs.getInt("id"), rs.getString("code"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addModule(int subjectId, int moduleNo, String name) throws CurriculumException {
        String sql = "INSERT INTO modules (subject_id, module_no, name) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, subjectId);
            pstmt.setInt(2, moduleNo);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CurriculumException("Failed to insert module: " + e.getMessage(), e);
        }
    }

    public Vector<CurriculumModule> getModulesBySubject(int subjectId) {
        Vector<CurriculumModule> list = new Vector<CurriculumModule>();
        String sql = "SELECT * FROM modules WHERE subject_id = ? ORDER BY module_no";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, subjectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new CurriculumModule(rs.getInt("id"), rs.getInt("subject_id"), rs.getInt("module_no"),
                            rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addTopic(int moduleId, String title, double weightage, boolean notes, boolean pyq)
            throws CurriculumException {
        String sql = "INSERT INTO topics (module_id, title, exam_weightage, has_notes, has_pyq) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, moduleId);
            pstmt.setString(2, title);
            pstmt.setDouble(3, weightage);
            pstmt.setInt(4, notes ? 1 : 0);
            pstmt.setInt(5, pyq ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CurriculumException("Failed to add topic: " + e.getMessage(), e);
        }
    }

    public Vector<Topic> getTopicsByModule(int moduleId) {
        Vector<Topic> list = new Vector<Topic>();
        String sql = "SELECT * FROM topics WHERE module_id = ? ORDER BY id";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, moduleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Topic(
                            rs.getInt("id"),
                            rs.getInt("module_id"),
                            rs.getString("title"),
                            rs.getDouble("exam_weightage"),
                            rs.getInt("has_notes") == 1,
                            rs.getInt("has_pyq") == 1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addResource(int topicId, String title, String type, String uri) throws CurriculumException {
        String sql = "INSERT INTO resources (topic_id, title, resource_type, uri_pointer) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, topicId);
            pstmt.setString(2, title);
            pstmt.setString(3, type);
            pstmt.setString(4, uri);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CurriculumException("Failed to index resource: " + e.getMessage(), e);
        }
    }

    public Vector<Resource> getResourcesByTopic(int topicId) {
        Vector<Resource> list = new Vector<Resource>();
        String sql = "SELECT * FROM resources WHERE topic_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, topicId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String type = rs.getString("resource_type");
                    String uri = rs.getString("uri_pointer");
                    if ("WEB_URL".equalsIgnoreCase(type)) {
                        list.add(new WebUrlResource(id, topicId, title, uri));
                    } else {
                        list.add(new LocalFileResource(id, topicId, title, uri));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteTopic(int topicId) throws CurriculumException {
        String sqlResources = "DELETE FROM resources WHERE topic_id = ?";
        String sqlTopic = "DELETE FROM topics WHERE id = ?";
        try {
            try (PreparedStatement pstmt = getConnection().prepareStatement(sqlResources)) {
                pstmt.setInt(1, topicId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = getConnection().prepareStatement(sqlTopic)) {
                pstmt.setInt(1, topicId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new CurriculumException("Failed to delete topic: " + e.getMessage(), e);
        }
    }

    public void deleteResource(int resourceId) throws CurriculumException {
        String sql = "DELETE FROM resources WHERE id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, resourceId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new CurriculumException("Failed to delete resource: " + e.getMessage(), e);
        }
    }

    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    int id = rs.getInt("id");
                    String fullName = rs.getString("full_name");
                    if ("CONTRIBUTOR".equalsIgnoreCase(role)) {
                        return new Contributor(id, username, fullName);
                    } else {
                        return new Student(id, username, fullName);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}