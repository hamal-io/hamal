package io.hamal.backend.instance.req

import io.hamal.lib.domain.req.SubmittedReq
import kotlin.reflect.KClass

abstract class ReqHandler<REQUEST : SubmittedReq>(
    val reqClass: KClass<REQUEST>
) {
    abstract operator fun invoke(req: @UnsafeVariance REQUEST)
}
