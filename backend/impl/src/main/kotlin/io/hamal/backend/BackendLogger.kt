package io.hamal.backend

import BackendLogger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun <T : Any> logger(forClass: KClass<T>): BackendLogger {
    return logger(forClass.java.name)
}

fun logger(name: String): BackendLogger {
    return DefaultBackendLogger(
        LoggerFactory.getLogger(name)
    )
}


class DefaultBackendLogger(
    private val delegate: org.slf4j.Logger
) : BackendLogger {
    override fun trace(msg: String) {
        delegate.trace(msg)
    }

    override fun debug(msg: String) {
        delegate.debug(msg)
    }

    override fun info(msg: String) {
        delegate.info(msg)
    }


    override fun warn(msg: String) {
        delegate.warn(msg)
    }

    override fun error(msg: String) {
        delegate.warn(msg)
    }

    override fun error(msg: String, throwable: Throwable) {
        delegate.error(msg, throwable)
    }
}