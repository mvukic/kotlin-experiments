package org.mvukic.service

import io.klogging.Klogging
import org.springframework.cache.CacheManager
import org.springframework.cache.set
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Service
class ApiService(
    private val cacheManager: CacheManager
) : Klogging {

    private val api1Cache = cacheManager.getCache("api_1")
    private val api2Cache = cacheManager.getCache("api_2")

    //    @Cacheable(value = ["api_"], key = "id")
    suspend fun api1(id: String): String {
        api1Cache?.get("id", String::class.java)?.let {
            return it
        }
        val link = "https://jsonplaceholder.typicode.com/todos/$id"
        val webClient = WebClient.builder().baseUrl(link).build()
        logger.info("get before call")
        val response = webClient.get().retrieve().awaitBody<String>()
        logger.info("get after call")
        api1Cache?.set("id", response)
        return response
    }

    //    @Cacheable(value = ["api_2"], key = "id")
    suspend fun api2(id: String): String? {
        api2Cache?.get("id", String::class.java)?.let {
            return it
        }
        val link = "https://jsonplaceholder.typicode.com/todos/$id"
        val restClient = RestClient.builder().baseUrl(link).build()
        logger.info("get before call")
        val response: String? = restClient.get().retrieve().body(String::class.java)
        logger.info("get after call")
        api2Cache?.set("id", response)
        return response
    }

}