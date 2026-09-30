package planner.service;

import planner.dao.CurriculumDAO;
import planner.model.CurriculumModule;
import planner.model.Topic;

import java.util.List;

/**
 * Service responsible for analyzing curriculum readiness and assessing exam risk.
 */
public class DiagnosticService {
    private final CurriculumDAO dao;

    public DiagnosticService() {
        this.dao = new CurriculumDAO();
    }

    public DiagnosticService(CurriculumDAO dao) {
        this.dao = dao;
    }

    /**
     * Calculates the readiness score (percentage 0.0 to 100.0) for a given curriculum module.
     * Each topic's readiness is assessed based on availability of verified notes (50%)
     * and solved previous year questions (PYQs) (50%), weighted by the topic's exam weightage.
     *
     * @param moduleId the unique ID of the module
     * @return readiness score from 0.0 to 100.0
     */
    public double calculateReadiness(int moduleId) {
        if (dao == null) {
            return 0.0;
        }

        List<Topic> topics = dao.getTopicsByModule(moduleId);
        if (topics == null || topics.isEmpty()) {
            return 0.0;
        }

        double totalWeightage = 0.0;
        double coveredWeightage = 0.0;

        for (Topic topic : topics) {
            double weight = topic.getExamWeightage();
            totalWeightage += weight;

            double topicScore = 0.0;
            if (topic.hasNotes()) {
                topicScore += 0.5;
            }
            if (topic.hasPyq()) {
                topicScore += 0.5;
            }

            coveredWeightage += topicScore * weight;
        }

        if (totalWeightage > 0.0) {
            double percentage = (coveredWeightage / totalWeightage) * 100.0;
            return Math.min(100.0, Math.max(0.0, percentage));
        }

        // Fallback for modules where weightages are not assigned (or all zero):
        // Calculate unweighted average across topics.
        double totalScore = 0.0;
        for (Topic topic : topics) {
            double topicScore = 0.0;
            if (topic.hasNotes()) {
                topicScore += 0.5;
            }
            if (topic.hasPyq()) {
                topicScore += 0.5;
            }
            totalScore += topicScore;
        }

        double percentage = (totalScore / topics.size()) * 100.0;
        return Math.min(100.0, Math.max(0.0, percentage));
    }

    /**
     * Overloaded method to calculate readiness for a CurriculumModule instance.
     *
     * @param module the CurriculumModule instance
     * @return readiness score from 0.0 to 100.0
     */
    public double calculateReadiness(CurriculumModule module) {
        if (module == null) {
            return 0.0;
        }
        return calculateReadiness(module.getId());
    }

    /**
     * Assesses exam preparation risk based on the calculated readiness percentage score.
     *
     * @param score readiness score between 0.0 and 100.0
     * @return "HIGH RISK", "MODERATE RISK", or "LOW RISK"
     */
    public String assessRisk(double score) {
        if (score < 50.0) {
            return "HIGH RISK";
        } else if (score < 75.0) {
            return "MODERATE RISK";
        } else {
            return "LOW RISK";
        }
    }

    /**
     * Assesses exam preparation risk directly for a module ID.
     *
     * @param moduleId the unique ID of the module
     * @return "HIGH RISK", "MODERATE RISK", or "LOW RISK"
     */
    public String assessRisk(int moduleId) {
        return assessRisk(calculateReadiness(moduleId));
    }

