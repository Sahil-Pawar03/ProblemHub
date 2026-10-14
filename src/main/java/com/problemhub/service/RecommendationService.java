package com.problemhub.service;

import com.problemhub.dao.ProblemDAO;
import com.problemhub.model.Problem;
import com.problemhub.model.RecommendationDTO;
import com.problemhub.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Score-based recommendation engine for student project discovery.
 * Designed for simplicity, performance, and clear viva explanation:
 * - Technology match: +30
 * - Domain match: +25
 * - Difficulty match: +20
 * - Duration match: +15
 * - Project type match: +10
 */
@Service
public class RecommendationService {

    private final ProblemDAO problemDAO;

    @Autowired
    public RecommendationService(ProblemDAO problemDAO) {
        this.problemDAO = problemDAO;
    }

    /**
     * Calculates recommendation scores for all approved problems based on user skills,
     * interests, and study year, returning the top matches with human-readable explanations.
     */
    public List<RecommendationDTO> getRecommendationsForUser(User user, int limit) {
        List<Problem> problems = problemDAO.findAllApproved();
        List<RecommendationDTO> recommendations = new ArrayList<>();

        if (user == null || problems.isEmpty()) {
            return recommendations;
        }

        // Parse student skills into normalized lowercase set
        Set<String> userSkills = extractTokens(user.getSkills());
        // Parse student interests into normalized lowercase set
        Set<String> userInterests = extractTokens(user.getInterests());

        String userYear = user.getYearOfStudy() != null ? user.getYearOfStudy().toLowerCase() : "";

        for (Problem problem : problems) {
            int score = 0;
            List<String> matchedTechList = new ArrayList<>();
            List<String> reasonParts = new ArrayList<>();

            // 1. Skill Overlap (Content-Based Filtering: up to 40%)
            String probTechStr = (problem.getSuggestedTech() != null ? problem.getSuggestedTech() : "") + " " +
                                 (problem.getRequiredSkills() != null ? problem.getRequiredSkills() : "");
            Set<String> probTechTokens = extractTokens(probTechStr);

            for (String skill : userSkills) {
                for (String pTech : probTechTokens) {
                    if (pTech.contains(skill) || skill.contains(pTech)) {
                        matchedTechList.add(capitalize(skill));
                        break;
                    }
                }
            }

            int distinctMatches = new HashSet<>(matchedTechList).size();
            if (distinctMatches >= 2) {
                score += 40;
                reasonParts.add("strong match for your " + String.join(", ", new HashSet<>(matchedTechList)) + " skills (40%)");
            } else if (distinctMatches == 1) {
                score += 25;
                reasonParts.add("matches your " + matchedTechList.get(0) + " skills (25%)");
            }

            // 2. Domain Affinity (Content-Based Filtering: up to 30%)
            boolean domainMatched = false;
            if (problem.getDomain() != null) {
                String probDomain = problem.getDomain().toLowerCase();
                for (String interest : userInterests) {
                    if (probDomain.contains(interest) || interest.contains(probDomain)) {
                        domainMatched = true;
                        score += 30;
                        reasonParts.add("aligns with your domain interest in " + problem.getDomain() + " (30%)");
                        break;
                    }
                }
            }

            // 3. Academic Level & Difficulty Fit (up to 20%)
            String difficulty = problem.getDifficulty() != null ? problem.getDifficulty() : "";
            if (userYear.contains("3rd") && "Intermediate".equalsIgnoreCase(difficulty)) {
                score += 20;
                reasonParts.add("tailored intermediate difficulty for 3rd year (20%)");
            } else if (userYear.contains("4th") && "Advanced".equalsIgnoreCase(difficulty)) {
                score += 20;
                reasonParts.add("advanced scope suited for final year capstone (20%)");
            } else if ((userYear.contains("1st") || userYear.contains("2nd")) && "Beginner".equalsIgnoreCase(difficulty)) {
                score += 20;
                reasonParts.add("beginner-friendly scope (20%)");
            } else if ("Intermediate".equalsIgnoreCase(difficulty)) {
                score += 15;
            } else {
                score += 10;
            }

            // 4. Semester Project Duration Feasibility (up to 10%)
            if ("1 Month".equalsIgnoreCase(problem.getDuration()) || "2–3 Months".equalsIgnoreCase(problem.getDuration())) {
                score += 10;
                reasonParts.add("ideal semester timeframe (10%)");
            } else {
                score += 5;
            }

            // Only recommend if there is a meaningful match (score >= 35%)
            if (score >= 35) {
                String explanation = "Recommended because it " + (reasonParts.isEmpty() ? "aligns with your academic profile" : String.join(", and ", reasonParts)) + ".";
                RecommendationDTO dto = new RecommendationDTO(problem, score, explanation);
                dto.setMatchedSkills(new ArrayList<>(new HashSet<>(matchedTechList)));
                dto.setDomainMatched(domainMatched);
                recommendations.add(dto);
            }
        }

        // Sort descending by match score
        recommendations.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        if (recommendations.size() > limit) {
            return recommendations.subList(0, limit);
        }
        return recommendations;
    }

    private Set<String> extractTokens(String str) {
        Set<String> set = new HashSet<>();
        if (str == null) return set;
        String[] parts = str.toLowerCase().split("[,;|/\\s]+");
        for (String p : parts) {
            String clean = p.replaceAll("[^a-z0-9#+]", "").trim();
            if (clean.length() >= 2) {
                set.add(clean);
            }
        }
        return set;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
