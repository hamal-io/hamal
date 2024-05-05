package io.hamal.lib.kua

import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueObject

abstract class KuaError(message: String, throwable: Throwable? = null) : Error(message, throwable)

class ExitComplete(
    val statusCode: ValueNumber,
    val result: ValueObject
) : KuaError(statusCode.toString())

class ExitFailure(
    val statusCode: ValueNumber,
    val result: ValueObject
) : KuaError(statusCode.toString())

class AssertionError(message: String) : KuaError(message)

class ScriptError(message: String) : KuaError(message)

class DecimalError(message: String) : KuaError(message)

class ExtensionError(cause: Throwable) : KuaError(cause.message ?: "Unknown error", cause)

