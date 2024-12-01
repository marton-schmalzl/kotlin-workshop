import com.example.demo.DemoApplication
import com.example.demo.model.Project
import com.example.demo.model.ProjectStatus
import com.example.demo.model.Task
import com.example.demo.repository.ProjectRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest(classes = [DemoApplication::class])
@AutoConfigureMockMvc
@TestPropertySource(properties = ["project.service.impl=default"])
class ProjectControllerE2ETest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val projectRepository: ProjectRepository
) {

    @BeforeEach
    fun setup() {
        projectRepository.deleteAll()

        val projectAlpha = Project().apply {
            name = "Project Alpha"
            status = ProjectStatus.PLANNED
        }

        val projectBeta = Project().apply {
            name = "Project Beta"
            status = ProjectStatus.IN_PROGRESS
        }

        val projectGamma = Project().apply {
            name = "Project Gamma"
            status = ProjectStatus.COMPLETED
        }

        projectAlpha.tasks = listOf(
            Task().apply {
                title = "Task Alpha 1"
                description = "Alpha Task Description 1"
                estimatedHours = 10
                project = projectAlpha
            },
            Task().apply {
                title = "Task Alpha 2"
                description = "Alpha Task Description 2"
                estimatedHours = 20
                project = projectAlpha
            },
            Task().apply {
                title = "Task Alpha 3"
                description = "Duplicate Description"
                estimatedHours = null
                project = projectAlpha
            }
        )

        projectBeta.tasks = listOf(
            Task().apply {
                title = "Task Beta 1"
                description = "Beta Task Description 1"
                estimatedHours = 30
                project = projectBeta
            },
            Task().apply {
                title = "Task Beta 2"
                description = "Duplicate Description"
                estimatedHours = 15
                project = projectBeta
            }
        )

        projectGamma.tasks = listOf(
            Task().apply {
                title = "Task Gamma 1"
                description = null
                estimatedHours = 25
                project = projectGamma
            },
            Task().apply {
                title = "Task Gamma 2"
                description = "Gamma Task Description 1"
                estimatedHours = 40
                project = projectGamma
            },
            Task().apply {
                title = "Task Gamma 3"
                description = "Gamma Task Description 2"
                estimatedHours = 50
                project = projectGamma
            }
        )

        projectRepository.saveAll(listOf(projectAlpha, projectBeta, projectGamma))
    }

    @ParameterizedTest
    @MethodSource("implementations")
    fun `GET statistics - exclude completed projects`(implementation: String) {
        System.setProperty("project.service.impl", implementation)

        mockMvc.get("/statistics?includeCompleted=false")
            .andExpect {
                status { isOk() }
                jsonPath("$.totalHoursByStatus.COMPLETED") { doesNotExist() } // Excluded
                jsonPath("$.totalHoursByStatus.PLANNED") { value(30) }
                jsonPath("$.totalHoursByStatus.IN_PROGRESS") { value(45) }
            }
    }

    @ParameterizedTest
    @MethodSource("implementations")
    fun `GET statistics - task summaries`(implementation: String) {
        System.setProperty("project.service.impl", implementation)

        mockMvc.get("/statistics?includeCompleted=true")
            .andExpect {
                status { isOk() }
                jsonPath("$.taskSummaries") { isArray() }
                jsonPath("$.taskSummaries.length()") { value(8) } // Total tasks from all projects
                jsonPath("$.taskSummaries[?(@.title == 'Task Alpha 1')].description") { value("Alpha Task Description 1") }
                jsonPath("$.taskSummaries[?(@.title == 'Task Beta 2')].hours") { value(15) }
                jsonPath("$.taskSummaries[?(@.title == 'Task Gamma 1')].description") { value(null) } // Null description
            }
    }

    @ParameterizedTest
    @MethodSource("implementations")
    fun `GET statistics - total hours by status`(implementation: String) {
        System.setProperty("project.service.impl", implementation)

        mockMvc.get("/statistics")
            .andExpect {
                status { isOk() }
                jsonPath("$.totalHoursByStatus.PLANNED") { value(30) }
                jsonPath("$.totalHoursByStatus.IN_PROGRESS") { value(45) }
                jsonPath("$.totalHoursByStatus.COMPLETED") { value(115) }
            }
    }

    @ParameterizedTest
    @MethodSource("implementations")
    fun `GET statistics - average title and description lengths`(implementation: String) {
        System.setProperty("project.service.impl", implementation)

        mockMvc.get("/statistics")
            .andExpect {
                status { isOk() }
                jsonPath("$.averageTitleLength") { value(11.75) } // Average of "Task Alpha 1", etc.
                jsonPath("$.averageDescriptionLength") { value(23.0) } // Excluding null
            }
    }

    @ParameterizedTest
    @MethodSource("implementations")
    fun `GET statistics - most common task description`(implementation: String) {
        System.setProperty("project.service.impl", implementation)

        mockMvc.get("/statistics")
            .andExpect {
                status { isOk() }
                jsonPath("$.mostCommonTaskDescription") { value("Duplicate Description") }
            }
    }

    @ParameterizedTest
    @MethodSource("implementations")
    fun `GET statistics - tasks categorized by size`(implementation: String) {
        System.setProperty("project.service.impl", implementation)

        mockMvc.get("/statistics")
            .andExpect {
                status { isOk() }
                jsonPath("$.tasksByCategory.Small.length()") { value(2) } // Tasks < 20 hours
                jsonPath("$.tasksByCategory.Medium.length()") { value(4) } // Tasks 20-40 hours
                jsonPath("$.tasksByCategory.Large.length()") { value(1) } // Tasks > 40 hours
            }
    }

    @ParameterizedTest
    @MethodSource("implementations")
    fun `GET statistics - projects by completion status`(implementation: String) {
        System.setProperty("project.service.impl", implementation)

        mockMvc.get("/statistics?includeCompleted=true")
            .andExpect {
                status { isOk() }
                jsonPath("$.projectsByCompletionStatus.Finished") { value(1) } // Adjust based on test data
                jsonPath("$.projectsByCompletionStatus.Unfinished") { value(2) }
            }
    }


    companion object {
        @JvmStatic
        fun implementations() = arrayOf("java", "kotlin")
    }

}
