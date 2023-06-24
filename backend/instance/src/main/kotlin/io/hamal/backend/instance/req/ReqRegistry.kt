package io.hamal.backend.instance.req

import io.hamal.lib.common.KeyedOnce
import io.hamal.lib.domain.req.Req
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
    operator fun <REQ : Req> get(reqClass: KClass<out REQ>): ReqHandler<REQ> =
        reqOnce(reqClass) {
            val result = reqHandler[reqClass]
                ?: reqClass.java.interfaces.asSequence().firstOrNull { reqHandler[it.kotlin] != null }
                ?: throw IllegalArgumentException("No req handler registered for $reqClass")

            result as ReqHandler<Req>
        } as ReqHandler<REQ>

    @Suppress("UNCHECKED_CAST")
    private fun register(
        reqClass: KClass<out Req>, operation: ReqHandler<*>
    ) {
        check(operation.reqClass == reqClass)
        reqHandler[reqClass] = operation as ReqHandler<Req>
    }

    private val reqHandler = mutableMapOf<KClass<out Req>, ReqHandler<Req>>()
    private val reqOnce: KeyedOnce<KClass<out Req>, ReqHandler<Req>> = KeyedOnce.default()
}