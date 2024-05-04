package io.hamal.lib.common.value.serde

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant

internal class ValueAdapterHonTest {

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
            assertThat(
                result,
                equalTo("""{"type":"Array","value":[{"type":"String","value":"some-string-value"},{"type":"Boolean","value":"true"},{"type":"Number","value":"42.0"},{"type":"Decimal","value":"123"}]}""")
            )
        }

        @Test
        fun read() {
            val expected = ValueArray.builder()
                .append("some-string-value")
                .append(true)
                .append(42)
                .append(ValueDecimal(123))
                .build()

            val result =
                serde.read<ValueArray>("""{"type":"Array","value":[{"type":"String","value":"some-string-value"},{"type":"Boolean","value":"true"},{"type":"Number","value":"42.0"},{"type":"Decimal","value":"123"}]}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon()
            .register(SerdeModuleValueHon)


    }

    @Nested
    inner class BooleanTest {
        @Test
        fun write() {
            val given = ValueTrue
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"Boolean","value":"true"}"""))
        }

        @Test
        fun read() {
            val expected = ValueFalse
            val result = serde.read<ValueBoolean>("""{"type":"Boolean","value":"false"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon().register(SerdeModuleValueHon)
    }

    @Nested
    inner class BooleanVariableTest {

        @Test
        fun write() {
            val given = TestObject(ValueBoolean(true))
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"Boolean","value":"true"}"""))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueBoolean(false))
            val result = serde.read<TestObject>("""{"type":"Boolean","value":"false"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon()
            .register(SerdeModuleValueHon)
            .register(TestObject::class, ValueVariableAdapters.Boolean(::TestObject))


        inner class TestObject(override val value: ValueBoolean) : ValueVariableBoolean()
    }

    @Nested
    inner class CodeTest {
        @Test
        fun write() {
            val given = ValueCode("print('Hamal Rocks')")
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"Code","value":"print(\u0027Hamal Rocks\u0027)"}"""))
        }

        @Test
        fun read() {
            val expected = ValueCode("print('Hamal Rocks')")
            val result = serde.read<ValueCode>("""{"type":"Code","value":"print('Hamal Rocks')"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon().register(SerdeModuleValueHon)
    }

    @Nested
    inner class DecimalTest {
        @Test
        fun write() {
            val given = ValueDecimal(42)
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"Decimal","value":"42"}"""))
        }

        @Test
        fun read() {
            val expected = ValueDecimal(1337.7331)
            val result = serde.read<ValueDecimal>("""{"type":"Decimal","value":"1337.7331"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon().register(SerdeModuleValueHon)
    }

    @Nested
    inner class EnumTest {
        @Test
        fun write() {
            val given = ValueEnum(TestEnum.On)
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"Enum","value":"On"}"""))
        }

        @Test
        fun read() {
            val expected = ValueEnum("Off")
            val result = serde.read<ValueEnum>("""{"type":"Enum","value":"Off"}""")
            assertThat(result, equalTo(expected))

            assertThat(result.enumValue<TestEnum>(), equalTo(TestEnum.Off))
        }

        private val serde = Serde.hon().register(SerdeModuleValueHon)
    }

    @Nested
    inner class EnumVariableTest {

        @Test
        fun write() {
            val given = TestObject(ValueEnum(TestEnum.On))
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"Enum","value":"On"}"""))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueEnum("Off"))
            val result = serde.read<TestObject>("""{"type":"Enum","value":"Off"}""")
            assertThat(result, equalTo(expected))

            assertThat(result.enumValue, equalTo(TestEnum.Off))
        }

        private val serde = Serde.hon()
            .register(SerdeModuleValueHon)
            .register(TestObject::class, ValueVariableAdapters.Enum(::TestObject))


        private inner class TestObject(override val value: ValueEnum) : ValueVariableEnum<TestEnum>(TestEnum::class)
    }


    @Nested
    inner class InstantTest {

        @Test
        fun write() {
            val given = ValueInstant(Instant.ofEpochSecond(1641454487))
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"Instant","value":"2022-01-06T07:34:47Z"}"""))
        }

        @Test
        fun read() {
            val expected = ValueInstant(Instant.ofEpochSecond(1641454487))
            val result = serde.read<ValueInstant>("""{"type":"Instant","value":"2022-01-06T07:34:47Z"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon().register(SerdeModuleValueHon)
    }

    @Nested
    inner class InstantVariableTest {

        @Test
        fun write() {
            val given = TestObject(ValueInstant(Instant.ofEpochSecond(1641454487)))
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"Instant","value":"2022-01-06T07:34:47Z"}"""))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueInstant(Instant.ofEpochSecond(1641454487)))
            val result = serde.read<TestObject>("""{"type":"Instant","value":"2022-01-06T07:34:47Z"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon()
            .register(SerdeModuleValueHon)
            .register(TestObject::class, ValueVariableAdapters.Instant(::TestObject))

        inner class TestObject(override val value: ValueInstant) : ValueVariableInstant()
    }

    @Nested
    inner class NilTest {
        @Test
        fun write() {
            val given = ValueNil
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"Nil"}"""))
        }

        @Test
        fun read() {
            val expected = ValueNil
            val result = serde.read<ValueNil>("""{"type":"Nil"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon().register(SerdeModuleValueHon)
    }

    @Nested
    inner class NumberTest {
        @Test
        fun write() {
            val given = ValueNumber(42)
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"Number","value":"42.0"}"""))
        }

        @Test
        fun read() {
            val expected = ValueNumber(42)
            val result = serde.read<ValueNumber>("""{"type":"Number","value":"42"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon().register(SerdeModuleValueHon)

    }

    @Nested
    inner class NumberVariableTest {

        @Test
        fun write() {
            val result = serde.write(TestObject(ValueNumber(1337)))
            assertThat(result, equalTo("""{"type":"Number","value":"1337.0"}"""))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueNumber(1337))
            val result = serde.read<TestObject>("""{"type":"Number","value":"1337"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon()
            .register(SerdeModuleValueHon)
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
            assertThat(
                result,
                equalTo("""{"type":"Object","value":{"some-string":{"type":"String","value":"some-string-value"},"some-boolean":{"type":"Boolean","value":"true"},"some-number":{"type":"Number","value":"42.0"},"some-decimal":{"type":"Decimal","value":"123"}}}""")
            )
        }

        @Test
        fun read() {
            val expected = ValueObject.builder()
                .set("some-string", "some-string-value")
                .set("some-boolean", true)
                .set("some-number", 42)
                .set("some-decimal", ValueDecimal(123))
                .build()

            val result = serde.read<ValueObject>(
                """{"type":"Object","value":{"some-string":{"type":"String","value":"some-string-value"},"some-boolean":{"type":"Boolean","value":"true"},"some-number":{"type":"Number","value":"42.0"},"some-decimal":{"type":"Decimal","value":"123"}}}"""
            )
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon().register(SerdeModuleValueHon)
    }

    @Nested
    inner class ObjectVariableTest {

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

            assertThat(
                result,
                equalTo("""{"type":"Object","value":{"some-string":{"type":"String","value":"some-string-value"},"some-boolean":{"type":"Boolean","value":"true"},"some-number":{"type":"Number","value":"42.0"},"some-decimal":{"type":"Decimal","value":"123"}}}""")
            )
        }

        @Test
        fun read() {
            val expected = TestObject(
                ValueObject.builder()
                    .set("some-string", "some-string-value")
                    .set("some-boolean", true)
                    .set("some-number", 42)
                    .set("some-decimal", ValueDecimal(123))
                    .build()
            )
            val result = serde.read<TestObject>(
                """{"type":"Object","value":{"some-string":{"type":"String","value":"some-string-value"},"some-boolean":{"type":"Boolean","value":"true"},"some-number":{"type":"Number","value":"42.0"},"some-decimal":{"type":"Decimal","value":"123"}}}"""
            )
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon()
            .register(SerdeModuleValueHon)
            .register(TestObject::class, ValueVariableAdapters.Object(::TestObject))


        inner class TestObject(override val value: ValueObject) : ValueVariableObject()
    }

    @Nested
    inner class SnowflakeIdTest {
        @Test
        fun write() {
            val given = ValueSnowflakeId(SnowflakeId(42))
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"Snowflake_Id","value":"2a"}"""))
        }

        @Test
        fun read() {
            val given = ValueSnowflakeId(SnowflakeId(42))
            val result = serde.read<ValueSnowflakeId>("""{"type":"Snowflake_Id","value":"2a"}""")
            assertThat(result, equalTo(given))
        }

        private val serde = Serde.hon().register(SerdeModuleValueHon)
    }


    @Nested
    inner class SnowflakeIdVariableTest {

        @Test
        fun write() {
            val result = serde.write(TestObject(ValueSnowflakeId(SnowflakeId(42))))
            assertThat(result, equalTo("""{"type":"Snowflake_Id","value":"2a"}"""))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueSnowflakeId(SnowflakeId(42)))
            val result = serde.read<TestObject>("""{"type":"Snowflake_Id","value":"2a"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon()
            .register(SerdeModuleValueHon)
            .register(TestObject::class, ValueVariableAdapters.SnowflakeId(::TestObject))


        inner class TestObject(override val value: ValueSnowflakeId) : ValueVariableSnowflakeId()
    }

    @Nested
    inner class StringTest {
        @Test
        fun write() {
            val given = ValueString("Hamal Rocks")
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"String","value":"Hamal Rocks"}"""))
        }

        @Test
        fun read() {
            val expected = ValueString("Hamal Rocks")
            val result = serde.read<ValueString>("""{"type":"String","value":"Hamal Rocks"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon().register(SerdeModuleValueHon)
    }

    @Nested
    inner class StringVariableTest {

        @Test
        fun write() {
            val given = TestObject(ValueString("Hamal Rocks"))
            val result = serde.write(given)
            assertThat(result, equalTo("""{"type":"String","value":"Hamal Rocks"}"""))
        }

        @Test
        fun read() {
            val expected = TestObject(ValueString("Hamal Rocks"))
            val result = serde.read<TestObject>("""{"type":"String","value":"Hamal Rocks"}""")
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon()
            .register(SerdeModuleValueHon)
            .register(TestObject::class, ValueVariableAdapters.String(::TestObject))


        inner class TestObject(override val value: ValueString) : ValueVariableString()
    }

    private enum class TestEnum {
        On, Off
    }
}