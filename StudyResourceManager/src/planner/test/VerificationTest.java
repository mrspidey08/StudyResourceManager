package planner.test;

import planner.dao.CurriculumDAO;
import planner.dao.DatabaseManager;
import planner.model.*;
import planner.service.DiagnosticService;

import java.util.Vector;

public class VerificationTest {
    public static void main(String[] args) {
        System.out.println("Starting Verification Tests...");

        // 1. Test DatabaseManager
        DatabaseManager db = DatabaseManager.getInstance();
        if (db.getConnection() == null) {
            throw new RuntimeException("Database connection is null!");
        }
        System.out.println("[PASS] Database connection established.");

        // 2. Test CurriculumDAO authentication
        CurriculumDAO dao = new CurriculumDAO();
        User admin = dao.authenticate("admin", "admin123");
        if (admin == null || !admin.canModifyCurriculum() || !"CONTRIBUTOR".equals(admin.getRole())) {
            throw new RuntimeException("Admin authentication failed!");
        }
        System.out.println("[PASS] Admin authentication and permissions verified.");

        User student = dao.authenticate("student", "stud123");
        if (student == null || student.canModifyCurriculum() || !"STUDENT".equals(student.getRole())) {
            throw new RuntimeException("Student authentication failed!");
        }
        System.out.println("[PASS] Student authentication and permissions verified.");

        User bad = dao.authenticate("admin", "wrongpassword");
        if (bad != null) {
            throw new RuntimeException("Invalid authentication should return null!");
        }
        System.out.println("[PASS] Invalid authentication properly rejected.");

        // 3. Test Curriculum Retrieval
        Vector<Subject> subjects = dao.getAllSubjects();
        if (subjects.isEmpty()) {
            throw new RuntimeException("Subjects list should not be empty!");
        }
        Subject s1 = subjects.get(0);
        System.out.println("[PASS] Retrieved subject: " + s1.toString());

        Vector<CurriculumModule> modules = dao.getModulesBySubject(s1.getId());
        if (modules.isEmpty()) {
            throw new RuntimeException("Modules list should not be empty!");
        }
        CurriculumModule m1 = modules.get(0);
        System.out.println("[PASS] Retrieved module: " + m1.toString());

        Vector<Topic> topics = dao.getTopicsByModule(m1.getId());
        if (topics.isEmpty()) {
            throw new RuntimeException("Topics list should not be empty!");
        }
        System.out.println("[PASS] Retrieved " + topics.size() + " topics for module 1.");

        // 4. Test DiagnosticService
        DiagnosticService diag = new DiagnosticService(dao);
        double score = diag.calculateReadiness(m1.getId());
        String risk = diag.assessRisk(score);
        System.out.println("[PASS] Module 1 readiness: " + String.format("%.2f%%", score) + ", Risk: " + risk);

        if (modules.size() > 1) {
            CurriculumModule m2 = modules.get(1);
            double score2 = diag.calculateReadiness(m2.getId());
            String risk2 = diag.assessRisk(score2);
            System.out.println("[PASS] Module 2 readiness: " + String.format("%.2f%%", score2) + ", Risk: " + risk2);
        }

        // 5. Test Resources
        Topic t1 = topics.get(0);
        Vector<Resource> res = dao.getResourcesByTopic(t1.getId());
        System.out.println("[PASS] Retrieved " + res.size() + " resources for topic " + t1.getTitle());
        for (Resource r : res) {
            System.out.println("       Resource: " + r.getTitle() + " (" + r.getResourceType() + ") -> " + r.getDisplayLocation());
        }

        // 6. Test Risk Threshold Unit Edge Cases
        if (!"HIGH RISK".equals(diag.assessRisk(0.0)) || !"HIGH RISK".equals(diag.assessRisk(49.9))) {
            throw new RuntimeException("Risk assessment failed for HIGH RISK threshold!");
        }
        if (!"MODERATE RISK".equals(diag.assessRisk(50.0)) || !"MODERATE RISK".equals(diag.assessRisk(74.9))) {
            throw new RuntimeException("Risk assessment failed for MODERATE RISK threshold!");
        }
        if (!"LOW RISK".equals(diag.assessRisk(75.0)) || !"LOW RISK".equals(diag.assessRisk(100.0))) {
            throw new RuntimeException("Risk assessment failed for LOW RISK threshold!");
        }
        // 7. Test Study Plan Report Generation (Processing user parameters & stored data)
        String planReport = diag.generateStudyPlanReport(m1.getId(), m1.getName(), s1.getName(), 14, 3.0);
        if (planReport == null || !planReport.contains("RECOMMENDED STUDY HOURS ALLOCATION")) {
            throw new RuntimeException("Study plan report generation failed!");
        }
        System.out.println("[PASS] Smart Study Plan report successfully generated:\n" + planReport.substring(0, Math.min(240, planReport.length())) + "...\n");

        System.out.println("ALL VERIFICATION TESTS COMPLETED SUCCESSFULLY!");
    }
}
