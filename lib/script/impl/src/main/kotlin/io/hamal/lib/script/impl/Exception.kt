package io.hamal.lib.script.impl

import io.hamal.lib.script.api.value.ErrorValue

sealed class ScriptException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}

class ScriptParseException(message: String) : io.hamal.lib.script.impl.ScriptException(message)

class ScriptEvaluationException(val error: ErrorValue) : io.hamal.lib.script.impl.ScriptException(message = error.toString())

class ForeignFunctionInvocationException(message: String? = null, cause: Throwable? = null) :
    io.hamal.lib.script.impl.ScriptException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}