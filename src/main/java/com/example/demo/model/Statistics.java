package com.example.demo.model;

import java.util.List;
import java.util.Map;

public class Statistics {

    private List<TaskSummary> taskSummaries;
    private Double averageTitleLength;
    private Double averageDescriptionLength;
    private String mostCommonTaskDescription;
    private Map<String, List<TaskSummary>> tasksByCategory;
    private Map<String, Integer> totalHoursByStatus;

    public Statistics(List<TaskSummary> taskSummaries, Double averageTitleLength, Double averageDescriptionLength, String mostCommonTaskDescription, Map<String, List<TaskSummary>> tasksByCategory, Map<String, Integer> totalHoursByStatus) {
        this.taskSummaries = taskSummaries;
        this.averageTitleLength = averageTitleLength;
        this.averageDescriptionLength = averageDescriptionLength;
        this.mostCommonTaskDescription = mostCommonTaskDescription;
        this.tasksByCategory = tasksByCategory;
        this.totalHoursByStatus = totalHoursByStatus;
    }

    public Statistics() {
    }

    // Getters and setters
    public List<TaskSummary> getTaskSummaries() {
        return taskSummaries;
    }

    public void setTaskSummaries(List<TaskSummary> taskSummaries) {
        this.taskSummaries = taskSummaries;
    }

    public Double getAverageTitleLength() {
        return averageTitleLength;
    }

    public void setAverageTitleLength(Double averageTitleLength) {
        this.averageTitleLength = averageTitleLength;
    }

    public Double getAverageDescriptionLength() {
        return averageDescriptionLength;
    }

    public void setAverageDescriptionLength(Double averageDescriptionLength) {
        this.averageDescriptionLength = averageDescriptionLength;
    }

    public String getMostCommonTaskDescription() {
        return mostCommonTaskDescription;
    }

    public void setMostCommonTaskDescription(String mostCommonTaskDescription) {
        this.mostCommonTaskDescription = mostCommonTaskDescription;
    }

    public Map<String, List<TaskSummary>> getTasksByCategory() {
        return tasksByCategory;
    }

    public void setTasksByCategory(Map<String, List<TaskSummary>> tasksByCategory) {
        this.tasksByCategory = tasksByCategory;
    }

    public Map<String, Integer> getTotalHoursByStatus() {
        return totalHoursByStatus;
    }

    public void setTotalHoursByStatus(Map<String, Integer> totalHoursByStatus) {
        this.totalHoursByStatus = totalHoursByStatus;
    }
}
