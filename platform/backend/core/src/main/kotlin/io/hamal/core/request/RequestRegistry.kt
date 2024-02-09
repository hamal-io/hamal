package io.hamal.core.request

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.request.Requested
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import kotlin.reflect.KClass


@Component
class RequestRegistry : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        event.applicationContext.getBeansOfType(io.hamal.core.request.RequestHandler::class.java)
            .forEach { (_, handler) ->
                register(handler.reqClass, handler as io.hamal.core.request.RequestHandler<*>)
            }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <REQ : Requested> get(reqClass: KClass<out REQ>): io.hamal.core.request.RequestHandler<REQ> =
        reqOnce(reqClass) {
            val result = reqHandler[reqClass]
                ?: reqClass.java.interfaces.asSequence().firstOrNull { reqHandler[it.kotlin] != null }
                ?: throw IllegalArgumentException("No request handler registered for $reqClass")

            result as io.hamal.core.request.RequestHandler<Requested>
        } as io.hamal.core.request.RequestHandler<REQ>

    @Suppress("UNCHECKED_CAST")
    private fun register(
        reqClass: KClass<out Requested>, operation: io.hamal.core.request.RequestHandler<*>
    ) {
        check(operation.reqClass == reqClass)
        reqHandler[reqClass] = operation as io.hamal.core.request.RequestHandler<Requested>
    }

    private val reqHandler = mutableMapOf<KClass<out Requested>, io.hamal.core.request.RequestHandler<Requested>>()
    private val reqOnce: KeyedOnce<KClass<out Requested>, io.hamal.core.request.RequestHandler<Requested>> =
        KeyedOnce.default()
}