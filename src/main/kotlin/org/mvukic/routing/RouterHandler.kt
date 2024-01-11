package org.mvukic.routing

import kotlinx.coroutines.delay
import org.mvukic.model.MyResponse
import org.mvukic.service.ApiService
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Service
class RouterHandler(private val api: ApiService) {

    suspend fun get1(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val response = api.api1(id)
        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    suspend fun get2(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id")
        val response = api.api2(id)
        return ServerResponse.ok().bodyValueAndAwait(response ?: "")
    }

    suspend fun free(request: ServerRequest): ServerResponse {
        delay(2000)
        return ServerResponse.ok().bodyValueAndAwait(MyResponse("no auth required"))
    }

    suspend fun error(request: ServerRequest): ServerResponse {
        error("some error")
    }
}