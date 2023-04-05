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

class DefaultUseCaseRegistryAdapter : GetCommandUseCasePort, GetQueryUseCasePort,
    GetFetchOneUseCasePort, ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        event.applicationContext.getBeansOfType(CommandUseCase::class.java)
            .forEach { (_, useCase) ->
                register(useCase.payloadClass, useCase as CommandUseCase<*, CommandUseCasePayload>)
            }

        event.applicationContext.getBeansOfType(QueryUseCase::class.java)
            .forEach { (_, useCase) ->
                register(useCase.payloadClass, useCase as QueryUseCase<*, QueryUseCasePayload>)
            }

        event.applicationContext.getBeansOfType(FetchOneUseCase::class.java)
            .forEach { (_, useCase) ->
                register(useCase.payloadClass, useCase as FetchOneUseCase<*, FetchOneUseCasePayload>)
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : Any, PAYLOAD : CommandUseCasePayload> get(
        resultClass: KClass<RESULT>,
        payloadClass: KClass<PAYLOAD>
    ): CommandUseCase<RESULT, PAYLOAD> =
        commandOnce.invoke(Tuple2(resultClass, payloadClass)) {
            val useCase = commandMapping[payloadClass]
                ?: payloadClass.java.interfaces.asSequence().mapNotNull { commandMapping[it.kotlin] }.firstOrNull()
                ?: throw NotFoundException("CommandUseCase<" + resultClass.simpleName + "," + payloadClass.simpleName + "> not found")

            when (resultClass) {
                Unit::class -> useCase
                else -> {
                    ensureResultClass(useCase, resultClass)
                    useCase
                }
            }
        } as CommandUseCase<RESULT, PAYLOAD>


    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : Any, PAYLOAD : QueryUseCasePayload> get(
        resultClass: KClass<RESULT>,
        payloadClass: KClass<PAYLOAD>
    ): QueryUseCase<RESULT, PAYLOAD> =
        queryOnce.invoke(Tuple2(resultClass, payloadClass)) {
            val useCase = queryMapping[payloadClass]
                ?: payloadClass.java.interfaces.asSequence().mapNotNull { queryMapping[it.kotlin] }.firstOrNull()
                ?: throw NotFoundException("QueryUseCase<" + resultClass.simpleName + "," + payloadClass.simpleName + "> not found")

            when (resultClass) {
                Unit::class -> useCase
                else -> {
                    ensureResultClass(useCase, resultClass)
                    useCase
                }
            }
        } as QueryUseCase<RESULT, PAYLOAD>

    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : Any, PAYLOAD : FetchOneUseCasePayload> get(
        resultClass: KClass<RESULT>,
        payloadClass: KClass<PAYLOAD>
    ): FetchOneUseCase<RESULT, PAYLOAD> =
        fetchOneOnce.invoke(Tuple2(resultClass, payloadClass)) {
            val useCase = fetchOneMapping[payloadClass]
                ?: payloadClass.java.interfaces.asSequence().mapNotNull { fetchOneMapping[it.kotlin] }.firstOrNull()
                ?: throw NotFoundException("FetchOneUseCase<" + resultClass.simpleName + "," + payloadClass.simpleName + "> not found")

            when (resultClass) {
                Unit::class -> useCase
                else -> {
                    ensureResultClass(useCase, resultClass)
                    useCase
                }
            }
        } as FetchOneUseCase<RESULT, PAYLOAD>

    internal fun <PAYLOAD : CommandUseCasePayload> register(
        payloadClass: KClass<out PAYLOAD>,
        useCase: CommandUseCase<*, PAYLOAD>
    ) {
        commandMapping[payloadClass] = useCase
    }

    internal fun <PAYLOAD : QueryUseCasePayload> register(
        payloadClass: KClass<out PAYLOAD>,
        useCase: QueryUseCase<*, PAYLOAD>
    ) {
        queryMapping[payloadClass] = useCase
    }

    internal fun <PAYLOAD : FetchOneUseCasePayload> register(
        payloadClass: KClass<out PAYLOAD>,
        useCase: FetchOneUseCase<*, PAYLOAD>
    ) {
        fetchOneMapping[payloadClass] = useCase
    }

    private val commandMapping =
        mutableMapOf<KClass<out CommandUseCasePayload>, CommandUseCase<*, CommandUseCasePayload>>()
    private val queryMapping =
        mutableMapOf<KClass<out QueryUseCasePayload>, QueryUseCase<*, QueryUseCasePayload>>()
    private val fetchOneMapping =
        mutableMapOf<KClass<out FetchOneUseCasePayload>, FetchOneUseCase<*, FetchOneUseCasePayload>>()

    private val commandOnce: KeyedOnce<
            Tuple2<KClass<*>, KClass<out CommandUseCasePayload>>,
            CommandUseCase<*, CommandUseCasePayload>
            > = KeyedOnce.default()

    private val queryOnce: KeyedOnce<
            Tuple2<KClass<*>, KClass<out QueryUseCasePayload>>,
            QueryUseCase<*, QueryUseCasePayload>
            > = KeyedOnce.default()

    private val fetchOneOnce: KeyedOnce<
            Tuple2<KClass<*>, KClass<out FetchOneUseCasePayload>>,
            FetchOneUseCase<*, FetchOneUseCasePayload>
            > = KeyedOnce.default()
}

private fun ensureResultClass(useCase: UseCase<*, *>, resultClass: KClass<*>) {
    val resultClassesMatch = useCase.resultClass == resultClass
    val isAssignable = resultClass.java.isAssignableFrom(useCase.resultClass.java)
    throwIf(!resultClassesMatch && !isAssignable) {
        IllegalArgumentException("result class(${resultClass.simpleName}) does not match with use case result class(${useCase.resultClass.simpleName})")
    }
}