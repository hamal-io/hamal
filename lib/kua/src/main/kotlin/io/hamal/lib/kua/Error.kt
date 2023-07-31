package io.hamal.lib.kua

import io.hamal.lib.kua.value.NumberValue

data class ExitError(val status: NumberValue) : Error(status.toString())

class AssertionError(message: String) : Error(message)

class ScriptError(message: String) : Error(message)

class ExtensionError(message: String, cause: Throwable) : Error(message, cause)

