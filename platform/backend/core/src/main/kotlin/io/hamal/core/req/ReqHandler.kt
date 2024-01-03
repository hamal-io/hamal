package io.hamal.core.req

import io.hamal.lib.domain.request.Requested
import kotlin.reflect.KClass

abstract class ReqHandler<REQUEST : Requested>(
    val reqClass: KClass<REQUEST>
) {
    abstract operator fun invoke(req: @UnsafeVariance REQUEST)
}
