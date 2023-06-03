import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

interface Logger {
    fun trace(msg: String)
    fun debug(msg: String)
    fun info(msg: String)
    fun warn(msg: String)
    fun error(msg: String)
    fun error(msg: String, throwable: Throwable)
}

fun <T : Any> logger(forClass: KClass<T>): Logger {
    return logger(forClass.java.name)
}

fun logger(name: String): Logger {
    return DefaultLogger(LoggerFactory.getLogger(name))
}

class DefaultLogger(
    private val delegate: org.slf4j.Logger
) : Logger {
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

object NopLogger : Logger {
    override fun trace(msg: String) {}

    override fun debug(msg: String) {}

    override fun info(msg: String) {}

    override fun warn(msg: String) {}

    override fun error(msg: String) {}

    override fun error(msg: String, throwable: Throwable) {}
}