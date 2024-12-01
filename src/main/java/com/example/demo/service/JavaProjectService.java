package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("javaProjectService")
public class JavaProjectService implements ProjectService {

    private final ProjectRepository projectRepository;

    public JavaProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public Statistics calculateStatistics(boolean includeCompleted) {
        List<Project> projects = projectRepository.findAll();

        if (projects.isEmpty()) {
            return new Statistics();
        }

        // Filter projects based on includeCompleted parameter
        List<Project> filteredProjects = includeCompleted
                ? projects
                : projects.stream().filter(p -> p.getStatus() != ProjectStatus.COMPLETED).toList();

        List<TaskSummary> taskSummaries = filteredProjects.stream()
                .flatMap(project -> project.getTasks().stream())
                .map(task -> new TaskSummary(task.getTitle(), task.getDescription(), task.getEstimatedHours()))
                .toList();

        double averageTitleLength = taskSummaries.stream()
                .mapToInt(summary -> summary.getTitle().length())
                .average()
                .orElse(0);

        double averageDescriptionLength = taskSummaries.stream()
                .filter(summary -> summary.getDescription() != null)
                .mapToInt(summary -> summary.getDescription().length())
                .average()
                .orElse(0);

        String mostCommonTaskDescription = taskSummaries.stream()
                .map(TaskSummary::getDescription)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(desc -> desc, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Map<String, List<TaskSummary>> tasksByCategory = taskSummaries.stream()
                .collect(Collectors.groupingBy(summary -> {
                    if (summary.getHours() == null) return "Uncategorized";
                    if (summary.getHours() < 20) return "Small";
                    if (summary.getHours() <= 40) return "Medium";
                    return "Large";
                }));

        Map<String, Integer> totalHoursByStatus = filteredProjects.stream()
                .collect(Collectors.groupingBy(
                        project -> project.getStatus().name(),
                        Collectors.summingInt(project -> project.getTasks().stream()
                                .mapToInt(task -> Optional.ofNullable(task.getEstimatedHours()).orElse(0))
                                .sum()
                        )
                ));

        Statistics statistics = new Statistics();
        statistics.setTaskSummaries(taskSummaries);
        statistics.setAverageTitleLength(averageTitleLength);
        statistics.setAverageDescriptionLength(averageDescriptionLength);
        statistics.setMostCommonTaskDescription(mostCommonTaskDescription);
        statistics.setTasksByCategory(tasksByCategory);
        statistics.setTotalHoursByStatus(totalHoursByStatus);

        return statistics;
    }
}
