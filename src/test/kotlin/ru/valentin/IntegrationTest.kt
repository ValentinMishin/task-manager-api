package ru.valentin

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.restassured.RestAssured
import io.restassured.http.ContentType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.shaded.com.fasterxml.jackson.databind.SerializationFeature
import org.testcontainers.utility.MountableFile
import ru.valentin.dto.request.CreateTaskDto
import ru.valentin.dto.request.NewTagDto
import java.nio.file.Paths
import java.time.LocalDate

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [TestContainersInitializer::class])
class IntegrationTest () {

    @LocalServerPort
    protected var port: Int = 0

    private val log = LoggerFactory.getLogger(javaClass)

//    companion object {
//        @Container
//        val postgres: PostgreSQLContainer<*>? = PostgreSQLContainer("postgres:15")
//            .withDatabaseName("testdb")
//            .withUsername("test")
//            .withPassword("test")
//            .withExposedPorts(5432)
//            .withCopyFileToContainer(
//                MountableFile.forHostPath(Paths.get("src/test/resources/01-schema.sql").toAbsolutePath()),
//                "/docker-entrypoint-initdb.d/01-schema.sql"
//            )
//
//        @JvmStatic
//        @DynamicPropertySource
//        fun registerProperties(registry: DynamicPropertyRegistry) {
//            postgres?.let { registry.add("spring.datasource.url", it::getJdbcUrl) }
//            postgres?.let { registry.add("spring.datasource.username", it::getUsername) }
//            postgres?.let { registry.add("spring.datasource.password", it::getPassword) }
//            registry.add("spring.jpa.hibernate.ddl-auto") { "none" }
//            registry.add("spring.sql.init.mode") { "always" }
//            registry.add("spring.sql.init.data-locations") { "classpath:02-data.sql" }
//        }
//    }

    @BeforeEach
    fun setup() {
        RestAssured.port = port
        RestAssured.baseURI = "http://localhost"
        log.info(RestAssured.port.toString())
    }

//    @Test
//    fun contextLoads() {
//        // Тест будет использовать реальный контейнер PostgreSQL
//    }
//
    @Test
    fun testJson() {
        val mapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())

        val newTag1 = NewTagDto("test_tag1")
        val newTag2 = NewTagDto("test_tag2")
        val newTags = setOf(newTag1, newTag2)

        val existingTags = setOf(1L, 2L)

        val createTaskDtoWithNulls = CreateTaskDto(
            title = "test_task1",
            typeId = 0L,
            description = "test description for test_task1",
            dueDate = LocalDate.now().plusDays(5),
            null,
            null
        )

        val createTaskDtoWithTags = CreateTaskDto(
            title = "test_task1",
            typeId = 0L,
            description = "test description for test_task1",
            dueDate = LocalDate.now().plusDays(5),
            existingTags,
            newTags = newTags
        )


        val jsonCreateTaskDtoWithNulls = mapper.writeValueAsString(createTaskDtoWithNulls)
        val jsonCreateTaskDtoWithTags = mapper.writeValueAsString(createTaskDtoWithTags)
    }

    @Test
    fun `GET non-existent tag should return 404`() {
        val nonExistentTagId = 999L

        RestAssured.given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/tags/$nonExistentTagId/tasks")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
    }
}