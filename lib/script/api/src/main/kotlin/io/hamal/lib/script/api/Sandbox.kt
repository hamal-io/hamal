package io.hamal.lib.script.api

import io.hamal.lib.kua.value.Value


interface Sandbox {
    fun eval(code: String): Value
}