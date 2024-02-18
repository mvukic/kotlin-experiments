package org.mvukic

import org.springframework.boot.autoconfigure.neo4j.Neo4jConnectionDetails
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration(proxyBeanMethods = false)
class DatabaseConnectionConfig {

    @Bean
    fun neo4jDatabaseConfig(): Neo4jConnectionDetails {
        return object : Neo4jConnectionDetails {

        }
    }

}
