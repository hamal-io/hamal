package io.hamal.backend.infra.adapter

import io.hamal.backend.core.port.GetLoggerPort
import io.hamal.backend.core.port.LogPort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class BackendLoggingAdapter(
    private val logger: Logger
) : LogPort {
    constructor(hostClass: KClass<*>) : this(LoggerFactory.getLogger(hostClass.java))

    override fun trace(msg: String) {
        logger.trace(msg)
    }

    override fun debug(msg: String) {
        logger.debug(msg)
    }

    override fun info(msg: String) {
        logger.info(msg)
    }


    override fun warn(msg: String) {
        logger.warn(msg)
    }

    override fun error(msg: String) {
        logger.warn(msg)
    }

    override fun error(msg: String, throwable: Throwable) {
        logger.error(msg, throwable)
    }

}

object BackendLoggingFactory : GetLoggerPort {
    override fun invoke(hostClass: KClass<*>): LogPort = BackendLoggingAdapter(hostClass)

}