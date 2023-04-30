package io.hamal.lib.ddd.usecase

import io.hamal.lib.ddd.base.DomainObject
import java.math.BigInteger
import kotlin.reflect.KClass

interface UseCase<RESULT : DomainObject>

interface UseCaseHandler<RESULT : DomainObject, USE_CASE : UseCase<RESULT>> {
    val useCaseClass: KClass<USE_CASE>
}

@JvmInline
value class RequestId(val value: BigInteger) {
    constructor(value: ByteArray) : this(BigInteger(value))
    constructor(value: Int) : this(value.toBigInteger())
}

interface RequestOneUseCase<RESULT : DomainObject> : UseCase<RESULT> {
    val requestId: RequestId
}

abstract class RequestOneUseCaseHandler<RESULT : DomainObject, USE_CASE : RequestOneUseCase<RESULT>>(
    override val useCaseClass: KClass<USE_CASE>
) : UseCaseHandler<RESULT, USE_CASE> {
    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): RESULT
}

interface QueryManyUseCase<RESULT : DomainObject> : UseCase<RESULT>

abstract class QueryManyUseCaseHandler<RESULT : DomainObject, USE_CASE : QueryManyUseCase<RESULT>>(
    override val useCaseClass: KClass<USE_CASE>
) : UseCaseHandler<RESULT, USE_CASE> {
    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): List<RESULT>
}

interface QueryOneUseCase<RESULT : DomainObject> : UseCase<RESULT>

abstract class QueryOneUseCaseHandler<RESULT : DomainObject, USE_CASE : QueryOneUseCase<RESULT>>(
    override val useCaseClass: KClass<USE_CASE>
) : UseCaseHandler<RESULT, USE_CASE> {

    abstract operator fun invoke(useCase: @UnsafeVariance USE_CASE): RESULT
}
