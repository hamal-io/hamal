package io.hamal.core.req

import io.hamal.lib.domain.submitted.Submitted
import kotlin.reflect.KClass

abstract class ReqHandler<REQUEST : Submitted>(
    val reqClass: KClass<REQUEST>
) {
    abstract operator fun invoke(req: @UnsafeVariance REQUEST)
}
