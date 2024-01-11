package io.hamal.core.request

import io.hamal.lib.domain.request.Requested
import kotlin.reflect.KClass

abstract class RequestHandler<REQUEST : Requested>(
    val reqClass: KClass<REQUEST>
) {
    abstract operator fun invoke(req: @UnsafeVariance REQUEST)
}
