package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import kotlin.reflect.KClass

interface UseCasePayload

interface UseCase<RESULT : Any, out PAYLOAD : UseCasePayload> {
    val resultClass: KClass<RESULT>
    val payloadClass: KClass<@UnsafeVariance PAYLOAD>

    abstract class BaseImpl<RESULT : Any, out PAYLOAD : UseCasePayload>(
        override val resultClass: KClass<RESULT>,
        override val payloadClass: KClass<@UnsafeVariance PAYLOAD>
    ) : UseCase<RESULT, PAYLOAD>
}

interface CommandUseCasePayload : UseCasePayload

abstract class CommandUseCase<RESULT : Any, out PAYLOAD : CommandUseCasePayload>(
    override val resultClass: KClass<RESULT>,
    override val payloadClass: KClass<@UnsafeVariance PAYLOAD>
) : UseCase<RESULT, PAYLOAD> {

    abstract operator fun invoke(payload: @UnsafeVariance PAYLOAD): List<RESULT>

}


interface QueryUseCasePayload : UseCasePayload

abstract class QueryUseCase<RESULT : Any, out PAYLOAD : QueryUseCasePayload>(
    override val resultClass: KClass<RESULT>,
    override val payloadClass: KClass<@UnsafeVariance PAYLOAD>
) : UseCase<RESULT, PAYLOAD> {
    abstract operator fun invoke(payload: @UnsafeVariance PAYLOAD): List<RESULT>
}

interface FetchOneUseCasePayload : UseCasePayload

abstract class FetchOneUseCase<RESULT : Any, out PAYLOAD : FetchOneUseCasePayload>(
    override val resultClass: KClass<RESULT>,
    override val payloadClass: KClass<@UnsafeVariance PAYLOAD>
) : UseCase<RESULT, PAYLOAD> {

    abstract operator fun invoke(payload: @UnsafeVariance PAYLOAD): Maybe<RESULT>
}
