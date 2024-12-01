package com.example.demo.controller;

import com.example.demo.model.Statistics;
import com.example.demo.service.ProjectService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectController {

    private ProjectService projectService() {
        return "kotlin".equalsIgnoreCase(System.getProperty("project.service.impl")) ? kotlinService : javaService;
    }

    private final ProjectService javaService;

    private final ProjectService kotlinService;

    public ProjectController(@Qualifier("javaProjectService") ProjectService javaService,
                             @Qualifier("kotlinProjectService") ProjectService kotlinService) {
        this.javaService = javaService;
        this.kotlinService = kotlinService;
    }


    @GetMapping("/statistics")
    public ResponseEntity<Statistics> getStatistics(@RequestParam(defaultValue = "true") boolean includeCompleted) {
        return ResponseEntity.ok(projectService().calculateStatistics(includeCompleted));
    }
}