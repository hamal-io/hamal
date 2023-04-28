package io.hamal.lib.ddd.usecase

import io.hamal.lib.ddd.base.DomainObject

interface InvokeCommandUseCasePort {
    fun <RESULT : DomainObject, USE_CASE : CommandUseCase<RESULT>> command(useCase: USE_CASE): RESULT
}

interface InvokeQueryUseCasePort {
    fun <RESULT : DomainObject, USE_CASE : QueryUseCase<RESULT>> query(useCase: USE_CASE): List<RESULT>
}

interface InvokeFetchOneUseCasePort {
    fun <RESULT : DomainObject, USE_CASE : FetchOneUseCase<RESULT>> fetchOne(useCase: USE_CASE): RESULT?
}

interface InvokeUseCasePort : InvokeCommandUseCasePort, InvokeQueryUseCasePort, InvokeFetchOneUseCasePort