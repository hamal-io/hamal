package io.hamal.api.http.controller.request

import io.hamal.api.http.controller.accepted
import io.hamal.core.adapter.request.RequestGetPort
import io.hamal.lib.domain.vo.RequestId
import io.hamal.lib.sdk.api.ApiRequested
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController


@RestController
internal class RequestGetController(private val requestGet: RequestGetPort) {
    @GetMapping("/v1/requests/{reqId}")
    fun get(@PathVariable("reqId") reqId: RequestId): ResponseEntity<ApiRequested> = requestGet(reqId).accepted()
}