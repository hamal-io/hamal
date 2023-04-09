package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import kotlin.reflect.KClass

interface UseCase

interface UseCaseOperation<RESULT : Any, out USE_CASE : UseCase> {
    val resultClass: KClass<RESULT>
    val useCaseClass: KClass<@UnsafeVariance USE_CASE>

    abstract class BaseImpl<RESULT : Any, out USE_CASE : UseCase>(
        override val resultClass: KClass<RESULT>,
        override val useCaseClass: KClass<@UnsafeVariance USE_CASE>
    ) : UseCaseOperation<RESULT, USE_CASE>
}

interface CommandUseCase : UseCase

abstract class CommandUseCaseOperation<RESULT : Any, out USE_CASE : CommandUseCase>(
    override val resultClass: KClass<RESULT>,
    override val useCaseClass: KClass<@UnsafeVariance USE_CASE>
) : UseCaseOperation<RESULT, USE_CASE> {

    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): List<RESULT>

    abstract class NoResultImpl<out USE_CASE : CommandUseCase>(useCaseClass: KClass<@UnsafeVariance USE_CASE>) :
        CommandUseCaseOperation<Unit, USE_CASE>(
            Unit::class, useCaseClass
        ) {

        abstract fun noResult(useCase: @UnsafeVariance USE_CASE)

        override operator fun invoke(useCase: @UnsafeVariance USE_CASE): List<Unit> {
            noResult(useCase)
            return listOf()
        }
    }
}

interface QueryUseCase : UseCase

abstract class QueryUseCaseOperation<RESULT : Any, out USE_CASE : QueryUseCase>(
    override val resultClass: KClass<RESULT>,
    override val useCaseClass: KClass<@UnsafeVariance USE_CASE>
) : UseCaseOperation<RESULT, USE_CASE> {
    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): List<RESULT>
}

interface FetchOneUseCase : UseCase

abstract class FetchOneUseCaseOperation<RESULT : Any, out USE_CASE : FetchOneUseCase>(
    override val resultClass: KClass<RESULT>,
    override val useCaseClass: KClass<@UnsafeVariance USE_CASE>
) : UseCaseOperation<RESULT, USE_CASE> {

    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): Maybe<RESULT>
}
