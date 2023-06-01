package io.hamal.backend.req

import io.hamal.backend.repository.api.domain.Req
import kotlin.reflect.KClass

abstract class ReqHandler<REQUEST : Req>(
    val reqClass: KClass<REQUEST>
) {
    abstract operator fun invoke(req: @UnsafeVariance REQUEST)
}
