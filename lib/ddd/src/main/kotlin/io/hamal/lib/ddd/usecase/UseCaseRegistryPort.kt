package io.hamal.lib.ddd.usecase

interface UseCaseRegistryPort {

    fun <RESULT, USE_CASE : CommandUseCase> getCommandHandler(
        resultClass: Class<RESULT>,
        useCaseClass: Class<USE_CASE>
    ): CommandUseCaseHandler<RESULT, USE_CASE>

    fun <RESULT, USE_CASE : QueryUseCase> getQueryHandler(
        resultClass: Class<RESULT>,
        useCaseClass: Class<USE_CASE>
    ): QueryUseCaseHandler<RESULT, USE_CASE>

    fun <RESULT, USE_CASE : FetchOneUseCase> getFetchOneHandler(
        resultClass: Class<RESULT>,
        useCaseClass: Class<USE_CASE>
    ): FetchOneUseCaseHandler<RESULT, USE_CASE>

}



