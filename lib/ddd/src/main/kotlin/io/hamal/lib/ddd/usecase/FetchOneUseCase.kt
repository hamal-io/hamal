package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import java.lang.reflect.ParameterizedType

interface FetchOneUseCase

interface FetchOneUseCaseHandler<RESULT : Any, USE_CASE : FetchOneUseCase> {
    val resultClass: Class<RESULT>
    val useCaseClass: Class<USE_CASE>

    fun handle(useCase: USE_CASE): Maybe<RESULT>

    abstract class BaseImpl<RESULT : Any, USE_CASE : FetchOneUseCase> : FetchOneUseCaseHandler<RESULT, USE_CASE> {

        override val resultClass: Class<RESULT> by lazy {
            val genericSuperclass = this.javaClass.genericSuperclass as ParameterizedType
            @Suppress("UNCHECKED_CAST")
            genericSuperclass.actualTypeArguments[0] as Class<RESULT>
        }

        override val useCaseClass: Class<USE_CASE> by lazy {
            val genericSuperclass = this.javaClass.genericSuperclass as ParameterizedType
            @Suppress("UNCHECKED_CAST")
            genericSuperclass.actualTypeArguments[1] as Class<USE_CASE>
        }
    }
}

