package io.hamal.lib.common.value.serde

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant

internal class ValueAdapterJsonTest {

    @Nested
    inner class ArrayTest {

        @Test
        fun write() {
            val result = serde.write(
                ValueArray.builder()
                    .append("some-string-value")
                    .append(true)
                    .append(42)
                    .append(ValueDecimal(123))
                    .build()
            )
            assertThat(result, equalTo("""["some-string-value",true,42.0,"123"]"""))
        }

        @Test
        fun read() {
            val expected = ValueArray.builder()
                .append("some-string-value")
                .append(true)
                .append(42)
                .build()

            val result = serde.read<ValueArray>("""["some-string-value",true,42.0]""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json()
            .register(SerdeModuleValueJson)


    }

    @Nested
    inner class BooleanTest {
        @Test
        fun write() {
            val given = ValueTrue
            val result = serde.write(given)
            assertThat(result, equalTo("true"))
        }

        @Test
        fun read() {
            val expected = ValueFalse
            val result = serde.read<ValueBoolean>("false")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json()
            .register(SerdeModuleValueJson)

    }

    @Nested
    inner class BooleanVariableTest {

        @Test
        fun write() {
            val given = TestObject(ValueBoolean(true))
            val result = serde.write(given)
            assertThat(result, equalTo("true"))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueBoolean(false))
            val result = serde.read<TestObject>("false")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json()
            .register(SerdeModuleValueJson)
            .register(TestObject::class, ValueVariableAdapters.Boolean(::TestObject))


        inner class TestObject(override val value: ValueBoolean) : ValueVariableBoolean()
    }

    @Nested
    inner class DecimalTest {
        @Test
        fun write() {
            val given = ValueDecimal(42)
            val result = serde.write(given)
            assertThat(result, equalTo("\"42\""))
        }

        @Test
        fun read() {
            val expected = ValueDecimal(1337)
            val result = serde.read<ValueDecimal>("\"1337\"")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json().register(SerdeModuleValueJson)
    }

    @Nested
    inner class EnumTest {
        @Test
        fun write() {
            val given = ValueEnum(TestEnum.On)
            val result = serde.write(given)
            assertThat(result, equalTo("\"On\""))
        }

        @Test
        fun read() {
            val expected = ValueEnum("Off")
            val result = serde.read<ValueEnum>("\"Off\"")
            assertThat(result, equalTo(expected))

            assertThat(result.enumValue<TestEnum>(), equalTo(TestEnum.Off))
        }

        private val serde = Serde.json().register(SerdeModuleValueJson)
    }

    @Nested
    inner class EnumVariableTest {

        @Test
        fun write() {
            val given = TestObject(ValueEnum(TestEnum.On))
            val result = serde.write(given)
            assertThat(result, equalTo("\"On\""))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueEnum("Off"))
            val result = serde.read<TestObject>("\"Off\"")
            assertThat(result, equalTo(expected))

            assertThat(result.enumValue, equalTo(TestEnum.Off))
        }

        private val serde = Serde.json()
            .register(SerdeModuleValueJson)
            .register(TestObject::class, ValueVariableAdapters.Enum(::TestObject))


        private inner class TestObject(override val value: ValueEnum) : ValueVariableEnum<TestEnum>(TestEnum::class)
    }

    @Nested
    inner class InstantTest {

        @Test
        fun write() {
            val given = ValueInstant(Instant.ofEpochSecond(1641454487))
            val result = serde.write(given)
            assertThat(result, equalTo("\"2022-01-06T07:34:47Z\""))
        }

        @Test
        fun read() {
            val expected = ValueInstant(Instant.ofEpochSecond(1641454487))
            val result = serde.read<ValueInstant>("\"2022-01-06T07:34:47Z\"")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json().register(SerdeModuleValueJson)
    }

    @Nested
    inner class InstantVariableTest {

        @Test
        fun write() {
            val given = TestObject(ValueInstant(Instant.ofEpochSecond(1641454487)))
            val result = serde.write(given)
            assertThat(result, equalTo("\"2022-01-06T07:34:47Z\""))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueInstant(Instant.ofEpochSecond(1641454487)))
            val result = serde.read<TestObject>("\"2022-01-06T07:34:47Z\"")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json()
            .register(SerdeModuleValueJson)
            .register(TestObject::class, ValueVariableAdapters.Instant(::TestObject))


        inner class TestObject(override val value: ValueInstant) : ValueVariableInstant()
    }

    @Nested
    inner class NilTest {
        @Test
        fun write() {
            val given = ValueNil
            val result = serde.write(given)
            assertThat(result, equalTo("null"))
        }

        private val serde = Serde.json().register(SerdeModuleValueJson)
    }

    @Nested
    inner class NumberTest {
        @Test
        fun write() {
            val given = ValueNumber(42)
            val result = serde.write(given)
            assertThat(result, equalTo("42.0"))
        }

        @Test
        fun read() {
            val expected = ValueNumber(42)
            val result = serde.read<ValueNumber>("42.0")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json().register(SerdeModuleValueJson)
    }

    @Nested
    inner class NumberVariableTest {

        @Test
        fun write() {
            val result = serde.write(TestObject(ValueNumber(1337)))
            assertThat(result, equalTo("1337.0"))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueNumber(1337))
            val result = serde.read<TestObject>("1337")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json()
            .register(SerdeModuleValueJson)
            .register(TestObject::class, ValueVariableAdapters.Number(::TestObject))


        inner class TestObject(override val value: ValueNumber) : ValueVariableNumber()
    }

    @Nested
    inner class ObjectTest {

        @Test
        fun write() {
            val result = serde.write(
                ValueObject.builder()
                    .set("some-string", "some-string-value")
                    .set("some-boolean", true)
                    .set("some-number", 42)
                    .set("some-decimal", ValueDecimal(123))
                    .build()
            )
            assertThat(result, equalTo("""{"some-string":"some-string-value","some-boolean":true,"some-number":42.0,"some-decimal":"123"}"""))
        }

        @Test
        fun read() {
            val expected = ValueObject.builder()
                .set("some-string", "some-string-value")
                .set("some-boolean", true)
                .set("some-number", 42)
                .build()

            val result = serde.read<ValueObject>("""{"some-string":"some-string-value","some-boolean":true,"some-number":42.0}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json().register(SerdeModuleValueJson)
    }

    @Nested
    inner class ObjectVariableTest {

        @Test
        fun write() {
            val result = serde.write(
                TestObject(
                    ValueObject.builder()
                        .set("some-string", "some-string-value")
                        .set("some-boolean", true)
                        .set("some-number", 42)
                        .build()
                )
            )
            assertThat(result, equalTo("""{"some-string":"some-string-value","some-boolean":true,"some-number":42.0}"""))
        }

        @Test
        fun read() {
            val expected = TestObject(
                ValueObject.builder()
                    .set("some-string", "some-string-value")
                    .set("some-boolean", true)
                    .set("some-number", 42)
                    .build()
            )
            val result = serde.read<TestObject>("""{"some-string":"some-string-value","some-boolean":true,"some-number":42.0}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json()
            .register(SerdeModuleValueJson)
            .register(TestObject::class, ValueVariableAdapters.Object(::TestObject))


        inner class TestObject(override val value: ValueObject) : ValueVariableObject()
    }

    @Nested
    inner class SnowflakeIdTest {
        @Test
        fun write() {
            val given = ValueSnowflakeId(SnowflakeId(42))
            val result = serde.write(given)
            assertThat(result, equalTo("\"2a\""))
        }

        @Test
        fun read() {
            val expected = ValueSnowflakeId(SnowflakeId(42))
            val result = serde.read<ValueSnowflakeId>("\"2a\"")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json().register(SerdeModuleValueJson)
    }

    @Nested
    inner class SnowflakeIdVariableTest {

        @Test
        fun write() {
            val result = serde.write(TestObject(ValueSnowflakeId(SnowflakeId(42))))
            assertThat(result, equalTo("\"2a\""))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueSnowflakeId(SnowflakeId(42)))
            val result = serde.read<TestObject>("\"2a\"")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json()
            .register(SerdeModuleValueJson)
            .register(TestObject::class, ValueVariableAdapters.SnowflakeId(::TestObject))


        inner class TestObject(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId()
    }

    @Nested
    inner class StringTest {
        @Test
        fun write() {
            val given = ValueString("Hamal Rocks")
            val result = serde.write(given)
            assertThat(result, equalTo("\"Hamal Rocks\""))
        }

        @Test
        fun read() {
            val expected = ValueString("Hamal Rocks")
            val result = serde.read<ValueString>("\"Hamal Rocks\"")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json().register(SerdeModuleValueJson)
    }

    @Nested
    inner class StringVariableTest {

        @Test
        fun write() {
            val given = TestObject(ValueString("Hamal Rocks"))
            val result = serde.write(given)
            assertThat(result, equalTo("\"Hamal Rocks\""))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueString("Hamal Rocks"))
            val result = serde.read<TestObject>("\"Hamal Rocks\"")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.json()
            .register(SerdeModuleValueJson)
            .register(TestObject::class, ValueVariableAdapters.String(::TestObject))


        inner class TestObject(override val value: ValueString) : ValueVariableString()
    }


    private enum class TestEnum {
        On, Off
    }
}