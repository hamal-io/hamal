package io.hamal.lib.ddd.usecase

import io.hamal.lib.ddd.base.DomainObject
import kotlin.reflect.KClass


interface GetExecuteOneUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : ExecuteOneUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): ExecuteOneUseCaseOperation<RESULT, USE_CASE>
}

interface GetQueryManyUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : QueryManyUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): QueryManyUseCaseOperation<RESULT, USE_CASE>
}

interface GetQueryOneUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : QueryOneUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): QueryOneUseCaseOperation<RESULT, USE_CASE>
}

interface GetUseCasePort : GetExecuteOneUseCasePort, GetQueryManyUseCasePort, GetQueryOneUseCasePort