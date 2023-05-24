import kotlin.reflect.KClass

interface BackendLogger {
    fun trace(msg: String)
    fun debug(msg: String)
    fun info(msg: String)
    fun warn(msg: String)
    fun error(msg: String)
    fun error(msg: String, throwable: Throwable)
}

fun <T : Any> logger(forClass: KClass<T>): NopLogger {
    return logger(forClass.java.name)
}

fun logger(name: String): NopLogger {
    return NopLogger// FIXME
}

object NopLogger : BackendLogger {
    override fun trace(msg: String) {}

    override fun debug(msg: String) {}

    override fun info(msg: String) {}

    override fun warn(msg: String) {}

    override fun error(msg: String) {}

    override fun error(msg: String, throwable: Throwable) {}
}