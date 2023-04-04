package io.hamal.application.impl

import io.hamal.lib.ddd.usecase.*
import io.hamal.lib.meta.KeyedOnce
import io.hamal.lib.meta.Tuple2
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent

class DefaultUseCaseRegistryImpl :
    GetCommandUseCaseHandlerPort, GetQueryUseCaseHandlerPort, GetFetchOneUseCaseHandlerPort,
    ApplicationListener<ContextRefreshedEvent> {

    private val commandHandlerMapping:
            Map<Class<CommandUseCase>, CommandUseCaseHandler<*, CommandUseCase>> = HashMap()
    private val queryHandlerMapping:
            Map<Class<QueryUseCase>, QueryUseCaseHandler<*, QueryUseCase>> = HashMap()
    private val fetchOneHandlerMapping:
            Map<Class<FetchOneUseCase>, FetchOneUseCaseHandler<*, FetchOneUseCase>> = HashMap()

    private val commandOnce: KeyedOnce<Tuple2<Class<*>, Class<CommandUseCase>>, CommandUseCaseHandler<*, *>> =
        KeyedOnce.default()
//    private val queryOnce: KeyedOnce<Tuple2<Class<*>, Class<out QueryUseCase?>>, QueryUseCaseHandler<*, *>> =
//        KeyedOnce.newInstance()
//    private val fetchOnce: KeyedOnce<Tuple2<Class<*>, Class<out FetchOneUseCase?>>, FetchOneUseCaseHandler<*, *>> =
//        KeyedOnce.newInstance()



    override fun <RESULT : Any, USE_CASE : CommandUseCase> get(
        resultClass: Class<RESULT>,
        useCaseClass: Class<USE_CASE>
    ): CommandUseCaseHandler<RESULT, USE_CASE> {
        TODO("Not yet implemented")
    }

    override fun <RESULT : Any, USE_CASE : QueryUseCase> get(
        resultClass: Class<RESULT>,
        useCaseClass: Class<USE_CASE>
    ): QueryUseCaseHandler<RESULT, USE_CASE> {
        TODO("Not yet implemented")
    }

    override fun <RESULT : Any, USE_CASE : FetchOneUseCase> get(
        resultClass: Class<RESULT>,
        useCaseClass: Class<USE_CASE>
    ): FetchOneUseCaseHandler<RESULT, USE_CASE> {
        TODO("Not yet implemented")
    }


    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        TODO("Not yet implemented")
    }

}