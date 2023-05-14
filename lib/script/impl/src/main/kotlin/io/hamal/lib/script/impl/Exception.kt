package io.hamal.lib.script.impl

import io.hamal.lib.script.api.value.ErrorValue

sealed class ScriptException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}

class ScriptParseException(message: String) : ScriptException(message)

class ScriptEvaluationException(val error: ErrorValue) : ScriptException(message = error.toString())

class NativeFunctionInvocationException(message: String? = null, cause: Throwable? = null) :
    ScriptException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}