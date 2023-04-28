package io.hamal.lib.ddd.usecase

import io.hamal.lib.ddd.base.DomainObject
import kotlin.reflect.KClass

interface UseCase<RESULT : DomainObject>

interface UseCaseOperation<RESULT : DomainObject, USE_CASE : UseCase<RESULT>> {
    val useCaseClass: KClass<USE_CASE>
}

interface CommandUseCase<RESULT : DomainObject> : UseCase<RESULT>

abstract class CommandUseCaseOperation<RESULT : DomainObject, USE_CASE : CommandUseCase<RESULT>>(
    override val useCaseClass: KClass<USE_CASE>
) : UseCaseOperation<RESULT, USE_CASE> {
    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): RESULT
}

interface QueryUseCase<RESULT : DomainObject> : UseCase<RESULT>

abstract class QueryUseCaseOperation<RESULT : DomainObject, USE_CASE : QueryUseCase<RESULT>>(
    override val useCaseClass: KClass<USE_CASE>
) : UseCaseOperation<RESULT, USE_CASE> {
    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): List<RESULT>
}

interface FetchOneUseCase<RESULT : DomainObject> : UseCase<RESULT>

abstract class FetchOneUseCaseOperation<RESULT : DomainObject, USE_CASE : FetchOneUseCase<RESULT>>(
    override val useCaseClass: KClass<USE_CASE>
) : UseCaseOperation<RESULT, USE_CASE> {

    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): RESULT?
}
