package io.hamal.backend.infra.adapter

import io.hamal.backend.application.trigger.InvokeManualTriggerUseCase
import io.hamal.backend.core.port.GetLoggerPort
import io.hamal.backend.core.port.LogPort
import io.hamal.backend.core.port.notification.FlushDomainNotificationPort
import io.hamal.lib.KeyedOnce
import io.hamal.lib.Tuple2
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
    private val flushDomainNotificationPort: FlushDomainNotificationPort,
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
            flushDomainNotificationPort.flush()
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
//        event.applicationContext.getBeansOfType(CommandUseCaseOperation::class.java)
//            .forEach { (_, operation) ->
//                register(operation.useCaseClass, operation as CommandUseCaseOperation<*, CommandUseCase<DomainObject>>)
//            }

//        event.applicationContext.getBeansOfType(QueryUseCaseOperation::class.java)
//            .forEach { (_, useCase) ->
//                register(useCase.useCaseClass, useCase as QueryUseCaseOperation<*, QueryUseCase<*>>)
//            }
//
//        event.applicationContext.getBeansOfType(FetchOneUseCaseOperation::class.java)
//            .forEach { (_, useCase) ->
//                register(useCase.useCaseClass, useCase as FetchOneUseCaseOperation<*, FetchOneUseCase<*>>)
//            }
    }

    override fun <RESULT : DomainObject, USE_CASE : CommandUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): CommandUseCaseOperation<RESULT, USE_CASE> {
        val operation = commandOperations[useCaseClass]
            ?: useCaseClass.java.interfaces.asSequence().mapNotNull { commandOperations[it.kotlin] }.firstOrNull()

        check(operation != null) { "Unable to find operation" }

        return operation as CommandUseCaseOperation<RESULT, USE_CASE>!!
    }

//    override fun <RESULT : DomainObject, USE_CASE : CommandUseCase<RESULT>> get(useCaseClass: KClass<USE_CASE>): CommandUseCaseOperation<RESULT, USE_CASE> {
//        TODO("Not yet implemented")
//    }

//    override fun <RESULT : DomainObject, USE_CASE : CommandUseCase<RESULT>> get(useCaseClass: KClass<USE_CASE>)
//            : CommandUseCaseOperation<RESULT, USE_CASE> {
//        val operation = commandOperations[useCaseClass]
//            ?: useCaseClass.java.interfaces.asSequence().mapNotNull { commandOperations[it.kotlin] }.firstOrNull()
//
//        check(operation != null) { "Unable to find operation" }
//
//        //FIXME
//        return operation as CommandUseCaseOperation<RESULT, USE_CASE>
//    }
//        commandOnce.invoke(useCaseClass) {
//            val operation = commandOperations[useCaseClass]
//                ?: useCaseClass.java.interfaces.asSequence().mapNotNull { commandOperations[it.kotlin] }.firstOrNull()
//                ?: throw IllegalStateException("CommandUseCaseOperation<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")
//
//            when (resultClass) {
//                Unit::class -> operation
//                else -> {
//                    ensureResultClass(operation, resultClass)
//                    operation
//                }
//            }
//        } as CommandUseCaseOperation<RESULT, USE_CASE>


    override fun <RESULT : DomainObject, USE_CASE : QueryUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>)
            : QueryUseCaseOperation<RESULT, USE_CASE> = TODO()
//        queryOnce.invoke(Tuple2(resultClass, useCaseClass)) {
//            val operation = queryOperations[useCaseClass]
//                ?: useCaseClass.java.interfaces.asSequence().mapNotNull { queryOperations[it.kotlin] }.firstOrNull()
//                ?: throw IllegalStateException("QueryUseCaseOperation<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")
//
//            when (resultClass) {
//                Unit::class -> throw IllegalArgumentException("Result class can not be ${resultClass.simpleName}")
//                else -> {
//                    ensureResultClass(operation, resultClass)
//                    operation
//                }
//            }
//        } as QueryUseCaseOperation<RESULT, USE_CASE>

    override fun <RESULT : DomainObject, USE_CASE : FetchOneUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>)
            : FetchOneUseCaseOperation<RESULT, USE_CASE> = TODO()
//        fetchOneOnce.invoke(Tuple2(resultClass, useCaseClass)) {
//            val operation = fetchOperations[useCaseClass]
//                ?: useCaseClass.java.interfaces.asSequence().mapNotNull { fetchOperations[it.kotlin] }.firstOrNull()
//                ?: throw IllegalStateException("FetchOneUseCaseOperation<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")
//
//            when (resultClass) {
//                Unit::class -> throw IllegalArgumentException("Result class can not be ${resultClass.simpleName}")
//                else -> {
//                    ensureResultClass(operation, resultClass)
//                    operation
//                }
//            }
//        } as FetchOneUseCaseOperation<RESULT, USE_CASE>

//    fun <RESULT : DomainObject, USE_CASE : CommandUseCase<RESULT>> register(
//        useCaseClass: KClass<out CommandUseCase<*>>,
//        operation: CommandUseCaseOperation<*, USE_CASE>
//    ) {
//        commandOperations[useCaseClass] = operation as CommandUseCaseOperation<*, CommandUseCase<*>>
//    }

    //    fun <USE_CASE : QueryUseCase> register(
//        useCaseClass: KClass<out USE_CASE>,
//        operation: QueryUseCaseOperation<*, USE_CASE>
//    ) {
//        queryOperations[useCaseClass] = operation
//    }
//
//    fun <USE_CASE : FetchOneUseCase> register(
//        useCaseClass: KClass<out USE_CASE>,
//        operation: FetchOneUseCaseOperation<*, USE_CASE>
//    ) {
//        fetchOperations[useCaseClass] = operation
//    }
//
//    private val commandOperations =
//        mutableMapOf<KClass<out CommandUseCase<*>>, CommandUseCaseOperation<*, CommandUseCase<*>>>()

    private val commandOperations =
        mutableMapOf<KClass<out UseCase<*>>, UseCaseOperation<*, UseCase<*>>>(
            InvokeManualTriggerUseCase::class to InvokeManualTriggerUseCase.Operation() as UseCaseOperation<*, UseCase<*>>
        )

//    private val queryOperations =
//        mutableMapOf<KClass<QueryUseCase<*>>, QueryUseCaseOperation<*, QueryUseCase<*>>>()
//    private val fetchOperations =
//        mutableMapOf<KClass<FetchOneUseCase<*>>, FetchOneUseCaseOperation<*, FetchOneUseCase<*>>>()

    private val commandOnce: KeyedOnce<
            KClass<CommandUseCase<*>>,
            CommandUseCaseOperation<*, CommandUseCase<*>>
            > = KeyedOnce.default()

    private val queryOnce: KeyedOnce<
            Tuple2<KClass<*>, KClass<QueryUseCase<*>>>,
            QueryUseCaseOperation<*, QueryUseCase<*>>
            > = KeyedOnce.default()

    private val fetchOneOnce: KeyedOnce<
            Tuple2<KClass<*>, KClass<out FetchOneUseCase<*>>>,
            FetchOneUseCaseOperation<*, FetchOneUseCase<*>>
            > = KeyedOnce.default()
}