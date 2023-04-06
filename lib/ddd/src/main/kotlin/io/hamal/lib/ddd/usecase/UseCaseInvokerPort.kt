package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import kotlin.reflect.KClass

interface InvokeCommandUseCasePort {

    fun <RESULT : Any, USE_CASE : CommandUseCase> command(
        resultClass: KClass<RESULT>,
        vararg useCases: USE_CASE
    ): List<RESULT>

    fun <USE_CASE : CommandUseCase> command(vararg useCases: USE_CASE)
}

interface InvokeQueryUseCasePort {
    fun <RESULT : Any, USE_CASE : QueryUseCase> query(
        resultClass: KClass<RESULT>,
        useCase: USE_CASE
    ): List<RESULT>
}

interface InvokeFetchOneUseCasePort {
    fun <RESULT : Any, USE_CASE : FetchOneUseCase> fetchOne(
        resultClass: KClass<RESULT>,
        useCase: USE_CASE
    ): Maybe<RESULT>
}

interface InvokeUseCasePort : InvokeCommandUseCasePort, InvokeQueryUseCasePort, InvokeFetchOneUseCasePort