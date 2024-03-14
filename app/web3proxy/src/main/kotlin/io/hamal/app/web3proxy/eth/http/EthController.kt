package io.hamal.app.web3proxy.eth.http

import io.hamal.app.web3proxy.eth.handler.HandleEthRequest
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotNode
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.web3.eth.domain.*
import io.hamal.lib.web3.eth.domain.EthError.ErrorCode.*
import io.hamal.lib.web3.eth.json
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
internal class EthController(
    private val handle: HandleEthRequest
) {

    @PostMapping("/eth")
    fun handle(@RequestBody body: HotNode<*>): ResponseEntity<*> {
        return when (body) {
            is HotArray -> handleArray(body)
            is HotObject -> handleObject(body)
            else -> TODO()
        }
    }

    private fun handleArray(@RequestBody requests: HotArray): ResponseEntity<List<EthResponse>> {
        // FIXME json.deserializeList(EthRequest::class, requests)
        val ethRequests = requests.map { obj ->
            json.deserialize(EthRequest::class, json.serialize(obj))
        }
        return ResponseEntity.ok(handle(ethRequests))
    }

    private fun handleObject(@RequestBody request: HotObject): ResponseEntity<EthResponse> {
        val (err, req) = parseRequest(request)
        if (err != null) {
            return ResponseEntity.ok(err)
        }
        return ResponseEntity.ok(handle(req!!))
    }

    private fun parseRequest(request: HotObject): Pair<EthErrorResponse?, EthRequest?> {
        return try {
            val ethRequest = json.deserialize(EthRequest::class, json.serialize(request))
            null to ethRequest
        } catch (e: Throwable) {
            e.printStackTrace()
            println(e.message)
            when {
                e.message?.contains("does not start with 0x") == true -> {
                    EthErrorResponse(
                        id = EthRequestId(request["id"].stringValue),
                        error = EthError(InvalidParams, "invalid argument: hex string without 0x prefix")
                    ) to null
                }

                e.message?.contains("does not match hex pattern") == true -> {
                    EthErrorResponse(
                        id = EthRequestId(request["id"].stringValue),
                        error = EthError(InvalidParams, "invalid argument: hex string")
                    ) to null
                }

                e.message?.contains("out of bounds for length") == true -> {
                    EthErrorResponse(
                        id = EthRequestId(request["id"].stringValue),
                        error = EthError(InvalidParams, "missing argument")
                    ) to null
                }

                e.message?.contains("EthMethod not found") == true -> {
                    EthErrorResponse(
                        id = EthRequestId(request["id"].stringValue),
                        error = EthError(MethodNotFound, "method not supported")
                    ) to null
                }

                else -> EthErrorResponse(
                    id = EthRequestId(request["id"].stringValue),
                    error = EthError(InternalError, "Unexpected error")
                ) to null
            }
        }
    }
}