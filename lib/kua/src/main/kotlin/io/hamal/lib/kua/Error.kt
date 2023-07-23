package io.hamal.lib.kua

import io.hamal.lib.kua.value.NumberValue

data class ExitError(val status: NumberValue) : Exception(status.toString())

class KuaError(message: String, cause: Throwable) : Error(message, cause)

