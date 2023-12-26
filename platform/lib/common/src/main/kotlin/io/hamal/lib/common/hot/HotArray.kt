package io.hamal.lib.common.hot

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class HotArray(
    val nodes: List<HotNode>
) : HotNode {

    override val isArray get(): Boolean = true

    override fun deepCopy(): HotNode {
        TODO("Not yet implemented")
    }

    companion object {
        val empty = HotArray(LinkedList())

        fun builder() = HotArrayBuilder()
    }
}

class HotArrayBuilder {
    val nodes: MutableList<HotNode> = LinkedList()

    fun add(value: String): HotArrayBuilder {
        nodes.add(HotString(value))
        return this
    }

    fun add(value: Byte): HotArrayBuilder {
        nodes.add(HotNumber(value))
        return this
    }

    fun add(value: Short): HotArrayBuilder {
        nodes.add(HotNumber(value))
        return this
    }

    fun add(value: BigInteger): HotArrayBuilder {
        nodes.add(HotNumber(value))
        return this
    }

    fun add(value: BigDecimal): HotArrayBuilder {
        nodes.add(HotNumber(value))
        return this
    }

    fun add(value: Int): HotArrayBuilder {
        nodes.add(HotNumber(value))
        return this
    }

    fun add(value: Float): HotArrayBuilder {
        nodes.add(HotNumber(value))
        return this
    }

    fun add(value: Double): HotArrayBuilder {
        nodes.add(HotNumber(value))
        return this
    }

    fun add(value: Long): HotArrayBuilder {
        nodes.add(HotNumber(value))
        return this
    }

    fun add(value: Boolean): HotArrayBuilder {
        nodes.add(HotBoolean(value))
        return this
    }

    fun add(value: HotNode): HotArrayBuilder {
        nodes.add(value)
        return this
    }

    fun build(): HotArray {
        return HotArray(nodes)
    }
}
