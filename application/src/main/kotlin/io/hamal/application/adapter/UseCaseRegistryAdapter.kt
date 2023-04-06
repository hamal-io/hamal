package io.hamal.application.adapter

import io.hamal.lib.ddd.usecase.*
import io.hamal.lib.meta.KeyedOnce
import io.hamal.lib.meta.Tuple2
import io.hamal.lib.meta.exception.IllegalArgumentException
import io.hamal.lib.meta.exception.NotFoundException
import io.hamal.lib.meta.exception.throwIf
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import kotlin.reflect.KClass

class DefaultUseCaseRegistryAdapter : GetUseCasePort, ApplicationListener<ContextRefreshedEvent> {

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
                ?: throw NotFoundException("CommandUseCaseOperation<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")

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
                ?: throw NotFoundException("QueryUseCaseOperation<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")

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
                ?: throw NotFoundException("FetchOneUseCaseOperation<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")

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

    private val commandOnce: KeyedOnce<
            Tuple2<KClass<*>, KClass<out CommandUseCase>>,
            CommandUseCaseOperation<*, CommandUseCase>
            > = KeyedOnce.default()

    private val queryOnce: KeyedOnce<
            Tuple2<KClass<*>, KClass<out QueryUseCase>>,
            QueryUseCaseOperation<*, QueryUseCase>
            > = KeyedOnce.default()

    private val fetchOneOnce: KeyedOnce<
            Tuple2<KClass<*>, KClass<out FetchOneUseCase>>,
            FetchOneUseCaseOperation<*, FetchOneUseCase>
            > = KeyedOnce.default()
}

private fun ensureResultClass(useCaseOperation: UseCaseOperation<*, *>, resultClass: KClass<*>) {
    val resultClassesMatch = useCaseOperation.resultClass == resultClass
    val isAssignable = resultClass.java.isAssignableFrom(useCaseOperation.resultClass.java)
    throwIf(!resultClassesMatch && !isAssignable) {
        IllegalArgumentException("result class(${resultClass.simpleName}) does not match with use case result class(${useCaseOperation.resultClass.simpleName})")
    }
}