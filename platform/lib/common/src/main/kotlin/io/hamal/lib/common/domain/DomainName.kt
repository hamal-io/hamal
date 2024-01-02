package io.hamal.lib.common.domain

abstract class DomainName : ValueObject.ComparableImpl<String>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}

internal object DomainNameValidator {
    private val regex = Regex("^([A-Za-z0-9-_@:.]{1,255})$")
    fun validate(value: String) {
        require(regex.matches(value)) { IllegalArgumentException("DomainName('$value') is illegal") }
    }
}

