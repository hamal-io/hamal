package io.hamal.lib.common.hot

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*

class HotArray(
    val nodes: List<HotNode>
) : HotNode {
    val size get() : Int = nodes.size

    override val isArray get(): Boolean = true

    fun find(idx: Int): HotNode? = if (idx >= 0 && idx < nodes.size) {
        nodes[idx]
    } else {
        null
    }

    fun get(idx: Int): HotNode = find(idx) ?: throw NoSuchElementException("Element at index $idx not found")

    fun isArray(idx: Int): Boolean = find(idx)?.isArray ?: false
    override fun asArray(): HotArray = this
    fun asArray(idx: Int): HotArray = find(idx)
        ?.let { if (it.isArray) it as HotArray else null }
        ?: throw IllegalStateException("Not HotArray")

    fun isBoolean(idx: Int): Boolean = find(idx)?.isBoolean ?: false
    fun asBoolean(idx: Int): HotBoolean = find(idx)
        ?.let { if (it.isBoolean) it as HotBoolean else null }
        ?: throw IllegalStateException("Not HotBoolean")

    fun booleanValue(idx: Int): Boolean = asBoolean(idx).value

    fun isNumber(idx: Int): Boolean = find(idx)?.isNumber ?: false
    fun asNumber(idx: Int): HotNumber = find(idx)
        ?.let { if (it.isNumber) it as HotNumber else null }
        ?: throw IllegalStateException("Not HotNumber")

    fun bigDecimalValue(idx: Int): BigDecimal = asNumber(idx).bigDecimalValue
    fun bigIntegerValue(idx: Int): BigInteger = asNumber(idx).bigIntegerValue
    fun byteValue(idx: Int): Byte = asNumber(idx).byteValue
    fun doubleValue(idx: Int): Double = asNumber(idx).doubleValue
    fun floatValue(idx: Int): Float = asNumber(idx).floatValue
    fun intValue(idx: Int): Int = asNumber(idx).intValue
    fun longValue(idx: Int): Long = asNumber(idx).longValue
    fun shortValue(idx: Int): Short = asNumber(idx).shortValue

    fun isNull(idx: Int): Boolean = find(idx)?.isNull ?: true
    fun asNull(idx: Int): HotNull = find(idx)
        ?.let { if (it.isNull) it as HotNull else throw IllegalStateException("Not HotNull") }
        ?: HotNull

    fun isObject(idx: Int): Boolean = find(idx)?.isObject ?: false
    fun asObject(idx: Int): HotObject = find(idx)
        ?.let { if (it.isObject) it as HotObject else null }
        ?: throw IllegalStateException("Not HotObject")

    fun isString(idx: Int): Boolean = find(idx)?.isString ?: false
    fun asString(idx: Int): HotString = find(idx)
        ?.let { if (it.isString) it as HotString else null }
        ?: throw IllegalStateException("Not HotString")

    fun stringValue(idx: Int): String = asString(idx).stringValue

    fun isTerminal(idx: Int): Boolean = find(idx)?.isTerminal ?: false
    fun asTerminal(idx: Int): HotTerminal = find(idx)
        ?.let { if (it.isTerminal) it as HotTerminal else null }
        ?: throw IllegalStateException("Not HotTerminal")

    override fun deepCopy(): HotNode {
        val builder = builder()
        nodes.forEach { node: HotNode -> builder.add(node.deepCopy()) }
        return builder.build()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HotArray
        return nodes == other.nodes
    }

    override fun hashCode(): Int {
        return nodes.hashCode()
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

    fun addNull() = add(HotNull)

    fun build(): HotArray {
        return HotArray(nodes)
    }
}
