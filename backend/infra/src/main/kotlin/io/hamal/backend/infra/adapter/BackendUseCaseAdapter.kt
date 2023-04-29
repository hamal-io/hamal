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
    private val getExecuteOneUseCase: GetExecuteOneUseCasePort,
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

    override fun <RESULT : DomainObject, USE_CASE : ExecuteOneUseCase<RESULT>> invoke(useCase: USE_CASE): RESULT {
        val operation = getExecuteOneUseCase[useCase::class]
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
        event.applicationContext.getBeansOfType(ExecuteOneUseCaseOperation::class.java)
            .forEach { (_, operation) ->
                register(operation.useCaseClass, operation as ExecuteOneUseCaseOperation<*, *>)
            }

        event.applicationContext.getBeansOfType(QueryManyUseCaseOperation::class.java)
            .forEach { (_, operation) ->
                register(operation.useCaseClass, operation as QueryManyUseCaseOperation<*, *>)
            }

        event.applicationContext.getBeansOfType(QueryOneUseCaseOperation::class.java)
            .forEach { (_, operation) ->
                register(operation.useCaseClass, operation as QueryOneUseCaseOperation<*, *>)
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : DomainObject, USE_CASE : ExecuteOneUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): ExecuteOneUseCaseOperation<RESULT, USE_CASE> =
        executeOneOnce(useCaseClass) {
            val operation = executeOneOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().firstOrNull { executeOneOperations[it.kotlin] != null }

            check(operation != null) { "No operation registered for $useCaseClass" }

            operation as ExecuteOneUseCaseOperation<*, ExecuteOneUseCase<*>>
        } as ExecuteOneUseCaseOperation<RESULT, USE_CASE>

    override fun <RESULT : DomainObject, USE_CASE : QueryManyUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): QueryManyUseCaseOperation<RESULT, USE_CASE> =
        queryManyOnce(useCaseClass) {
            val operation = queryManyOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().firstOrNull { queryManyOperations[it.kotlin] != null }

            check(operation != null) { "No operation registered for $useCaseClass" }

            operation as QueryManyUseCaseOperation<*, QueryManyUseCase<*>>
        } as QueryManyUseCaseOperation<RESULT, USE_CASE>

    override fun <RESULT : DomainObject, USE_CASE : QueryOneUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): QueryOneUseCaseOperation<RESULT, USE_CASE> =
        queryOneOnce(useCaseClass) {
            val operation = fetchOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().firstOrNull { fetchOperations[it.kotlin] != null }

            check(operation != null) { "No operation registered for $useCaseClass" }

            operation as QueryOneUseCaseOperation<*, QueryOneUseCase<*>>
        } as QueryOneUseCaseOperation<RESULT, USE_CASE>

    @Suppress("UNCHECKED_CAST")
    internal fun register(
        useCaseClass: KClass<out ExecuteOneUseCase<*>>, operation: ExecuteOneUseCaseOperation<*, *>
    ) {
        check(operation.useCaseClass == useCaseClass)
        executeOneOperations[useCaseClass] = operation as ExecuteOneUseCaseOperation<*, ExecuteOneUseCase<*>>
    }

    @Suppress("UNCHECKED_CAST")
    internal fun register(useCaseClass: KClass<out QueryManyUseCase<*>>, operation: QueryManyUseCaseOperation<*, *>) {
        check(operation.useCaseClass == useCaseClass)
        queryManyOperations[useCaseClass] = operation as QueryManyUseCaseOperation<*, QueryManyUseCase<*>>
    }

    @Suppress("UNCHECKED_CAST")
    internal fun register(useCaseClass: KClass<out QueryOneUseCase<*>>, operation: QueryOneUseCaseOperation<*, *>) {
        check(operation.useCaseClass == useCaseClass)
        fetchOperations[useCaseClass] = operation as QueryOneUseCaseOperation<*, QueryOneUseCase<*>>
    }


    private val executeOneOperations =
        mutableMapOf<KClass<out UseCase<*>>, ExecuteOneUseCaseOperation<*, ExecuteOneUseCase<*>>>()
    private val queryManyOperations =
        mutableMapOf<KClass<out QueryManyUseCase<*>>, QueryManyUseCaseOperation<*, QueryManyUseCase<*>>>()
    private val fetchOperations =
        mutableMapOf<KClass<out QueryOneUseCase<*>>, QueryOneUseCaseOperation<*, QueryOneUseCase<*>>>()

    private val executeOneOnce: KeyedOnce<KClass<out ExecuteOneUseCase<*>>, ExecuteOneUseCaseOperation<*, ExecuteOneUseCase<*>>> =
        KeyedOnce.default()

    private val queryManyOnce: KeyedOnce<KClass<out QueryManyUseCase<*>>, QueryManyUseCaseOperation<*, QueryManyUseCase<*>>> =
        KeyedOnce.default()

    private val queryOneOnce: KeyedOnce<KClass<out QueryOneUseCase<*>>, QueryOneUseCaseOperation<*, QueryOneUseCase<*>>> =
        KeyedOnce.default()
}