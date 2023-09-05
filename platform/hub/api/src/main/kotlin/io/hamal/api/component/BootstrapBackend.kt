package io.hamal.api.component

import io.hamal.api.req.SubmitRequest
import io.hamal.lib.domain.req.CreateAccountReq
import io.hamal.lib.domain.req.CreateNamespaceReq
import io.hamal.lib.domain.vo.AccountName
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import io.hamal.lib.domain.vo.Password
import org.springframework.stereotype.Component

@Component
class BootstrapBackend(
    private val submitRequest: SubmitRequest
) {
    fun bootstrap() {
        val acctReq = submitRequest(
            CreateAccountReq(
                AccountName("root"),
                null,
                Password("toor")
            )
        )
        submitRequest(acctReq.groupId, CreateNamespaceReq(NamespaceName("hamal"), NamespaceInputs()))
    }
}