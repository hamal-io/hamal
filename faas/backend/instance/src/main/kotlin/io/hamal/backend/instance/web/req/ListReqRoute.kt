package io.hamal.backend.instance.web.req

import io.hamal.backend.repository.api.ReqQueryRepository
import io.hamal.backend.repository.api.submitted_req.*
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.domain.ReqId
import io.hamal.lib.sdk.domain.ApiDefaultSubmittedReq
import io.hamal.lib.sdk.domain.ApiReqList
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithDomainId
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ListReqRoute(
    private val reqQueryRepository: ReqQueryRepository
) {
    @GetMapping("/v1/reqs")
    fun listReqs(
        @RequestParam(required = false, name = "after_id", defaultValue = "0") afterId: ReqId,
        @RequestParam(required = false, name = "limit", defaultValue = "100") limit: Limit
    ): ResponseEntity<ApiReqList> {
        val result = reqQueryRepository.list {
            this.afterId = afterId
            this.limit = limit
        }
        return ResponseEntity.ok(ApiReqList(result.map {
            //FIXME
            when (val r = it) {
                is SubmittedAppendToTopicReq -> ApiSubmittedReqWithDomainId(
                    reqId = r.reqId,
                    status = r.status,
                    id = r.id.value
                )

                is SubmittedCompleteExecReq -> ApiSubmittedReqWithDomainId(
                    reqId = r.reqId,
                    status = r.status,
                    id = r.id.value
                )

                is SubmittedCreateFuncReq -> ApiSubmittedReqWithDomainId(
                    reqId = r.reqId,
                    status = r.status,
                    id = r.id.value
                )

                is SubmittedCreateTopicReq -> ApiSubmittedReqWithDomainId(
                    reqId = r.reqId,
                    status = r.status,
                    id = r.id.value
                )

                is SubmittedCreateTriggerReq -> ApiSubmittedReqWithDomainId(
                    reqId = r.reqId,
                    status = r.status,
                    id = r.id.value
                )

                is SubmittedFailExecReq -> ApiSubmittedReqWithDomainId(
                    reqId = r.reqId,
                    status = r.status,
                    id = r.id.value
                )

                is SubmittedInvokeExecReq -> ApiSubmittedReqWithDomainId(
                    reqId = r.reqId,
                    status = r.status,
                    id = r.id.value
                )

                is SubmittedUpdateFuncReq -> ApiSubmittedReqWithDomainId(
                    reqId = r.reqId,
                    status = r.status,
                    id = r.id.value
                )

                is SubmittedSetStateReq -> ApiDefaultSubmittedReq(
                    reqId = r.reqId,
                    status = r.status
                )
            }
        }))
    }
}