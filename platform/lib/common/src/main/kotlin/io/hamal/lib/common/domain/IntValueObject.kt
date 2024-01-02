package io.hamal.lib.common.domain

abstract class IntValueObject : ValueObject.ComparableImpl<Int>() {
    override fun toString(): String {
        return "${this::class.simpleName}(${value})"
    }
}
