package io.hamal.lib.kua.type

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId
import kotlin.reflect.KClass


data class ArrayType(
    val value: MutableMap<Int, SerializableType> = mutableMapOf(),
) : TableType(), Map<Int, SerializableType> {

    override val size get() = value.size

    @Transient
    override val entries: Set<Map.Entry<Int, SerializableType>> = value.entries

    @Transient
    override val keys: Set<Int> = value.keys

    @Transient
    override val values: Collection<SerializableType> = value.values
    override fun isEmpty(): Boolean = value.isEmpty()
    override fun containsValue(value: SerializableType): Boolean = this.value.containsValue(value)
    override fun containsKey(key: Int): Boolean = this.containsKey(key)

    fun append(value: ArrayType): Int {
        this.value[this.value.size + 1] = value
        return size
    }

    fun append(value: SerializableType): Int {
        this.value[this.value.size + 1] = value
        return size
    }

    fun append(value: AnySerializableType) = append(value.value)

    override operator fun get(key: Int): SerializableType {
        return value[key]!!
    }

    fun getBoolean(idx: Int) = getBooleanValue(idx) == True
    fun getBooleanValue(idx: Int): BooleanType {
        checkExpectedType(idx, BooleanType::class)
        return value[idx]!! as BooleanType
    }

    fun append(value: Boolean) = append(booleanOf(value))
    fun append(value: BooleanType): Int {
        this.value[this.value.size + 1] = value
        return size
    }


    fun getInt(idx: Int) = getNumberType(idx).value.toInt()
    fun getLong(idx: Int) = getNumberType(idx).value.toLong()
    fun getFloat(idx: Int) = getNumberType(idx).value.toFloat()
    fun getDouble(idx: Int) = getNumberType(idx).value.toDouble()
    fun getNumberType(idx: Int): NumberType {
        checkExpectedType(idx, NumberType::class)
        return value[idx]!! as NumberType
    }

    fun getDecimalType(idx: Int): DecimalType {
        checkExpectedType(idx, DecimalType::class)
        return value[idx]!! as DecimalType
    }


    fun append(value: Int) = append(value.toDouble())
    fun append(value: Long) = append(value.toDouble())
    fun append(value: Float) = append(value.toDouble())
    fun append(value: Double) = append(NumberType(value))
    fun append(value: NumberType): Int {
        this.value[this.value.size + 1] = value
        return size
    }

    fun append(value: DecimalType): Int {
        this.value[this.value.size + 1] = value
        return size
    }


    fun append(value: SnowflakeId) = append(value.value.toString(16))
    fun append(value: ValueObjectId) = append(value.value.value)

    fun append(value: MapType): Int {
        this.value[this.value.size + 1] = value
        return size
    }

    fun getMap(idx: Int): MapType {
        checkExpectedType(idx, MapType::class)
        return value[idx]!! as MapType
    }

    fun getArray(idx: Int): ArrayType {
        checkExpectedType(idx, ArrayType::class)
        return value[idx]!! as ArrayType
    }

    fun getString(idx: Int) = getStringType(idx).value
    fun getStringType(idx: Int): StringType {
        checkExpectedType(idx, StringType::class)
        return value[idx]!! as StringType
    }

    fun append(value: String) = append(StringType(value))
    fun append(value: StringType): Int {
        this.value[this.value.size + 1] = value
        return size
    }

    fun getCodeType(idx: Int) = CodeType(getString(idx))

    fun type(idx: Int): KClass<out Type> {
        return value[idx]?.let { it::class } ?: NilType::class
    }
}

internal fun ArrayType.checkExpectedType(idx: Int, expected: KClass<out Type>) {
    check(type(idx) == expected) {
        "Expected type to be $expected but was $this"
    }
}