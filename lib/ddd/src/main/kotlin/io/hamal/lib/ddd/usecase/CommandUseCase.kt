package io.hamal.lib.ddd.usecase

import io.hamal.lib.meta.Maybe
import java.lang.reflect.ParameterizedType

interface CommandUseCase

interface CommandUseCaseHandler<RESULT, USE_CASE : CommandUseCase> {
    val resultClass: Class<RESULT>
    val useCaseClass: Class<USE_CASE>

    fun handle(useCase: USE_CASE): RESULT

    abstract class MaybeImpl<RESULT, USE_CASE : CommandUseCase> : CommandUseCaseHandler<Maybe<RESULT>, USE_CASE> {

        @Suppress("UNCHECKED_CAST")
        override val resultClass: Class<Maybe<RESULT>> = Maybe::class.java as Class<Maybe<RESULT>>

        val innerResultClass: Class<Maybe<RESULT>> by lazy {
            val genericSuperclass = this.javaClass.genericSuperclass as ParameterizedType

            @Suppress("UNCHECKED_CAST")
            val actualResultClass = genericSuperclass.actualTypeArguments[0] as Class<RESULT>
            @Suppress("UNCHECKED_CAST")
            actualResultClass as Class<Maybe<RESULT>>
        }

        override val useCaseClass: Class<USE_CASE> by lazy {
            val genericSuperclass = this.javaClass.genericSuperclass as ParameterizedType
            @Suppress("UNCHECKED_CAST")
            genericSuperclass.actualTypeArguments[1] as Class<USE_CASE>
        }
    }

    abstract class ListImpl<RESULT, USE_CASE : CommandUseCase> : CommandUseCaseHandler<List<RESULT>, USE_CASE> {

        @Suppress("UNCHECKED_CAST")
        override val resultClass: Class<List<RESULT>> = List::class.java as Class<List<RESULT>>

        val innerResultClass: Class<List<RESULT>> by lazy {
            val genericSuperclass = this.javaClass.genericSuperclass as ParameterizedType

            @Suppress("UNCHECKED_CAST")
            val actualResultClass = genericSuperclass.actualTypeArguments[0] as Class<RESULT>
            @Suppress("UNCHECKED_CAST")
            actualResultClass as Class<List<RESULT>>
        }

        override val useCaseClass: Class<USE_CASE> by lazy {
            val genericSuperclass = this.javaClass.genericSuperclass as ParameterizedType
            @Suppress("UNCHECKED_CAST")
            genericSuperclass.actualTypeArguments[1] as Class<USE_CASE>
        }
    }

    abstract class NoResultImpl<USE_CASE : CommandUseCase> : CommandUseCaseHandler<Unit, USE_CASE> {
        abstract fun handleWithoutResult(useCase: USE_CASE)
        override val resultClass = Unit::class.java
        override val useCaseClass: Class<USE_CASE> by lazy {
            val genericSuperclass = this.javaClass.genericSuperclass as ParameterizedType
            @Suppress("UNCHECKED_CAST")
            genericSuperclass.actualTypeArguments[0] as Class<USE_CASE>
        }

        final override fun handle(useCase: USE_CASE) {
            handleWithoutResult(useCase)
        }
    }
}

