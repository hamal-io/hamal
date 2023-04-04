package io.hamal.lib.ddd.usecase


interface GetCommandUseCaseHandlerPort {
    operator fun <RESULT : Any, USE_CASE : CommandUseCase> get(
        resultClass: Class<RESULT>,
        useCaseClass: Class<USE_CASE>
    ): CommandUseCaseHandler<RESULT, USE_CASE>
}

interface GetQueryUseCaseHandlerPort {
    operator fun <RESULT : Any, USE_CASE : QueryUseCase> get(
        resultClass: Class<RESULT>,
        useCaseClass: Class<USE_CASE>
    ): QueryUseCaseHandler<RESULT, USE_CASE>
}

interface GetFetchOneUseCaseHandlerPort {
    operator fun <RESULT : Any, USE_CASE : FetchOneUseCase> get(
        resultClass: Class<RESULT>,
        useCaseClass: Class<USE_CASE>
    ): FetchOneUseCaseHandler<RESULT, USE_CASE>
}