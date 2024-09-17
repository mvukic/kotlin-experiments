package org.mvukic

import io.klogging.NoCoLogging
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.Neo4jContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class IntegrationTestsBase : NoCoLogging {

    companion object {

        private val dbImage = DockerImageName.parse("neo4j").withTag("5.16-enterprise")

        @JvmStatic
        @Container
        @ServiceConnection
        private val db = Neo4jContainer(dbImage)
            .withReuse(true)
            .withEnv("NEO4J_ACCEPT_LICENSE_AGREEMENT", "yes")
            .withoutAuthentication()

        private val mockImage = DockerImageName.parse("wiremock/wiremock").withTag("2.35.0")


        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            db.start()
            println(db.boltUrl)
            println(db.httpUrl)
            println(db.containerName)

            // TODO: run some query to initialize database
            db.execInContainer("")

//            mock.start()
//            println(mock.baseUrl)
//            println(mock.port)

        }
    }

    @BeforeEach
    fun beforeEach() {
        println("Before each database test")
    }


}
