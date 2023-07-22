package io.hamal.lib.kua

import io.hamal.lib.kua.value.NumberValue

data class ExitException(val status: NumberValue) : Exception(status.toString())