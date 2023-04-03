package io.hamal.lib.ddd.usecase

interface CommandUseCase

interface CommandUseCaseHandler<RESULT, USE_CASE : CommandUseCase> {

    fun handle(useCase: USE_CASE): RESULT

}

