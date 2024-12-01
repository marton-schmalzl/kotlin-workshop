package com.example.demo.service

import com.example.demo.model.*
import com.example.demo.repository.ProjectRepository
import org.springframework.stereotype.Service

@Service("kotlinProjectService")
class KotlinProjectService(private val projectRepository: ProjectRepository) : ProjectService {

    override fun calculateStatistics(includeCompleted: Boolean): Statistics {
        return Statistics() // @TODO
    }
}
