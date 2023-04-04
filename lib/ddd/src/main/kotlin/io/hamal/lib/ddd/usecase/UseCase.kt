package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import java.lang.reflect.ParameterizedType

enum class UseCaseType {
    Command,
    Query,
    FetchOne
}

interface UseCase {
    val type: UseCaseType
}

interface UseCaseHandler<RESULT : Any, out USE_CASE : UseCase> {
    val handlesType: UseCaseType
    val resultClass: Class<RESULT>
    val useCaseClass: Class<@UnsafeVariance USE_CASE>

    @Suppress("UNCHECKED_CAST")
    abstract class BaseImpl<RESULT : Any, out USE_CASE : UseCase> : UseCaseHandler<RESULT, USE_CASE> {
        final override val resultClass: Class<RESULT> by lazy {
            val genericSuperclass = this.javaClass.genericSuperclass as ParameterizedType
            genericSuperclass.actualTypeArguments[0] as Class<RESULT>
        }

        override val useCaseClass: Class<@UnsafeVariance USE_CASE> by lazy {
            val genericSuperclass = this.javaClass.genericSuperclass as ParameterizedType
            genericSuperclass.actualTypeArguments[1] as Class<USE_CASE>
        }
    }
}

interface CommandUseCase : UseCase {
    override val type: UseCaseType get() = UseCaseType.Command
}


interface CommandUseCaseHandler<RESULT : Any, out USE_CASE : CommandUseCase> : UseCaseHandler<RESULT, USE_CASE> {
    override val handlesType: UseCaseType get() = UseCaseType.Command

    fun handle(useCase: @UnsafeVariance USE_CASE): List<RESULT>

    abstract class BaseImpl<RESULT : Any, USE_CASE : CommandUseCase> :
        UseCaseHandler.BaseImpl<RESULT, USE_CASE>(), CommandUseCaseHandler<RESULT, USE_CASE>
}


interface QueryUseCase : UseCase {
    override val type: UseCaseType get() = UseCaseType.Query
}

interface QueryUseCaseHandler<RESULT : Any, out USE_CASE : QueryUseCase> : UseCaseHandler<RESULT, USE_CASE> {
    override val handlesType: UseCaseType get() = UseCaseType.Query

    fun handle(useCase: @UnsafeVariance USE_CASE): List<RESULT>

    abstract class BaseImpl<RESULT : Any, USE_CASE : QueryUseCase> :
        UseCaseHandler.BaseImpl<RESULT, USE_CASE>(), QueryUseCaseHandler<RESULT, USE_CASE> {
    }
}

interface FetchOneUseCase : UseCase {
    override val type: UseCaseType get() = UseCaseType.FetchOne
}

interface FetchOneUseCaseHandler<RESULT : Any, out USE_CASE : FetchOneUseCase> : UseCaseHandler<RESULT, USE_CASE> {
    override val handlesType: UseCaseType get() = UseCaseType.FetchOne

    fun handle(useCase: @UnsafeVariance USE_CASE): Maybe<RESULT>

    abstract class BaseImpl<RESULT : Any, USE_CASE : FetchOneUseCase> :
        UseCaseHandler.BaseImpl<RESULT, USE_CASE>(), FetchOneUseCaseHandler<RESULT, USE_CASE> {
    }
}
