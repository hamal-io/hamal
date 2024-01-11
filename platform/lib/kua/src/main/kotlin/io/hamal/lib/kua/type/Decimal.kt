package io.hamal.lib.kua.type

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonSerializationContext
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.serialization.JsonAdapter
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.kua.type.KuaType.Type.Decimal
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode


data class KuaDecimal(
    val value: BigDecimal,
) : KuaType, Comparable<KuaDecimal> {

    override val type: KuaType.Type = Decimal

    companion object {
        val Zero = KuaDecimal(0)
        val One = KuaDecimal(1)

        operator fun invoke(value: Byte): KuaDecimal = KuaDecimal(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Short): KuaDecimal = KuaDecimal(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Int): KuaDecimal = KuaDecimal(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Long): KuaDecimal = KuaDecimal(BigDecimal.valueOf(value))
        operator fun invoke(value: Number): KuaDecimal = KuaDecimal(BigDecimal.valueOf(value.toDouble()))
        operator fun invoke(value: SnowflakeId): KuaDecimal = KuaDecimal(BigDecimal.valueOf(value.value))

        operator fun invoke(value: Float): KuaDecimal {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return KuaDecimal(BigDecimal.valueOf(value.toDouble()))
        }

        operator fun invoke(value: Double): KuaDecimal {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return KuaDecimal(BigDecimal.valueOf(value))
        }

        operator fun invoke(value: String): KuaDecimal {
            require(isNumber(value)) { IllegalArgumentException("NaN") }
            return KuaDecimal(BigDecimal(value.trim(), mathContext))
        }

        fun isNumber(value: String): Boolean {
            val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
            return value.matches(regex)
        }

        private val mathContext = MathContext.DECIMAL128
    }

    fun plus(other: KuaDecimal) = KuaDecimal(value.add(other.value, mathContext))

    fun minus(other: KuaDecimal) =
        KuaDecimal(value.subtract(other.value, mathContext))

    fun multiply(other: KuaDecimal) =
        KuaDecimal(value.multiply(other.value, mathContext))

    fun divide(other: KuaDecimal) =
        KuaDecimal(value.divide(other.value, mathContext))

    fun pow(other: KuaDecimal) =
        KuaDecimal(value.pow(other.value.toInt(), mathContext))

    fun remainder(other: KuaDecimal) =
        KuaDecimal(value.remainder(other.value, mathContext))

    fun floor() = KuaDecimal(value.setScale(0, RoundingMode.FLOOR))

    fun ceil() = KuaDecimal(value.setScale(0, RoundingMode.CEILING))

    fun ln(): KuaDecimal {
        // Algorithm: http://functions.wolfram.com/ElementaryFunctions/Log/10/
        // https://stackoverjob.com/a/6169691/6444586
        val result: KuaDecimal
        require(isPositive()) { IllegalStateException("Value must >= 1") }
        if (value == BigDecimal.ONE) {
            return KuaDecimal(BigDecimal.ZERO)
        } else {
            val iterations = 25000L
            val x = value.subtract(BigDecimal.ONE)
            var ret = BigDecimal(iterations + 1)
            for (i in iterations downTo 0) {
                var N = BigDecimal(i / 2 + 1).pow(2)
                N = N.multiply(x, mathContext)
                ret = N.divide(ret, mathContext)
                N = BigDecimal(i + 1)
                ret = ret.add(N, mathContext)
            }
            ret = x.divide(ret, mathContext)
            result = KuaDecimal(ret)
        }
        return result
    }

    fun sqrt(): KuaDecimal {
        require(!isNegative()) { throw IllegalStateException("Value must >= 0") }
        return KuaDecimal(value.sqrt(mathContext))
    }

    fun abs() = KuaDecimal(value.abs(mathContext))

    fun negate() = KuaDecimal(value.negate(mathContext))


    override fun toString() = value.toString()

    override operator fun compareTo(other: KuaDecimal) = value.compareTo(other.value)

    fun isLessThan(other: KuaDecimal) = compareTo(other) < 0

    fun isLessThanEqual(other: KuaDecimal) = compareTo(other) <= 0

    fun isGreaterThan(other: KuaDecimal) = compareTo(other) > 0

    fun isGreaterThanEqual(other: KuaDecimal) = compareTo(other) >= 0

    fun isNegative() = value.signum() < 0

    fun isPositive() = value.signum() > 0

    fun isZero() = value.signum() == 0

    fun toByte() = value.toByte()

    fun toShort() = value.toShort()

    fun toInt() = value.toInt()

    fun toLong() = value.toLong()

    fun toFloat() = value.toFloat()

    fun toChar() = value.toChar()

    fun toDouble() = value.toDouble()

    fun toBigDecimal() = value

    object Adapter : JsonAdapter<KuaDecimal> {
        override fun serialize(instance: KuaDecimal, type: Type, ctx: JsonSerializationContext): JsonElement {
            return ctx.serialize(
                HotObject.builder()
                    .set("value", instance.value.toPlainString())
                    .set("type", instance.type.name)
                    .build()
            )
        }

        override fun deserialize(element: JsonElement, type: Type, ctx: JsonDeserializationContext): KuaDecimal {
            return KuaDecimal(element.asJsonObject.get("value").asString)
        }
    }
}