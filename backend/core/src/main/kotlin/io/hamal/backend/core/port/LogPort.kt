package io.hamal.backend.core.port

import kotlin.reflect.KClass

interface LogPort{
    fun trace(msg: String)
    fun debug(msg: String)
    fun info(msg: String)
    fun warn(msg: String)
    fun error(msg: String)
    fun error(msg: String, throwable: Throwable)
}

fun interface GetLoggerPort{
    operator fun invoke(hostClass: KClass<*>) : LogPort
}

object NopLogger : LogPort{
    override fun trace(msg: String) {}

    override fun debug(msg: String) {}

    override fun info(msg: String) {}

    override fun warn(msg: String) {}

    override fun error(msg: String) {}

    override fun error(msg: String, throwable: Throwable) {}
}