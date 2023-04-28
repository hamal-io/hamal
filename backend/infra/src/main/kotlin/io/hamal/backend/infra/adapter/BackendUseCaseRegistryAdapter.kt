package io.hamal.backend.infra.adapter

import io.hamal.lib.Tuple2
import io.hamal.lib.ddd.usecase.*
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class BackendUseCaseRegistryAdapter : GetUseCasePort, ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        event.applicationContext.getBeansOfType(CommandUseCaseOperation::class.java)
            .forEach { (_, useCase) ->
                register(useCase.useCaseClass, useCase as CommandUseCaseOperation<*, CommandUseCase>)
            }

        event.applicationContext.getBeansOfType(QueryUseCaseOperation::class.java)
            .forEach { (_, useCase) ->
                register(useCase.useCaseClass, useCase as QueryUseCaseOperation<*, QueryUseCase>)
            }

        event.applicationContext.getBeansOfType(FetchOneUseCaseOperation::class.java)
            .forEach { (_, useCase) ->
                register(useCase.useCaseClass, useCase as FetchOneUseCaseOperation<*, FetchOneUseCase>)
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : Any, USE_CASE : CommandUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): CommandUseCaseOperation<RESULT, USE_CASE> =
        commandOnce.invoke(Tuple2(resultClass, useCaseClass)) {
            val operation = commandOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().mapNotNull { commandOperations[it.kotlin] }.firstOrNull()
                ?: throw IllegalStateException("CommandUseCaseOperation<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")

            when (resultClass) {
                Unit::class -> operation
                else -> {
                    ensureResultClass(operation, resultClass)
                    operation
                }
            }
        } as CommandUseCaseOperation<RESULT, USE_CASE>


    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : Any, USE_CASE : QueryUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): QueryUseCaseOperation<RESULT, USE_CASE> =
        queryOnce.invoke(Tuple2(resultClass, useCaseClass)) {
            val operation = queryOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().mapNotNull { queryOperations[it.kotlin] }.firstOrNull()
                ?: throw IllegalStateException("QueryUseCaseOperation<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")

            when (resultClass) {
                Unit::class -> throw IllegalArgumentException("Result class can not be ${resultClass.simpleName}")
                else -> {
                    ensureResultClass(operation, resultClass)
                    operation
                }
            }
        } as QueryUseCaseOperation<RESULT, USE_CASE>

    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : Any, USE_CASE : FetchOneUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): FetchOneUseCaseOperation<RESULT, USE_CASE> =
        fetchOneOnce.invoke(Tuple2(resultClass, useCaseClass)) {
            val operation = fetchOperations[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().mapNotNull { fetchOperations[it.kotlin] }.firstOrNull()
                ?: throw IllegalStateException("FetchOneUseCaseOperation<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")

            when (resultClass) {
                Unit::class -> throw IllegalArgumentException("Result class can not be ${resultClass.simpleName}")
                else -> {
                    ensureResultClass(operation, resultClass)
                    operation
                }
            }
        } as FetchOneUseCaseOperation<RESULT, USE_CASE>

    fun <USE_CASE : CommandUseCase> register(
        useCaseClass: KClass<out USE_CASE>,
        operation: CommandUseCaseOperation<*, USE_CASE>
    ) {
        commandOperations[useCaseClass] = operation
    }

    fun <USE_CASE : QueryUseCase> register(
        useCaseClass: KClass<out USE_CASE>,
        operation: QueryUseCaseOperation<*, USE_CASE>
    ) {
        queryOperations[useCaseClass] = operation
    }

    fun <USE_CASE : FetchOneUseCase> register(
        useCaseClass: KClass<out USE_CASE>,
        operation: FetchOneUseCaseOperation<*, USE_CASE>
    ) {
        fetchOperations[useCaseClass] = operation
    }

    private val commandOperations =
        mutableMapOf<KClass<out CommandUseCase>, CommandUseCaseOperation<*, CommandUseCase>>()
    private val queryOperations =
        mutableMapOf<KClass<out QueryUseCase>, QueryUseCaseOperation<*, QueryUseCase>>()
    private val fetchOperations =
        mutableMapOf<KClass<out FetchOneUseCase>, FetchOneUseCaseOperation<*, FetchOneUseCase>>()

    private val commandOnce: io.hamal.lib.KeyedOnce<
            Tuple2<KClass<*>, KClass<out CommandUseCase>>,
            CommandUseCaseOperation<*, CommandUseCase>
            > = io.hamal.lib.KeyedOnce.default()

    private val queryOnce: io.hamal.lib.KeyedOnce<
            Tuple2<KClass<*>, KClass<out QueryUseCase>>,
            QueryUseCaseOperation<*, QueryUseCase>
            > = io.hamal.lib.KeyedOnce.default()

    private val fetchOneOnce: io.hamal.lib.KeyedOnce<
            Tuple2<KClass<*>, KClass<out FetchOneUseCase>>,
            FetchOneUseCaseOperation<*, FetchOneUseCase>
            > = io.hamal.lib.KeyedOnce.default()
}

private fun ensureResultClass(useCaseOperation: UseCaseOperation<*, *>, resultClass: KClass<*>) {
    val resultClassesMatch = useCaseOperation.resultClass == resultClass
    val isAssignable = resultClass.java.isAssignableFrom(useCaseOperation.resultClass.java)
    require(resultClassesMatch || isAssignable) {
        IllegalArgumentException("result class(${resultClass.simpleName}) does not match with use case result class(${useCaseOperation.resultClass.simpleName})")
    }
}