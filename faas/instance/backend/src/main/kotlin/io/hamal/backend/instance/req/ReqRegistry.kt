package io.hamal.backend.instance.req

import io.hamal.backend.repository.api.submitted_req.SubmittedReq
import io.hamal.lib.common.KeyedOnce
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
    operator fun <REQ : SubmittedReq> get(reqClass: KClass<out REQ>): ReqHandler<REQ> =
        reqOnce(reqClass) {
            val result = reqHandler[reqClass]
                ?: reqClass.java.interfaces.asSequence().firstOrNull { reqHandler[it.kotlin] != null }
                ?: throw IllegalArgumentException("No req handler registered for $reqClass")

            result as ReqHandler<SubmittedReq>
        } as ReqHandler<REQ>

    @Suppress("UNCHECKED_CAST")
    private fun register(
        reqClass: KClass<out SubmittedReq>, operation: ReqHandler<*>
    ) {
        check(operation.reqClass == reqClass)
        reqHandler[reqClass] = operation as ReqHandler<SubmittedReq>
    }

    private val reqHandler = mutableMapOf<KClass<out SubmittedReq>, ReqHandler<SubmittedReq>>()
    private val reqOnce: KeyedOnce<KClass<out SubmittedReq>, ReqHandler<SubmittedReq>> = KeyedOnce.default()
}