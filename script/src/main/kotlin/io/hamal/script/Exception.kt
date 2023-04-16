package io.hamal.script

import io.hamal.lib.meta.exception.HamalException
import io.hamal.script.value.ErrorValue

sealed class ScriptException(message: String? = null, cause: Throwable? = null) : HamalException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}

class ScriptParseException(message: String) : ScriptException(message)

class ScriptEvaluationException(val error: ErrorValue) : ScriptException(message = error.toString())

class ForeignFunctionInvocationException(message: String? = null, cause: Throwable? = null) :
    ScriptException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}