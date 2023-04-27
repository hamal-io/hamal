package io.hamal.bootstrap.adapter

import io.hamal.lib.ddd.usecase.*
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

class DefaultUseCaseInvokerAdapter(
    @Autowired internal val getCommandUseCasePort: GetCommandUseCasePort,
    @Autowired internal val getQueryUseCasePort: GetQueryUseCasePort,
    @Autowired internal val getFetchOneUseCasePort: GetFetchOneUseCasePort
) : InvokeUseCasePort {

    constructor(useCaseRegistry: GetUseCasePort)
            : this(useCaseRegistry, useCaseRegistry, useCaseRegistry)

    override fun <RESULT : Any, USE_CASE : CommandUseCase> command(
        resultClass: KClass<RESULT>,
        vararg useCases: USE_CASE
    ): List<RESULT> = useCases.flatMap { useCase -> getCommandUseCasePort[resultClass, useCase::class](useCase) }

    override fun <USE_CASE : CommandUseCase> command(
        vararg useCases: USE_CASE
    ) = useCases.forEach { useCase -> getCommandUseCasePort[Unit::class, useCase::class](useCase) }


    override fun <RESULT : Any, USE_CASE : QueryUseCase> query(
        resultClass: KClass<RESULT>,
        useCase: USE_CASE
    ): List<RESULT> = getQueryUseCasePort[resultClass, useCase::class](useCase)

    override fun <RESULT : Any, USE_CASE : FetchOneUseCase> fetchOne(
        resultClass: KClass<RESULT>,
        useCase: USE_CASE
    ): RESULT? = getFetchOneUseCasePort[resultClass, useCase::class](useCase)
}