package com.example.demo.model;

public class TaskSummary {

    private String title;
    private String description;
    private Integer hours;

    public TaskSummary(String title, String description, Integer hours) {
        this.title = title;
        this.description = description;
        this.hours = hours;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getHours() {
        return hours;
    }
}
