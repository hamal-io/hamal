package io.hamal.script.impl

import io.hamal.lib.meta.exception.HamalException
import io.hamal.script.api.value.ErrorValue

sealed class ScriptException(message: String? = null, cause: Throwable? = null) : HamalException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}

class ScriptParseException(message: String) : io.hamal.script.impl.ScriptException(message)

class ScriptEvaluationException(val error: ErrorValue) : io.hamal.script.impl.ScriptException(message = error.toString())

class ForeignFunctionInvocationException(message: String? = null, cause: Throwable? = null) :
    io.hamal.script.impl.ScriptException(message, cause) {
    constructor(cause: Throwable) : this(null, cause)
}