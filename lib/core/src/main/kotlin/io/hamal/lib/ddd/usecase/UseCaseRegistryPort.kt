package io.hamal.lib.ddd.usecase

import io.hamal.lib.ddd.base.DomainObject
import kotlin.reflect.KClass


interface GetCommandUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : CommandUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): CommandUseCaseOperation<RESULT, USE_CASE>
}

interface GetQueryUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : QueryUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): QueryUseCaseOperation<RESULT, USE_CASE>
}

interface GetFetchOneUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : FetchOneUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): FetchOneUseCaseOperation<RESULT, USE_CASE>
}

interface GetUseCasePort : GetCommandUseCasePort, GetQueryUseCasePort, GetFetchOneUseCasePort