package io.hamal.lib.ddd.usecase

import io.hamal.lib.ddd.base.DomainObject

interface InvokeExecuteOneUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : ExecuteOneUseCase<RESULT>> invoke(useCase: USE_CASE): RESULT
}

interface InvokeQueryManyUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : QueryManyUseCase<RESULT>> invoke(useCase: USE_CASE): List<RESULT>
}

interface InvokeQueryOneUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : QueryOneUseCase<RESULT>> invoke(useCase: USE_CASE): RESULT
}

interface InvokeUseCasePort : InvokeExecuteOneUseCasePort, InvokeQueryManyUseCasePort, InvokeQueryOneUseCasePort