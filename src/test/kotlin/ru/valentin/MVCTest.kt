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
import org.testcontainers.junit.jupiter.Testcontainers
import ru.valentin.dto.request.CreateTaskDto
import ru.valentin.dto.request.NewTagDto
import java.time.LocalDate

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [TestContainersInitializer::class])
class MVCTest () {

    @LocalServerPort
    protected var port: Int = 0

    private val log = LoggerFactory.getLogger(javaClass)

    @BeforeEach
    fun setup() {
        RestAssured.port = port
        RestAssured.baseURI = "http://localhost"
    }

    @Test
    fun testCre() {
        val mapper = jacksonObjectMapper()
            .registerModule(JavaTimeModule())

        val newTag1 = NewTagDto("test_tag1")
        val newTag2 = NewTagDto("test_tag2")
        val newTags = setOf(newTag1, newTag2)
        val existingTags = setOf(1L, 2L)

        val createTaskWithNulls = CreateTaskDto(
            title = "test_task1",
            typeId = 0L,
            description = "test description for test_task1",
            dueDate = LocalDate.now().plusDays(5),
            null,
            null
        ).let { mapper.writeValueAsString(it) }

        val createTaskWithTags = CreateTaskDto(
            title = "test_task1",
            typeId = 0L,
            description = "test description for test_task1",
            dueDate = LocalDate.now().plusDays(5),
            existingTags,
            newTags = newTags
        ).let { mapper.writeValueAsString(it) }

    }

    @Test
    fun tagNotExists() {
        val nonExistentTagId = 999L

        RestAssured.given()
            .contentType(ContentType.JSON)
            .`when`()
            .get("/api/tags/$nonExistentTagId/tasks")
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
    }
}