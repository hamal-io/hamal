package io.hamal.lib.kua

import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueObject

sealed class KuaError(message: String, throwable: Throwable? = null) : Error(message, throwable)

class ExitComplete(
    val statusCode: ValueNumber,
    val result: ValueObject
) : KuaError(statusCode.toString())

class ExitFailure(
    val statusCode: ValueNumber,
    val result: ValueObject
) : KuaError(statusCode.toString())

class ErrorAssertion(message: String) : KuaError(message)

class ErrorDecimal(message: String) : KuaError(message)

class ErrorPlugin(cause: Throwable) : KuaError(cause.message ?: "Unknown error", cause)

class ErrorInternal(message: String) : KuaError(message)

class ErrorNotFound(message: String) : KuaError(message)

class ErrorIllegalArgument(message: String) : KuaError(message)

class ErrorIllegalState(message: String) : KuaError(message)