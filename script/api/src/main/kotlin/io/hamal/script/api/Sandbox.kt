package io.hamal.script.api

import io.hamal.script.api.value.Value

interface Sandbox {
    fun eval(code: String): Value
}