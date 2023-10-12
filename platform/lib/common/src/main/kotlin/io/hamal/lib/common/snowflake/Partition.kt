package io.hamal.lib.common.snowflake

import io.hamal.lib.common.Partition

interface PartitionSource {
    fun get(): Partition
}

class PartitionSourceImpl(
    private val partition: Partition
) : PartitionSource {
    constructor(value: Int) : this(Partition(value))

    override fun get() = partition
}