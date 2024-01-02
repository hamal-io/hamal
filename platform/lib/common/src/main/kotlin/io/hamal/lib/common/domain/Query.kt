package io.hamal.lib.common.domain

data class Limit(
    override val value: Int
) : ValueObject.ComparableImpl<Int>() {

    init {
        require(value > 0) { "Limit must be positive" }
    }
    
    companion object {
        val all = Limit(Int.MAX_VALUE)
    }
}