package io.hamal.lib.script.api

import io.hamal.lib.script.api.value.DepValue

interface Sandbox {
    fun eval(code: String): DepValue
}