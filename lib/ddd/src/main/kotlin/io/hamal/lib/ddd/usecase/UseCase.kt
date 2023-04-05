package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import kotlin.reflect.KClass

interface UseCase

interface UseCaseHandler<RESULT : Any, out USE_CASE : UseCase> {
    val resultClass: KClass<RESULT>
    val useCaseClass: KClass<@UnsafeVariance USE_CASE>

    abstract class BaseImpl<RESULT : Any, out USE_CASE : UseCase>(
        override val resultClass: KClass<RESULT>,
        override val useCaseClass: KClass<@UnsafeVariance USE_CASE>
    ) : UseCaseHandler<RESULT, USE_CASE>
}

interface CommandUseCase : UseCase

interface CommandUseCaseHandler<RESULT : Any, out USE_CASE : CommandUseCase> : UseCaseHandler<RESULT, USE_CASE> {

    fun handle(useCase: @UnsafeVariance USE_CASE): List<RESULT>

    abstract class BaseImpl<RESULT : Any, USE_CASE : CommandUseCase>(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<@UnsafeVariance USE_CASE>
    ) : UseCaseHandler.BaseImpl<RESULT, USE_CASE>(
        resultClass,
        useCaseClass
    ), CommandUseCaseHandler<RESULT, USE_CASE>
}


interface QueryUseCase : UseCase

interface QueryUseCaseHandler<RESULT : Any, out USE_CASE : QueryUseCase> : UseCaseHandler<RESULT, USE_CASE> {

    fun handle(useCase: @UnsafeVariance USE_CASE): List<RESULT>

    abstract class BaseImpl<RESULT : Any, USE_CASE : QueryUseCase>(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<@UnsafeVariance USE_CASE>
    ) : UseCaseHandler.BaseImpl<RESULT, USE_CASE>(
        resultClass,
        useCaseClass
    ), QueryUseCaseHandler<RESULT, USE_CASE>
}

interface FetchOneUseCase : UseCase

interface FetchOneUseCaseHandler<RESULT : Any, out USE_CASE : FetchOneUseCase> : UseCaseHandler<RESULT, USE_CASE> {

    fun handle(useCase: @UnsafeVariance USE_CASE): Maybe<RESULT>

    abstract class BaseImpl<RESULT : Any, USE_CASE : FetchOneUseCase>(
        resultClass: KClass<RESULT>,
        useCaseClass: KClass<@UnsafeVariance USE_CASE>
    ) : UseCaseHandler.BaseImpl<RESULT, USE_CASE>(
        resultClass,
        useCaseClass
    ), FetchOneUseCaseHandler<RESULT, USE_CASE>
}
