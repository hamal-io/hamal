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

class DefaultUseCaseRegistryAdapter : GetCommandUseCaseHandlerPort, GetQueryUseCaseHandlerPort,
    GetFetchOneUseCaseHandlerPort, ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        event.applicationContext.getBeansOfType(CommandUseCaseHandler::class.java)
            .forEach { (_, useCaseHandler) ->
                register(useCaseHandler.useCaseClass, useCaseHandler as CommandUseCaseHandler<*, CommandUseCase>)
            }

        event.applicationContext.getBeansOfType(QueryUseCaseHandler::class.java)
            .forEach { (_, useCaseHandler) ->
                register(useCaseHandler.useCaseClass, useCaseHandler as QueryUseCaseHandler<*, QueryUseCase>)
            }

        event.applicationContext.getBeansOfType(FetchOneUseCaseHandler::class.java)
            .forEach { (_, useCaseHandler) ->
                register(useCaseHandler.useCaseClass, useCaseHandler as FetchOneUseCaseHandler<*, FetchOneUseCase>)
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : Any, USE_CASE : CommandUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): CommandUseCaseHandler<RESULT, USE_CASE> =
        commandOnce.invoke(Tuple2(resultClass, useCaseClass)) {
            val resultHandler = commandHandlerMapping[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().mapNotNull { commandHandlerMapping[it.kotlin] }.firstOrNull()
                ?: throw NotFoundException("CommandUseCaseHandler<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")

            when (resultClass) {
                Unit::class -> resultHandler
                else -> {
                    ensureCanHandleResultClass(resultHandler, resultClass)
                    resultHandler
                }
            }
        } as CommandUseCaseHandler<RESULT, USE_CASE>


    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : Any, USE_CASE : QueryUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): QueryUseCaseHandler<RESULT, USE_CASE> =
        queryOnce.invoke(Tuple2(resultClass, useCaseClass)) {
            val resultHandler = queryHandlerMapping[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().mapNotNull { queryHandlerMapping[it.kotlin] }.firstOrNull()
                ?: throw NotFoundException("QueryUseCaseHandler<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")

            when (resultClass) {
                Unit::class -> resultHandler
                else -> {
                    ensureCanHandleResultClass(resultHandler, resultClass)
                    resultHandler
                }
            }
        } as QueryUseCaseHandler<RESULT, USE_CASE>

    @Suppress("UNCHECKED_CAST")
    override fun <RESULT : Any, USE_CASE : FetchOneUseCase> get(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<USE_CASE>
    ): FetchOneUseCaseHandler<RESULT, USE_CASE> =
        fetchOneOnce.invoke(Tuple2(resultClass, useCaseClass)) {
            val resultHandler = fetchOneHandlerMapping[useCaseClass]
                ?: useCaseClass.java.interfaces.asSequence().mapNotNull { fetchOneHandlerMapping[it.kotlin] }.firstOrNull()
                ?: throw NotFoundException("FetchOneUseCaseHandler<" + resultClass.simpleName + "," + useCaseClass.simpleName + "> not found")

            when (resultClass) {
                Unit::class -> resultHandler
                else -> {
                    ensureCanHandleResultClass(resultHandler, resultClass)
                    resultHandler
                }
            }
        } as FetchOneUseCaseHandler<RESULT, USE_CASE>

    internal fun <USE_CASE : CommandUseCase> register(
        useCaseClass: KClass<out USE_CASE>,
        useCaseHandler: CommandUseCaseHandler<*, USE_CASE>
    ) {
        commandHandlerMapping[useCaseClass] = useCaseHandler
    }

    internal fun <USE_CASE : QueryUseCase> register(
        useCaseClass: KClass<out USE_CASE>,
        useCaseHandler: QueryUseCaseHandler<*, USE_CASE>
    ) {
        queryHandlerMapping[useCaseClass] = useCaseHandler
    }

    internal fun <USE_CASE : FetchOneUseCase> register(
        useCaseClass: KClass<out USE_CASE>,
        useCaseHandler: FetchOneUseCaseHandler<*, USE_CASE>
    ) {
        fetchOneHandlerMapping[useCaseClass] = useCaseHandler
    }

    private val commandHandlerMapping =
        mutableMapOf<KClass<out CommandUseCase>, CommandUseCaseHandler<*, CommandUseCase>>()
    private val queryHandlerMapping =
        mutableMapOf<KClass<out QueryUseCase>, QueryUseCaseHandler<*, QueryUseCase>>()
    private val fetchOneHandlerMapping =
        mutableMapOf<KClass<out FetchOneUseCase>, FetchOneUseCaseHandler<*, FetchOneUseCase>>()

    private val commandOnce: KeyedOnce<
            Tuple2<KClass<*>, KClass<out CommandUseCase>>,
            CommandUseCaseHandler<*, CommandUseCase>
            > = KeyedOnce.default()

    private val queryOnce: KeyedOnce<
            Tuple2<KClass<*>, KClass<out QueryUseCase>>,
            QueryUseCaseHandler<*, QueryUseCase>
            > = KeyedOnce.default()

    private val fetchOneOnce: KeyedOnce<
            Tuple2<KClass<*>, KClass<out FetchOneUseCase>>,
            FetchOneUseCaseHandler<*, FetchOneUseCase>
            > = KeyedOnce.default()
}

private fun ensureCanHandleResultClass(handler: UseCaseHandler<*, *>, resultClass: KClass<*>) {
    val resultClassesMatch = handler.resultClass == resultClass
    val isAssignable = resultClass.java.isAssignableFrom(handler.resultClass.java)
    throwIf(!resultClassesMatch && !isAssignable) {
        IllegalArgumentException("result class(${resultClass.simpleName}) does not match with UseCase result class(${handler.resultClass.simpleName})")
    }
}