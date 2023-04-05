package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import kotlin.reflect.KClass

interface UseCasePayload

interface UseCase<RESULT : Any, out PAYLOAD : UseCasePayload> {
    val resultClass: KClass<RESULT>
    val payload: PAYLOAD

    abstract class BaseImpl<RESULT : Any, out PAYLOAD : UseCasePayload>(
        override val resultClass: KClass<RESULT>,
        override val payload: PAYLOAD
    ) : UseCase<RESULT, PAYLOAD>
}

interface CommandUseCasePayload : UseCasePayload

interface CommandUseCase<RESULT : Any, out PAYLOAD : CommandUseCasePayload> :
    UseCase<RESULT, PAYLOAD> {

    operator fun invoke(payload: @UnsafeVariance PAYLOAD): List<RESULT>

    abstract class BaseImpl<RESULT : Any, PAYLOAD : CommandUseCasePayload>(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ) : UseCase.BaseImpl<RESULT, PAYLOAD>(resultClass, payload), CommandUseCase<RESULT, PAYLOAD>
}


interface QueryUseCasePayload : UseCasePayload

interface QueryUseCase<RESULT : Any, out PAYLOAD : QueryUseCasePayload> :
    UseCase<RESULT, PAYLOAD> {

    operator fun invoke(payload: @UnsafeVariance PAYLOAD): List<RESULT>

    abstract class BaseImpl<RESULT : Any, PAYLOAD : QueryUseCasePayload>(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ) : UseCase.BaseImpl<RESULT, PAYLOAD>(resultClass, payload), QueryUseCase<RESULT, PAYLOAD>
}

interface FetchOneUseCasePayload : UseCasePayload

interface FetchOneUseCase<RESULT : Any, out PAYLOAD : FetchOneUseCasePayload> :
    UseCase<RESULT, PAYLOAD> {

    operator fun invoke(payload: @UnsafeVariance PAYLOAD): Maybe<RESULT>

    abstract class BaseImpl<RESULT : Any, PAYLOAD : FetchOneUseCasePayload>(
        resultClass: KClass<RESULT>,
        payload: PAYLOAD
    ) : UseCase.BaseImpl<RESULT, PAYLOAD>(resultClass, payload), FetchOneUseCase<RESULT, PAYLOAD>
}
