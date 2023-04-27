package io.hamal.backend.infra.adapter

import io.hamal.backend.core.port.notification.FlushDomainNotificationPort
import io.hamal.lib.ddd.usecase.*
import kotlin.reflect.KClass

class BackendUseCaseInvokerAdapter(
    private val getCommandUseCasePort: GetCommandUseCasePort,
    private val getQueryUseCasePort: GetQueryUseCasePort,
    private val getFetchOneUseCasePort: GetFetchOneUseCasePort,
    private val flushDomainNotificationPort: FlushDomainNotificationPort
) : InvokeUseCasePort {

    constructor(useCaseRegistry: GetUseCasePort, flushDomainNotificationPort: FlushDomainNotificationPort)
            : this(useCaseRegistry, useCaseRegistry, useCaseRegistry, flushDomainNotificationPort)

    override fun <RESULT : Any, USE_CASE : CommandUseCase> command(
        resultClass: KClass<RESULT>,
        vararg useCases: USE_CASE
    ): List<RESULT> {
        val result = useCases.flatMap { useCase -> getCommandUseCasePort[resultClass, useCase::class](useCase) }
        flushDomainNotificationPort.flush()
        return result;
    }

    override fun <USE_CASE : CommandUseCase> command(
        vararg useCases: USE_CASE
    ) {
        useCases.forEach { useCase -> getCommandUseCasePort[Unit::class, useCase::class](useCase) }
        flushDomainNotificationPort.flush()
    }


    override fun <RESULT : Any, USE_CASE : QueryUseCase> query(
        resultClass: KClass<RESULT>,
        useCase: USE_CASE
    ): List<RESULT> = getQueryUseCasePort[resultClass, useCase::class](useCase)

    override fun <RESULT : Any, USE_CASE : FetchOneUseCase> fetchOne(
        resultClass: KClass<RESULT>,
        useCase: USE_CASE
    ): RESULT? = getFetchOneUseCasePort[resultClass, useCase::class](useCase)
}