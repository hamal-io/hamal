package io.hamal.backend.instance.req

import io.hamal.backend.repository.api.submitted_req.SubmittedReq
import kotlin.reflect.KClass

abstract class ReqHandler<REQUEST : SubmittedReq>(
    val reqClass: KClass<REQUEST>
) {
    abstract operator fun invoke(req: @UnsafeVariance REQUEST)
}
