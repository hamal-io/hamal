package io.hamal.lib.kua

import io.hamal.lib.kua.type.NumberType

abstract class KuaError(message: String, throwable: Throwable? = null) : Error(message, throwable)

class ExitError(val status: NumberType) : KuaError(status.toString())

class AssertionError(message: String) : KuaError(message)

class ScriptError(message: String) : KuaError(message)

class DecimalError(message: String) : KuaError(message)

class ExtensionError(cause: Throwable) : KuaError(cause.message ?: "Unknown error", cause)

