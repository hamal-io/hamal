package io.hamal.lib.kua

import io.hamal.lib.common.hot.HotNumber
import io.hamal.lib.common.value.ValueObject

abstract class KuaError(message: String, throwable: Throwable? = null) : Error(message, throwable)

class ExitError(
    val status: HotNumber,
    val result: ValueObject
) : KuaError(status.toString())

class AssertionError(message: String) : KuaError(message)

class ScriptError(message: String) : KuaError(message)

class DecimalError(message: String) : KuaError(message)

class ExtensionError(cause: Throwable) : KuaError(cause.message ?: "Unknown error", cause)

