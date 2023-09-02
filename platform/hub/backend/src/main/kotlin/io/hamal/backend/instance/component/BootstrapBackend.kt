package io.hamal.backend.instance.component

import io.hamal.backend.instance.req.SubmitRequest
import io.hamal.lib.domain.req.CreateNamespaceReq
import io.hamal.lib.domain.vo.NamespaceInputs
import io.hamal.lib.domain.vo.NamespaceName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class BootstrapBackend(
    @Autowired val submitRequest: SubmitRequest
) {

    fun bootstrap() {
        submitRequest(CreateNamespaceReq(NamespaceName("hamal"), NamespaceInputs()))
    }

}