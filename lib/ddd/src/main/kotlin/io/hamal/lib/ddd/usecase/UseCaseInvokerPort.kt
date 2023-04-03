package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe

interface CommandUseCaseInvokerPort {
    fun <RESULT, USE_CASE : CommandUseCase> maybe(
        useCase: USE_CASE,
        resultClass: Class<RESULT>
    ): Maybe<RESULT>

    fun <RESULT, USE_CASE : CommandUseCase?> list(
        useCase: USE_CASE,
        resultClass: Class<RESULT>
    ): List<RESULT>

    fun <RESULT, USE_CASE : CommandUseCase?> commands(
        useCases: Collection<USE_CASE>,
        resultClass: Class<RESULT>
    ): List<RESULT>

    fun <USE_CASE : CommandUseCase> noResultCommand(useCase: USE_CASE) {
        maybe(useCase, Unit::class.java)
    }
}

interface QueryUseCaseInvokerPort {
    fun <RESULT, USE_CASE : QueryUseCase> query(
        useCase: USE_CASE,
        resultClass: Class<RESULT>
    ): List<RESULT>
}

interface FetchOneUseCaseInvokerPort {
    fun <RESULT, USE_CASE : FetchOneUseCase> fetchOne(
        useCase: USE_CASE,
        resultClass: Class<RESULT>
    ): Maybe<RESULT>
}