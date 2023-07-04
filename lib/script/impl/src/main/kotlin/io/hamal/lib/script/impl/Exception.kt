package io.hamal.lib.script.impl

import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.script.api.value.NumberValue

sealed class ScriptException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}

class ScriptEvaluationException(val error: ErrorValue) : ScriptException(message = error.toString())

class ExitException(val status: NumberValue) : Exception(status.toString())