package io.hamal.lib.ddd.usecase

interface UseCaseRegistry {
    fun <RESULT, USE_CASE : CommandUseCase> getCommandHandler(
        resultClass: Class<RESULT>,
        useCaseClass: Class<USE_CASE>
    ): CommandUseCaseHandler<RESULT, USE_CASE>
}

