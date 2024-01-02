package io.hamal.lib.common.domain

abstract class ValueObjectString : ValueObject.ComparableImpl<String>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}
