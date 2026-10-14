package com.problemhub;

import com.problemhub.model.Problem;
import com.problemhub.model.RecommendationDTO;
import com.problemhub.model.User;
import com.problemhub.service.AuthService;
import com.problemhub.service.ProblemService;
import com.problemhub.service.RecommendationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Automated test suite implementing Test Cases TC001 - TC008 from
 * Section 7.2 of the ProblemHub Academic Mini-Project Report.
 * Demonstrates test automation in 5th-sem viva presentation.
 */
@SpringBootTest
class ProblemHubApplicationTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private ProblemService problemService;

    @Autowired
    private RecommendationService recommendationService;

    @Test
    @DisplayName("Context Loads - Spring Boot starts successfully")
    void contextLoads() {
        assertNotNull(authService, "AuthService should be injected");
        assertNotNull(problemService, "ProblemService should be injected");
        assertNotNull(recommendationService, "RecommendationService should be injected");
    }

    @Test
    @DisplayName("TC001 & TC002: Authenticate valid user and reject invalid password")
    void testAuthentication() {
        // Test valid student demo account
        Optional<User> validUser = authService.authenticate("rahul.student@problemhub.com", "password123");
        assertTrue(validUser.isPresent(), "Valid student should authenticate successfully");
        assertEquals("STUDENT", validUser.get().getRole(), "User role should be STUDENT");

        // Test invalid password rejection
        Optional<User> invalidUser = authService.authenticate("rahul.student@problemhub.com", "wrong_password");
        assertFalse(invalidUser.isPresent(), "Invalid password should be rejected");
    }

    @Test
    @DisplayName("TC003: Search problems by keyword title")
    void testProblemSearchByTitle() {
        List<Problem> results = problemService.searchProblems("Waste", null, null, null, null, null, "APPROVED", null);
        assertNotNull(results, "Search results should not be null");
        assertFalse(results.isEmpty(), "Search for 'Waste' should return matching problem statements");
        assertTrue(results.get(0).getTitle().toLowerCase().contains("waste"), "Returned problem title should contain keyword");
    }

    @Test
    @DisplayName("TC004: Filter problems by difficulty level")
    void testProblemFilterByDifficulty() {
        List<Problem> intermediateProblems = problemService.searchProblems(null, null, null, "Intermediate", null, null, "APPROVED", null);
        assertNotNull(intermediateProblems, "Filter results should not be null");
        for (Problem p : intermediateProblems) {
            assertEquals("Intermediate", p.getDifficulty(), "All filtered problems should be Intermediate");
        }
    }

    @Test
    @DisplayName("TC006: User Role checks for authorization")
    void testUserRoleAuthorization() {
        User student = new User();
        student.setRole("STUDENT");
        assertFalse(student.isAdmin(), "Student should NOT have admin privileges");
        assertFalse(student.isFaculty(), "Student should NOT have faculty privileges");

        User admin = new User();
        admin.setRole("ADMIN");
        assertTrue(admin.isAdmin(), "Admin should have admin privileges");

        User faculty = new User();
        faculty.setRole("FACULTY");
        assertTrue(faculty.isFaculty(), "Faculty should have faculty privileges");
    }

    @Test
    @DisplayName("TC007: Content-Based recommendation engine calculates match percentage")
    void testContentBasedRecommendations() {
        User testStudent = new User();
        testStudent.setId(1);
        testStudent.setFullName("Test Student");
        testStudent.setRole("STUDENT");
        testStudent.setSkills("Java, MySQL, Leaflet");
        testStudent.setInterests("Environment, Smart City");
        testStudent.setYearOfStudy("3rd Year");

        List<RecommendationDTO> recommendations = recommendationService.getRecommendationsForUser(testStudent, 5);
        assertNotNull(recommendations, "Recommendations list should not be null");
        assertFalse(recommendations.isEmpty(), "Should return recommendations for student with registered skills");

        RecommendationDTO topMatch = recommendations.get(0);
        assertTrue(topMatch.getMatchPercentage() >= 40, "Top recommendation match score should be >= 40%");
        assertNotNull(topMatch.getExplanation(), "Recommendation explanation should be present");
        assertTrue(topMatch.getExplanation().length() > 10, "Explanation should describe matching attributes");
    }

    @Test
    @DisplayName("TC008: Controlled response for non-existent problem ID")
    void testInvalidProblemIdHandling() {
        Optional<Problem> nonExistent = problemService.getProblemDetails(999999, null);
        assertTrue(nonExistent.isEmpty(), "Querying non-existent problem ID should return empty Optional without crashing");
    }
}
