package io.hamal.backend.infra.adapter

import io.hamal.backend.core.port.GetLoggerPort
import io.hamal.backend.core.port.LogPort
import io.hamal.backend.core.port.notification.FlushDomainNotificationPort
import io.hamal.lib.KeyedOnce
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.ddd.usecase.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class BackendUseCaseInvokerAdapter private constructor(
    private val getRequestOneUseCase: GetRequestOneUseCasePort,
    private val getQueryManyUseCase: GetQueryManyUseCasePort,
    private val getQueryOneUseCase: GetQueryOneUseCasePort,
    private val flushDomainNotifications: FlushDomainNotificationPort,
    private val log: LogPort
) : InvokeUseCasePort {

    @Autowired
    constructor(
        useCaseRegistry: GetUseCasePort,
        flushDomainNotificationPort: FlushDomainNotificationPort,
        getLoggerPort: GetLoggerPort
    ) : this(
        useCaseRegistry,
        useCaseRegistry,
        useCaseRegistry,
        flushDomainNotificationPort,
        getLoggerPort(BackendUseCaseInvokerAdapter::class)
    )

    override fun <RESULT : DomainObject, USE_CASE : RequestOneUseCase<RESULT>> invoke(useCase: USE_CASE): RESULT {
        val operation = getRequestOneUseCase[useCase::class]
        logUseCaseInvocation(useCase)
        return operation(useCase).let {
            flushDomainNotifications()
            it
        }
    }

    override fun <RESULT : DomainObject, USE_CASE : QueryManyUseCase<RESULT>> invoke(useCase: USE_CASE): List<RESULT> {
        logUseCaseInvocation(useCase)
        return getQueryManyUseCase[useCase::class](useCase)
    }

    override fun <RESULT : DomainObject, USE_CASE : QueryOneUseCase<RESULT>> invoke(useCase: USE_CASE): RESULT {
        logUseCaseInvocation(useCase)
        return getQueryOneUseCase[useCase::class](useCase)
    }

    private fun logUseCaseInvocation(useCase: UseCase<*>) {
        log.trace("Invoking use case $useCase")
    }
}

@Component
class BackendUseCaseRegistryAdapter : GetUseCasePort, ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        event.applicationContext.getBeansOfType(RequestOneUseCaseHandler::class.java)
            .forEach { (_, operation) ->
                register(operation.useCaseClass, operation as RequestOneUseCaseHandler<*, *>)
            }

        event.applicationContext.getBeansOfType(QueryManyUseCaseHandler::class.java)
            .forEach { (_, operation) ->
                register(operation.useCaseClass, operation as QueryManyUseCaseHandler<*, *>)
            }

        event.applicationContext.getBeansOfType(QueryOneUseCaseHandler::class.java)
            .forEach { (_, operation) ->
                register(operation.useCaseClass, operation as QueryOneUseCaseHandler<*, *>)
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : DomainObject, USE_CASE : RequestOneUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): RequestOneUseCaseHandler<RESULT, USE_CASE> =
        requestOneOnce(useCaseClass) {
            val operation = requestOneOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().firstOrNull { requestOneOperations[it.kotlin] != null }

            check(operation != null) { "No operation registered for $useCaseClass" }

            operation as RequestOneUseCaseHandler<*, RequestOneUseCase<*>>
        } as RequestOneUseCaseHandler<RESULT, USE_CASE>

    override fun <RESULT : DomainObject, USE_CASE : QueryManyUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): QueryManyUseCaseHandler<RESULT, USE_CASE> =
        queryManyOnce(useCaseClass) {
            val operation = queryManyOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().firstOrNull { queryManyOperations[it.kotlin] != null }

            check(operation != null) { "No operation registered for $useCaseClass" }

            operation as QueryManyUseCaseHandler<*, QueryManyUseCase<*>>
        } as QueryManyUseCaseHandler<RESULT, USE_CASE>

    override fun <RESULT : DomainObject, USE_CASE : QueryOneUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): QueryOneUseCaseHandler<RESULT, USE_CASE> =
        queryOneOnce(useCaseClass) {
            val operation = fetchOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().firstOrNull { fetchOperations[it.kotlin] != null }

            check(operation != null) { "No operation registered for $useCaseClass" }

            operation as QueryOneUseCaseHandler<*, QueryOneUseCase<*>>
        } as QueryOneUseCaseHandler<RESULT, USE_CASE>

    @Suppress("UNCHECKED_CAST")
    internal fun register(
        useCaseClass: KClass<out RequestOneUseCase<*>>, operation: RequestOneUseCaseHandler<*, *>
    ) {
        check(operation.useCaseClass == useCaseClass)
        requestOneOperations[useCaseClass] = operation as RequestOneUseCaseHandler<*, RequestOneUseCase<*>>
    }

    @Suppress("UNCHECKED_CAST")
    internal fun register(useCaseClass: KClass<out QueryManyUseCase<*>>, operation: QueryManyUseCaseHandler<*, *>) {
        check(operation.useCaseClass == useCaseClass)
        queryManyOperations[useCaseClass] = operation as QueryManyUseCaseHandler<*, QueryManyUseCase<*>>
    }

    @Suppress("UNCHECKED_CAST")
    internal fun register(useCaseClass: KClass<out QueryOneUseCase<*>>, operation: QueryOneUseCaseHandler<*, *>) {
        check(operation.useCaseClass == useCaseClass)
        fetchOperations[useCaseClass] = operation as QueryOneUseCaseHandler<*, QueryOneUseCase<*>>
    }


    private val requestOneOperations =
        mutableMapOf<KClass<out UseCase<*>>, RequestOneUseCaseHandler<*, RequestOneUseCase<*>>>()
    private val queryManyOperations =
        mutableMapOf<KClass<out QueryManyUseCase<*>>, QueryManyUseCaseHandler<*, QueryManyUseCase<*>>>()
    private val fetchOperations =
        mutableMapOf<KClass<out QueryOneUseCase<*>>, QueryOneUseCaseHandler<*, QueryOneUseCase<*>>>()

    private val requestOneOnce: KeyedOnce<KClass<out RequestOneUseCase<*>>, RequestOneUseCaseHandler<*, RequestOneUseCase<*>>> =
        KeyedOnce.default()

    private val queryManyOnce: KeyedOnce<KClass<out QueryManyUseCase<*>>, QueryManyUseCaseHandler<*, QueryManyUseCase<*>>> =
        KeyedOnce.default()

    private val queryOneOnce: KeyedOnce<KClass<out QueryOneUseCase<*>>, QueryOneUseCaseHandler<*, QueryOneUseCase<*>>> =
        KeyedOnce.default()
}