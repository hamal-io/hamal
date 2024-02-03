package io.hamal.lib.common.domain

class Limit(override val value: Int) : ValueObjectInt() {
    init {
        require(value > 0) { "Limit must be positive" }
    }

    companion object {
        val all = Limit(Int.MAX_VALUE)
    }
}


class Count(override val value: Long) : ValueObjectLong() {
    init {
        require(value >= 0) { "Count must not be negative" }
    }
}
