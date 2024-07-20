package org.mvukic.routing

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
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
        println(Json.encodeToString(ListSerializer(MyResponse.serializer()), listOf(MyResponse("free access"))))
//        return ServerResponse.ok().bodyValueAndAwait(listOf(MyResponse("no auth required")))
        return ServerResponse.ok().bodyValueAndAwait(MyResponse("no auth required"))
    }

    suspend fun error(request: ServerRequest): ServerResponse {
        error("some error")
    }
}