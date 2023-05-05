package io.hamal.lib.ddd.usecase

import io.hamal.lib.ddd.base.DomainObject
import kotlin.reflect.KClass


interface GetRequestOneUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : RequestOneUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): RequestOneUseCaseHandler<RESULT, USE_CASE>
}

interface GetQueryManyUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : QueryManyUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): QueryManyUseCaseHandler<RESULT, USE_CASE>
}

interface GetQueryOneUseCasePort {
    operator fun <RESULT : DomainObject, USE_CASE : QueryOneUseCase<RESULT>> get(useCaseClass: KClass<out USE_CASE>): QueryOneUseCaseHandler<RESULT, USE_CASE>
}

interface GetUseCasePort : GetRequestOneUseCasePort, GetQueryManyUseCasePort, GetQueryOneUseCasePort