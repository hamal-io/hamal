package io.hamal.lib.domain.vo.validator

import io.hamal.lib.domain.exception.IllegalArgumentException
import io.hamal.lib.domain.exception.throwIf


internal object IdValidator {
    private val regex = Regex("^([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})$")
    fun validate(value: String) {
        throwIf(!regex.matches(value)) { IllegalArgumentException("Value('$value') is illegal") }
    }
}

