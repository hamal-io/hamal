package io.hamal.lib.script.api

import io.hamal.lib.script.api.value.Value


interface Sandbox {
    fun eval(code: String): Value
}