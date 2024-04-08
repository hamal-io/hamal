package com.nyanbot.http.controller.flow

import com.nyanbot.repository.*
import com.nyanbot.security.SecurityContext
import io.hamal.lib.domain.GenerateDomainId
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.sdk.ApiSdkImpl
import io.hamal.lib.sdk.api.ApiFuncCreateRequest
import io.hamal.lib.sdk.api.ApiNamespaceAppendRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class FlowCreateRequest(
    val name: FlowName,
    val triggerType: FlowTriggerType,
    val code: CodeValue
)


@RestController
class FlowCreateController(
    private val flowRepository: FlowRepository,
    private val generateDomainId: GenerateDomainId,
    @Value("\${io.hamal.server.url}") val url: String,
    @Value("\${io.hamal.server.token}") val token: String
) {

    @PostMapping("/v1/flows")
    fun invoke(
        @RequestBody req: FlowCreateRequest
    ): Flow {

        val accountId = SecurityContext.currentAccountId
        val flowId = generateDomainId(::FlowId)

        // ensure com::nyanbot::accounts::accountId -- exists
        // com::nyanbot::accounts::accountId::flows::flowId -- create
        // create function
        // create trigger
        //

        val sdk = ApiSdkImpl(HttpTemplateImpl(
            baseUrl = url,
            headerFactory = { this["Authorization"] = "Bearer $token" }
        ))

        val me = sdk.account.me()
        val workspaceId = me.workspaces.first().id

        val accountNamespaceId = sdk.namespace.list(workspaceId).let { namespaces ->
            val accountNamespaceRoot = namespaces.find { it.name == NamespaceName("com::nyanbot::accounts") }!!

            val accountNamespaceName = NamespaceName("com::nyanbot::accounts::${accountId.value.value.toString(16)}")
            println(namespaces)

            namespaces.find { it.name == accountNamespaceName }?.id
                ?: sdk.namespace.append(
                    accountNamespaceRoot.id, ApiNamespaceAppendRequest(
                        name = NamespaceName(accountId.value.value.toString(16)),
                        features = null
                    )
                ).id
        }

        Thread.sleep(200)

        val flowsNamespaceId = sdk.namespace.list(workspaceId).let { namespaces ->
            namespaces.find { it.name == NamespaceName("com::nyanbot::accounts::${accountId.value.value.toString(16)}::flows") }?.id ?: sdk.namespace.append(
                accountNamespaceId, ApiNamespaceAppendRequest(
                    name = NamespaceName("flows"),
                    features = null
                )
            ).id
        }

        Thread.sleep(200)

        val flowNamespaceId = sdk.namespace.append(
            flowsNamespaceId,
            ApiNamespaceAppendRequest(
                name = NamespaceName(flowId.value.value.toString(16)),
                features = null
            )
        ).id

        println(flowNamespaceId)

        val funcId = sdk.func.create(
            flowNamespaceId, ApiFuncCreateRequest(
                name = FuncName("func"),
                inputs = FuncInputs(),
                code = req.code,
                codeType = CodeType.Nodes
            )
        ).id

//        val triggerId = sdk.trigger.create(
//            flowNamespaceId, ApiTriggerCreateReq(
//                TriggerType.Event,
//                name = TriggerName("trigger"),
//                funcId = funcId,
//                topicId = TopicId("d2555165c10000") // FIXME this must be resolved automatically based on flow trigger type
//            )
//        ).id

        return flowRepository.create(
            FlowCmdRepository.CreateCmd(
                flowId = flowId,
                name = req.name,
                accountId = SecurityContext.currentAccountId,
                flowTrigger = FlowTrigger(
                    type = FlowTriggerType("v1_web3_new_lp_pair")
                    // config
                ),
                namespaceId = flowNamespaceId,
                funcId = funcId,
//                triggerId = triggerId
                triggerId = null
            )
        )
    }

}