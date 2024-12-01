package com.example.demo.service

import com.example.demo.model.*
import com.example.demo.repository.ProjectRepository
import org.springframework.stereotype.Service

// Extension function for List<TaskSummary>
fun List<TaskSummary>.categorizeTasks(): Map<String, List<TaskSummary>> {
    return this.groupBy {
        when {
            it.hours == null -> "Uncategorized"
            it.hours < 20 -> "Small"
            it.hours <= 40 -> "Medium"
            else -> "Large"
        }
    }
}
fun ProjectStatus.isFinished(): Boolean {
    return when (this) {
        ProjectStatus.COMPLETED -> true
        ProjectStatus.PLANNED, ProjectStatus.IN_PROGRESS -> false
    }
}

@Service("kotlinProjectService")
class KotlinProjectService(private val projectRepository: ProjectRepository) : ProjectService {

    override fun calculateStatistics(includeCompleted: Boolean): Statistics {
        val projects = projectRepository.findAll()

        if (projects.isEmpty()) {
            return Statistics()
        }

        // Filter projects based on includeCompleted parameter
        val filteredProjects = if (includeCompleted) {
            projects
        } else {
            projects.filter { it.status != ProjectStatus.COMPLETED }
        }

        val taskSummaries = filteredProjects.flatMap { it.tasks }
            .map {
                TaskSummary(
                    it.title, it.description, it.estimatedHours
                )
            }

        val averageTitleLength = taskSummaries.map { it.title.length }.average()
        val averageDescriptionLength = taskSummaries.mapNotNull { it.description?.length }.average()

        val mostCommonTaskDescription = taskSummaries.mapNotNull { it.description }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }
            ?.key

        // Use the extension function to categorize tasks
        val tasksByCategory = taskSummaries.categorizeTasks()

        val totalHoursByStatus = filteredProjects
            .groupBy { it.status.name }
            .mapValues { (_, projects) ->
                projects.flatMap { it.tasks }
                    .sumOf { it.estimatedHours ?: 0 }
            }

        val projectsByCompletionStatus = projects
            .groupingBy { if (it.status.isFinished()) "Finished" else "Unfinished" }
            .eachCount()
            .mapValues { it.value.toLong() }

        return Statistics(
            taskSummaries,
            averageTitleLength,
            averageDescriptionLength,
            mostCommonTaskDescription,
            tasksByCategory,
            totalHoursByStatus,
            projectsByCompletionStatus,
        )
    }
}
