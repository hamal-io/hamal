package io.hamal.lib.kua

import io.hamal.lib.common.serialization.serde.SerdeNumber
import io.hamal.lib.common.value.ValueObject

abstract class KuaError(message: String, throwable: Throwable? = null) : Error(message, throwable)

class ExitError(
    val status: SerdeNumber,
    val result: ValueObject
) : KuaError(status.toString())

class AssertionError(message: String) : KuaError(message)

class ScriptError(message: String) : KuaError(message)

class DecimalError(message: String) : KuaError(message)

class ExtensionError(cause: Throwable) : KuaError(cause.message ?: "Unknown error", cause)

