package io.hamal.lib.ddd.usecase

import io.hamal.lib.ddd.base.DomainObject

interface InvokeExecuteOneUseCasePort {
    fun <RESULT : DomainObject, USE_CASE : ExecuteOneUseCase<RESULT>> executeOne(useCase: USE_CASE): RESULT
}

interface InvokeQueryManyUseCasePort {
    fun <RESULT : DomainObject, USE_CASE : QueryManyUseCase<RESULT>> queryMany(useCase: USE_CASE): List<RESULT>
}

interface InvokeQueryOneUseCasePort {
    fun <RESULT : DomainObject, USE_CASE : QueryOneUseCase<RESULT>> queryOne(useCase: USE_CASE): RESULT?
}

interface InvokeUseCasePort : InvokeExecuteOneUseCasePort, InvokeQueryManyUseCasePort, InvokeQueryOneUseCasePort