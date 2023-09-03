package io.hamal.backend.component

import io.hamal.backend.req.SubmitRequest
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
        submitRequest(CreateNamespaceReq(NamespaceName("hamal"), NamespaceInputs()))
        submitRequest(CreateAccountReq(AccountName("root"), null, Password("toor")))
    }
}