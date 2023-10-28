package io.hamal.core.req

import io.hamal.lib.common.KeyedOnce
import io.hamal.repository.api.submitted_req.Submitted
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import kotlin.reflect.KClass


@Component
class ReqRegistry : ApplicationListener<ContextRefreshedEvent> {
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        event.applicationContext.getBeansOfType(ReqHandler::class.java)
            .forEach { (_, handler) ->
                register(handler.reqClass, handler as ReqHandler<*>)
            }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <REQ : Submitted> get(reqClass: KClass<out REQ>): ReqHandler<REQ> =
        reqOnce(reqClass) {
            val result = reqHandler[reqClass]
                ?: reqClass.java.interfaces.asSequence().firstOrNull { reqHandler[it.kotlin] != null }
                ?: throw IllegalArgumentException("No req handler registered for $reqClass")

            result as ReqHandler<Submitted>
        } as ReqHandler<REQ>

    @Suppress("UNCHECKED_CAST")
    private fun register(
        reqClass: KClass<out Submitted>, operation: ReqHandler<*>
    ) {
        check(operation.reqClass == reqClass)
        reqHandler[reqClass] = operation as ReqHandler<Submitted>
    }

    private val reqHandler = mutableMapOf<KClass<out Submitted>, ReqHandler<Submitted>>()
    private val reqOnce: KeyedOnce<KClass<out Submitted>, ReqHandler<Submitted>> = KeyedOnce.default()
}