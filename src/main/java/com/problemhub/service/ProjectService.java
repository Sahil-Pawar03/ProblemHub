package com.problemhub.service;

import com.problemhub.dao.ProjectDAO;
import com.problemhub.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing student team projects and progress tracking.
 */
@Service
public class ProjectService {

    private final ProjectDAO projectDAO;

    @Autowired
    public ProjectService(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public Optional<Project> getProjectById(int id) {
        return projectDAO.findById(id);
    }

    public List<Project> getProjectsForStudent(int studentUserId, String status) {
        return projectDAO.findByStudentId(studentUserId, status);
    }

    public List<Project> getProjectsForProblem(int problemId) {
        return projectDAO.findByProblemId(problemId);
    }

    public List<Project> getAllProjects() {
        return projectDAO.findAll();
    }

    public int startProject(Project project) {
        return projectDAO.createProject(project);
    }

    public boolean updateProgress(int id, int progress, String status, String repoUrl, String docUrl) {
        return projectDAO.updateProgressAndStatus(id, progress, status, repoUrl, docUrl);
    }

    public int getTotalCount() {
        return projectDAO.countAll();
    }

    public int getStudentProjectCount(int studentUserId) {
        return projectDAO.countByStudentId(studentUserId);
    }

    public int getStudentCompletedCount(int studentUserId) {
        return projectDAO.countCompletedByStudentId(studentUserId);
    }
}
