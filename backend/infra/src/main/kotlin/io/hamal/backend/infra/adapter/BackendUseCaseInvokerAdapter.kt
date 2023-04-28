package io.hamal.backend.infra.adapter

import io.hamal.backend.core.port.GetLoggerPort
import io.hamal.backend.core.port.LogPort
import io.hamal.backend.core.port.notification.FlushDomainNotificationPort
import io.hamal.lib.ddd.base.DomainObject
import io.hamal.lib.ddd.usecase.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.reflect.KClass


@Component
class BackendUseCaseInvokerAdapter private constructor(
    private val getCommandUseCasePort: GetCommandUseCasePort,
    private val getQueryUseCasePort: GetQueryUseCasePort,
    private val getFetchOneUseCasePort: GetFetchOneUseCasePort,
    private val flushDomainNotificationPort: FlushDomainNotificationPort,
    private val log: LogPort
) : InvokeUseCasePort {

    @Autowired
    constructor(
        useCaseRegistry: GetUseCasePort,
        flushDomainNotificationPort: FlushDomainNotificationPort,
        getLoggerPort: GetLoggerPort
    ) : this(
        useCaseRegistry,
        useCaseRegistry,
        useCaseRegistry,
        flushDomainNotificationPort,
        getLoggerPort(BackendUseCaseInvokerAdapter::class)
    )

    override fun <RESULT : DomainObject, USE_CASE : CommandUseCase> newCommand(vararg useCases: USE_CASE): RESULT {
        TODO("Not yet implemented")
    }

    override fun <RESULT : Any, USE_CASE : CommandUseCase> command(
        resultClass: KClass<RESULT>,
        vararg useCases: USE_CASE
    ): List<RESULT> {
        val result = useCases
            .onEach(::logUseCaseInvocation)
            .flatMap { useCase -> getCommandUseCasePort[resultClass, useCase::class](useCase) }
        flushDomainNotificationPort.flush()
        return result;
    }

    override fun <USE_CASE : CommandUseCase> command(
        vararg useCases: USE_CASE
    ) {
        useCases
            .onEach(::logUseCaseInvocation)
            .forEach { useCase -> getCommandUseCasePort[Unit::class, useCase::class](useCase) }
        flushDomainNotificationPort.flush()
    }


    override fun <RESULT : Any, USE_CASE : QueryUseCase> query(
        resultClass: KClass<RESULT>,
        useCase: USE_CASE
    ): List<RESULT> {
        logUseCaseInvocation(useCase)
        return getQueryUseCasePort[resultClass, useCase::class](useCase)
    }

    override fun <RESULT : Any, USE_CASE : FetchOneUseCase> fetchOne(
        resultClass: KClass<RESULT>,
        useCase: USE_CASE
    ): RESULT? {
        logUseCaseInvocation(useCase)
        return getFetchOneUseCasePort[resultClass, useCase::class](useCase)
    }

    private fun logUseCaseInvocation(useCase: UseCase) {
        log.trace("Invoking command use case $useCase")
    }
}