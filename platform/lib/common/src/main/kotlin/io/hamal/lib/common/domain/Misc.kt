package io.hamal.lib.common.domain

class BatchSize(override val value: Int) : ValueObjectInt() {

    init {
        require(value > 0) { "BatchSize must be positive" }
    }

    companion object {
        val all = BatchSize(Int.MAX_VALUE)
    }
}