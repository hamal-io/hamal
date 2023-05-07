package io.hamal.lib.ddd.usecase

import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.ddd.base.DomainObject
import kotlin.reflect.KClass

interface UseCase<RESULT : DomainObject<*>>

interface UseCaseHandler<RESULT : DomainObject<*>, USE_CASE : UseCase<RESULT>> {
    val useCaseClass: KClass<USE_CASE>
}

interface RequestOneUseCase<RESULT : DomainObject<*>> : UseCase<RESULT> {
    val requestId: RequestId
    val shard: Shard
}

abstract class RequestOneUseCaseHandler<RESULT : DomainObject<*>, USE_CASE : RequestOneUseCase<RESULT>>(
    override val useCaseClass: KClass<USE_CASE>
) : UseCaseHandler<RESULT, USE_CASE> {
    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): RESULT
}

interface RequestManyUseCase<RESULT : DomainObject<*>> : UseCase<RESULT> {
    val requestId: RequestId
    val shard: Shard
}

abstract class RequestManyUseCaseHandler<RESULT : DomainObject<*>, USE_CASE : RequestManyUseCase<RESULT>>(
    override val useCaseClass: KClass<USE_CASE>
) : UseCaseHandler<RESULT, USE_CASE> {
    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): List<RESULT>
}


interface QueryManyUseCase<RESULT : DomainObject<*>> : UseCase<RESULT>

abstract class QueryManyUseCaseHandler<RESULT : DomainObject<*>, USE_CASE : QueryManyUseCase<RESULT>>(
    override val useCaseClass: KClass<USE_CASE>
) : UseCaseHandler<RESULT, USE_CASE> {
    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): List<RESULT>
}

interface QueryOneUseCase<RESULT : DomainObject<*>> : UseCase<RESULT>

abstract class QueryOneUseCaseHandler<RESULT : DomainObject<*>, USE_CASE : QueryOneUseCase<RESULT>>(
    override val useCaseClass: KClass<USE_CASE>
) : UseCaseHandler<RESULT, USE_CASE> {

    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): RESULT
}
