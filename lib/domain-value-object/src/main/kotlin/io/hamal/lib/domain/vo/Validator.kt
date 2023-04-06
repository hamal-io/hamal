package io.hamal.lib.domain.vo

import io.hamal.lib.meta.exception.IllegalArgumentException
import io.hamal.lib.meta.exception.throwIf


internal object IdValidator {
    private val regex = Regex("^([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})$")
    fun validate(value: String) {
        throwIf(!regex.matches(value)) { IllegalArgumentException("Id('$value') is illegal") }
    }
}

internal object ReferenceValidator {
    private val regex = Regex("^([A-Za-z0-9-_@:.]{1,255})$")
    fun validate(value: String) {
        throwIf(!regex.matches(value)) { IllegalArgumentException("Reference('$value') is illegal") }
    }
}

internal object RegionValidator {
    private val regex = Regex("^([A-Za-z0-9-_]{1,255})$")
    fun validate(value: String) {
        throwIf(!regex.matches(value)) { IllegalArgumentException("Region('$value') is illegal") }
    }
}

internal object VersionValidator {
    fun validate(value: Int) {
        throwIf(value <= 0) { IllegalArgumentException("Version('$value') is illegal") }
    }
}