    /**
     * Generates a comprehensive study schedule and diagnostic plan report.
     * Processes stored curriculum metrics alongside user-provided preparation inputs.
     *
     * @param moduleId the unique ID of the module
     * @param moduleName the display name of the module
     * @param subjectTitle the display title of the subject
     * @param daysRemaining number of days remaining until exam
     * @param dailyStudyHours hours available per day for study
     * @return Formatted audit and preparation report string
     */
    public String generateStudyPlanReport(int moduleId, String moduleName, String subjectTitle, int daysRemaining, double dailyStudyHours) {
        List<Topic> topics = dao != null ? dao.getTopicsByModule(moduleId) : new java.util.ArrayList<>();
        double readiness = calculateReadiness(moduleId);
        String risk = assessRisk(readiness);

        double totalAvailableHours = daysRemaining * dailyStudyHours;
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("             EXAM READINESS AUDIT & SMART REVISION SCHEDULE             \n");
        sb.append("========================================================================\n\n");
        sb.append(String.format("Subject  : %s\n", subjectTitle != null ? subjectTitle : "N/A"));
        sb.append(String.format("Module   : %s (ID: %d)\n", moduleName != null ? moduleName : "N/A", moduleId));
        sb.append(String.format("Readiness: %.1f%%  -->  Risk Status: %s\n", readiness, risk));
        sb.append(String.format("Timeframe: %d Days @ %.1f hrs/day  -->  Total Revision Budget: %.1f Hours\n\n",
                daysRemaining, dailyStudyHours, totalAvailableHours));

        sb.append("------------------------------------------------------------------------\n");
        sb.append("SECTION 1: TOPIC AUDIT & STATUS BREAKDOWN\n");
        sb.append("------------------------------------------------------------------------\n");

        if (topics.isEmpty()) {
            sb.append("No topics found for this module. Please add topics to generate allocation.\n");
            return sb.toString();
        }

        double totalUrgencies = 0.0;
        double[] urgencies = new double[topics.size()];
        int missingNotesCount = 0;
        int missingPyqCount = 0;

        for (int i = 0; i < topics.size(); i++) {
            Topic t = topics.get(i);
            boolean notes = t.hasNotes();
            boolean pyq = t.hasPyq();
            if (!notes) missingNotesCount++;
            if (!pyq) missingPyqCount++;

            double gapFactor = 1.0;
            if (!notes) gapFactor += 0.5;
            if (!pyq) gapFactor += 0.5;

            double weight = t.getExamWeightage() > 0 ? t.getExamWeightage() : 10.0;
            urgencies[i] = weight * gapFactor;
            totalUrgencies += urgencies[i];

            String statusIndicator = (notes && pyq) ? "[COMPLETE] " : "[PENDING]  ";
            sb.append(String.format("%s Topic #%d: %-32s | Wt: %4.1f%% | Notes: %-3s | PYQ: %-3s\n",
                    statusIndicator,
                    t.getId(),
                    t.getTitle().length() > 32 ? t.getTitle().substring(0, 29) + "..." : t.getTitle(),
                    t.getExamWeightage(),
                    notes ? "YES" : "NO",
                    pyq ? "YES" : "NO"));
        }

        sb.append(String.format("\nSummary: %d Topics total | %d Missing Notes | %d Missing PYQs\n\n",
                topics.size(), missingNotesCount, missingPyqCount));

        sb.append("------------------------------------------------------------------------\n");
        sb.append("SECTION 2: RECOMMENDED STUDY HOURS ALLOCATION (PRIORITIZED)\n");
        sb.append("------------------------------------------------------------------------\n");

        for (int i = 0; i < topics.size(); i++) {
            Topic t = topics.get(i);
            double allocatedHours = totalUrgencies > 0 ? (urgencies[i] / totalUrgencies) * totalAvailableHours : (totalAvailableHours / topics.size());
            String priority;
            if (!t.hasNotes() || !t.hasPyq()) {
                priority = t.getExamWeightage() >= 20.0 ? "CRITICAL" : "HIGH";
            } else if (t.getExamWeightage() >= 20.0) {
                priority = "MEDIUM";
            } else {
                priority = "LOW";
            }

            sb.append(String.format("• %-35s : %5.1f hrs  [%s Priority]\n",
                    t.getTitle().length() > 35 ? t.getTitle().substring(0, 32) + "..." : t.getTitle(),
                    allocatedHours,
                    priority));
        }

        sb.append("\n------------------------------------------------------------------------\n");
        sb.append("SECTION 3: ACTIONABLE PREPARATION DIRECTIVES\n");
        sb.append("------------------------------------------------------------------------\n");
        if (missingNotesCount > 0) {
            sb.append("1. Acquire/complete verified lecture notes for pending topics first.\n");
        }
        if (missingPyqCount > 0) {
            sb.append("2. Practice past university questions (PYQs) under timed exam conditions.\n");
        }
        if (missingNotesCount == 0 && missingPyqCount == 0) {
            sb.append("1. All reference notes and past questions are logged. Focus on rapid recall and timed mocks.\n");
        }
        sb.append("3. Review high-weightage topics (>=20%) at least twice during the final 3 days.\n");
        sb.append("========================================================================\n");

        return sb.toString();
    }
}
