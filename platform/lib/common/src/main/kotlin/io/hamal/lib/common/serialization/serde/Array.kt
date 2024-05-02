package io.hamal.lib.common.serialization.serde

import io.hamal.lib.common.Decimal
import java.math.BigDecimal
import java.math.BigInteger
import java.util.*


@JvmInline
value class SerdeArray(
    val nodes: List<SerdeNode<*>>
) : SerdeNode<SerdeArray>, Collection<SerdeNode<*>> {

    override val size get() : Int = nodes.size
    override fun isEmpty() = nodes.isEmpty()
    override fun iterator() = nodes.iterator()
    override fun containsAll(elements: Collection<SerdeNode<*>>) = nodes.containsAll(elements)
    override fun contains(element: SerdeNode<*>) = nodes.contains(element)

    override val isArray get(): kotlin.Boolean = true

    fun find(idx: Int): SerdeNode<*>? = if (idx >= 0 && idx < nodes.size) {
        nodes[idx]
    } else {
        null
    }

    operator fun get(idx: Int): SerdeNode<*> =
        find(idx) ?: throw NoSuchElementException("Element at index $idx not found")

    fun isArray(idx: Int): kotlin.Boolean = find(idx)?.isArray ?: false
    override fun asArray(): SerdeArray = this
    fun asArray(idx: Int): SerdeArray = find(idx)
        ?.let { if (it.isArray) it as SerdeArray else null }
        ?: throw IllegalStateException("Not HotArray")

    fun isBoolean(idx: Int): kotlin.Boolean = find(idx)?.isBoolean ?: false
    fun asBoolean(idx: Int): SerdeBoolean = find(idx)
        ?.let { if (it.isBoolean) it as SerdeBoolean else null }
        ?: throw IllegalStateException("Not HotBoolean")

    fun booleanValue(idx: Int): kotlin.Boolean = asBoolean(idx).value

    fun isNumber(idx: Int): kotlin.Boolean = find(idx)?.isNumber ?: false
    fun asNumber(idx: Int): SerdeNumber = find(idx)
        ?.let { if (it.isNumber) it as SerdeNumber else null }
        ?: throw IllegalStateException("Not HotNumber")

    fun decimalValue(idx: Int): Decimal = asNumber(idx).decimalValue
    fun byteValue(idx: Int): Byte = asNumber(idx).byteValue
    fun doubleValue(idx: Int): Double = asNumber(idx).doubleValue
    fun floatValue(idx: Int): Float = asNumber(idx).floatValue
    fun intValue(idx: Int): Int = asNumber(idx).intValue
    fun longValue(idx: Int): Long = asNumber(idx).longValue
    fun shortValue(idx: Int): Short = asNumber(idx).shortValue

    fun isNull(idx: Int): kotlin.Boolean = find(idx)?.isNull ?: true
    fun asNull(idx: Int): SerdeNull = find(idx)
        ?.let { if (it.isNull) it as SerdeNull else throw IllegalStateException("Not HotNull") }
        ?: SerdeNull

    fun isObject(idx: Int): kotlin.Boolean = find(idx)?.isObject ?: false
    fun asObject(idx: Int): SerdeObject = find(idx)
        ?.let { if (it.isObject) it as SerdeObject else null }
        ?: throw IllegalStateException("Not HotObject")

    fun isString(idx: Int): kotlin.Boolean = find(idx)?.isString ?: false
    fun asString(idx: Int): SerdeString = find(idx)
        ?.let { if (it.isString) it as SerdeString else null }
        ?: throw IllegalStateException("Not HotString")

    fun stringValue(idx: Int): kotlin.String = asString(idx).stringValue

    fun isTerminal(idx: Int): kotlin.Boolean = find(idx)?.isPrimitive ?: false
    fun asTerminal(idx: Int): SerdePrimitive<*> = find(idx)
        ?.let { if (it.isPrimitive) it as SerdePrimitive else null }
        ?: throw IllegalStateException("Not HotTerminal")

    override fun deepCopy(): SerdeArray {
        val builder = builder()
        nodes.forEach { node -> builder.append(node.deepCopy()) }
        return builder.build()
    }

    companion object {
        val empty = SerdeArray(LinkedList())

        fun builder() = HotArrayBuilder()
    }

}

class HotArrayBuilder {

    fun append(value: kotlin.String): HotArrayBuilder {
        nodes.add(SerdeString(value))
        return this
    }

    fun append(value: Byte): HotArrayBuilder {
        nodes.add(SerdeNumber(value))
        return this
    }

    fun append(value: Short): HotArrayBuilder {
        nodes.add(SerdeNumber(value))
        return this
    }

    fun append(value: BigInteger): HotArrayBuilder {
        nodes.add(SerdeNumber(value))
        return this
    }

    fun append(value: BigDecimal): HotArrayBuilder {
        nodes.add(SerdeNumber(value))
        return this
    }

    fun append(value: Int): HotArrayBuilder {
        nodes.add(SerdeNumber(value))
        return this
    }

    fun append(value: Float): HotArrayBuilder {
        nodes.add(SerdeNumber(value))
        return this
    }

    fun append(value: Double): HotArrayBuilder {
        nodes.add(SerdeNumber(value))
        return this
    }

    fun append(value: Long): HotArrayBuilder {
        nodes.add(SerdeNumber(value))
        return this
    }

    fun append(value: kotlin.Boolean): HotArrayBuilder {
        nodes.add(SerdeBoolean(value))
        return this
    }

    fun append(value: SerdeNode<*>): HotArrayBuilder {
        nodes.add(value)
        return this
    }

    fun appendNull() = append(SerdeNull)

    fun build(): SerdeArray {
        return SerdeArray(nodes)
    }

    private val nodes: MutableList<SerdeNode<*>> = LinkedList()

}
