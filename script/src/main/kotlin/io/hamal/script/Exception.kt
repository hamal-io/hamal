package io.hamal.script

import io.hamal.lib.meta.exception.HamalException
import io.hamal.script.value.ErrorValue

class ScriptParseException(message: String) : HamalException(message)

class ScriptEvaluationException(val error: ErrorValue) : HamalException(message = error.toString())

