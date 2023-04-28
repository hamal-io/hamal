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
    private val getCommandUseCase: GetCommandUseCasePort,
    private val getQueryUseCase: GetQueryUseCasePort,
    private val getFetchOneUseCase: GetFetchOneUseCasePort,
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


    override fun <RESULT : DomainObject, USE_CASE : CommandUseCase<RESULT>> command(useCase: USE_CASE): RESULT {
        val operation: CommandUseCaseOperation<RESULT, USE_CASE> = getCommandUseCase[useCase::class]
        logUseCaseInvocation(useCase)
        return operation(useCase).let {
            flushDomainNotifications()
            it
        }
    }

    override fun <RESULT : DomainObject, USE_CASE : QueryUseCase<RESULT>> query(useCase: USE_CASE): List<RESULT> {
//        logUseCaseInvocation(useCase)
//        return getQueryUseCasePort[useCase::class](useCase)
        TODO()
    }

    override fun <RESULT : DomainObject, USE_CASE : FetchOneUseCase<RESULT>> fetchOne(useCase: USE_CASE): RESULT? {
//        logUseCaseInvocation(useCase)
//        return getFetchOneUseCasePort[useCase::class](useCase)
        TODO()
    }

    private fun logUseCaseInvocation(useCase: UseCase<*>) {
        log.trace("Invoking command use case $useCase")
    }
}

@Component
class BackendUseCaseRegistryAdapter : GetUseCasePort, ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        event.applicationContext.getBeansOfType(CommandUseCaseOperation::class.java)
            .forEach { (_, operation) ->
                register(operation.useCaseClass, operation as CommandUseCaseOperation<*, *>)
            }

        event.applicationContext.getBeansOfType(QueryUseCaseOperation::class.java)
            .forEach { (_, operation) ->
                register(operation.useCaseClass, operation as QueryUseCaseOperation<*, *>)
            }

        event.applicationContext.getBeansOfType(FetchOneUseCaseOperation::class.java)
            .forEach { (_, operation) ->
                register(operation.useCaseClass, operation as FetchOneUseCaseOperation<*, *>)
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : DomainObject, USE_CASE : CommandUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): CommandUseCaseOperation<RESULT, USE_CASE> =
        commandOnce(useCaseClass) {
            val operation = commandOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().firstOrNull { commandOperations[it.kotlin] != null }

            check(operation != null) { "No operation registered for $useCaseClass" }

            operation as CommandUseCaseOperation<*, CommandUseCase<*>>
        } as CommandUseCaseOperation<RESULT, USE_CASE>

    override fun <RESULT : DomainObject, USE_CASE : QueryUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): QueryUseCaseOperation<RESULT, USE_CASE> =
        queryOnce(useCaseClass) {
            val operation = queryOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().firstOrNull { queryOperations[it.kotlin] != null }

            check(operation != null) { "No operation registered for $useCaseClass" }

            operation as QueryUseCaseOperation<*, QueryUseCase<*>>
        } as QueryUseCaseOperation<RESULT, USE_CASE>

    override fun <RESULT : DomainObject, USE_CASE : FetchOneUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): FetchOneUseCaseOperation<RESULT, USE_CASE> =
        fetchOneOnce(useCaseClass) {
            val operation = fetchOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().firstOrNull { fetchOperations[it.kotlin] != null }

            check(operation != null) { "No operation registered for $useCaseClass" }

            operation as FetchOneUseCaseOperation<*, FetchOneUseCase<*>>
        } as FetchOneUseCaseOperation<RESULT, USE_CASE>

    @Suppress("UNCHECKED_CAST")
    internal fun register(
        useCaseClass: KClass<out CommandUseCase<*>>, operation: CommandUseCaseOperation<*, *>
    ) {
        check(operation.useCaseClass == useCaseClass)
        commandOperations[useCaseClass] = operation as CommandUseCaseOperation<*, CommandUseCase<*>>
    }

    @Suppress("UNCHECKED_CAST")
    internal fun register(useCaseClass: KClass<out QueryUseCase<*>>, operation: QueryUseCaseOperation<*, *>) {
        check(operation.useCaseClass == useCaseClass)
        queryOperations[useCaseClass] = operation as QueryUseCaseOperation<*, QueryUseCase<*>>
    }

    @Suppress("UNCHECKED_CAST")
    internal fun register(useCaseClass: KClass<out FetchOneUseCase<*>>, operation: FetchOneUseCaseOperation<*, *>) {
        check(operation.useCaseClass == useCaseClass)
        fetchOperations[useCaseClass] = operation as FetchOneUseCaseOperation<*, FetchOneUseCase<*>>
    }


    private val commandOperations =
        mutableMapOf<KClass<out UseCase<*>>, CommandUseCaseOperation<*, CommandUseCase<*>>>()
    private val queryOperations = mutableMapOf<KClass<out QueryUseCase<*>>, QueryUseCaseOperation<*, QueryUseCase<*>>>()
    private val fetchOperations =
        mutableMapOf<KClass<out FetchOneUseCase<*>>, FetchOneUseCaseOperation<*, FetchOneUseCase<*>>>()

    private val commandOnce: KeyedOnce<KClass<out CommandUseCase<*>>, CommandUseCaseOperation<*, CommandUseCase<*>>> =
        KeyedOnce.default()

    private val queryOnce: KeyedOnce<KClass<out QueryUseCase<*>>, QueryUseCaseOperation<*, QueryUseCase<*>>> =
        KeyedOnce.default()

    private val fetchOneOnce: KeyedOnce<KClass<out FetchOneUseCase<*>>, FetchOneUseCaseOperation<*, FetchOneUseCase<*>>> =
        KeyedOnce.default()
}