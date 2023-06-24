package io.hamal.backend.instance.req

import io.hamal.lib.domain.req.Req
import kotlin.reflect.KClass

abstract class ReqHandler<REQUEST : Req>(
    val reqClass: KClass<REQUEST>
) {
    abstract operator fun invoke(req: @UnsafeVariance REQUEST)
}
