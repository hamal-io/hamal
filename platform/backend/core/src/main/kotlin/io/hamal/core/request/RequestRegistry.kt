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
        event.applicationContext.getBeansOfType(RequestHandler::class.java)
            .forEach { (_, handler) ->
                register(handler.reqClass, handler as RequestHandler<*>)
            }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <REQ : Requested> get(reqClass: KClass<out REQ>): RequestHandler<REQ> =
        reqOnce(reqClass) {
            val result = reqHandler[reqClass]
                ?: reqClass.java.interfaces.asSequence().firstOrNull { reqHandler[it.kotlin] != null }
                ?: throw IllegalArgumentException("No request handler registered for $reqClass")

            result as RequestHandler<Requested>
        } as RequestHandler<REQ>

    @Suppress("UNCHECKED_CAST")
    private fun register(
        reqClass: KClass<out Requested>, operation: RequestHandler<*>
    ) {
        check(operation.reqClass == reqClass)
        reqHandler[reqClass] = operation as RequestHandler<Requested>
    }

    private val reqHandler = mutableMapOf<KClass<out Requested>, RequestHandler<Requested>>()
    private val reqOnce: KeyedOnce<KClass<out Requested>, RequestHandler<Requested>> =
        KeyedOnce.default()
}