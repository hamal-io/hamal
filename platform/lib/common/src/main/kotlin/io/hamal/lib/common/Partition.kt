package io.hamal.lib.common

data class Partition(val value: Int) {
    init {
        require(value >= 0) { "Partition must not be negative - [0, 63]" }
        require(value <= 63) { "Partition is limited to 6 bits - [0, 63]" }
    }

    override fun toString(): String {
        return "Partition($value)"
    }
}