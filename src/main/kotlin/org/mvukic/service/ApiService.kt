package org.mvukic.service

import io.klogging.Klogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class ApiService : Klogging {

    suspend fun api1(id: String): String {
        val link = "https://jsonplaceholder.typicode.com/todos/$id"
        val webClient = WebClient.builder().baseUrl(link).build()
        logger.info("get before call")
        val response = webClient.get().retrieve().awaitBody<String>()
        logger.info("get after call")
        return response
    }

    suspend fun api2(id: String): String? {
        val link = "https://jsonplaceholder.typicode.com/todos/$id"
        val restClient = RestClient.builder().baseUrl(link).build()
        logger.info("get before call")
        val response: String? = restClient.get().retrieve().body(String::class.java)
        logger.info("get after call")
        return response
    }

}