package io.hamal.lib.common

data class Partition(val value: Int) {
    init {
        require(value >= 0) { "Partition must not be negative - [0, 1023]" }
        require(value <= 1023) { "Partition is limited to 10 bits - [0, 1023]" }
    }

    override fun toString(): String {
        return "Partition($value)"
    }
}