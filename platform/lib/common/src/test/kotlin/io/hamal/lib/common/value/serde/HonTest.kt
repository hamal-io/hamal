package io.hamal.lib.common.value.serde

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
import io.hamal.lib.common.value.serde.ValueHonAdapters.BooleanVariable
import io.hamal.lib.common.value.serde.ValueHonAdapters.InstantVariable
import io.hamal.lib.common.value.serde.ValueHonAdapters.NumberVariable
import io.hamal.lib.common.value.serde.ValueHonAdapters.ObjectVariable
import io.hamal.lib.common.value.serde.ValueHonAdapters.SnowflakeIdVariable
import io.hamal.lib.common.value.serde.ValueHonAdapters.StringVariable
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Instant

internal class ValueAdapterHonTest {

//@Nested inner class ArrayTest {
//
//    @Test
//    fun write() {
//        val result = serde.write(
//            ValueArray.builder()
//                .append("some-string-value")
//                .append(true)
//                .append(42)
//                .append(ValueDecimal(123))
//                .build()
//        )
//        assertThat(result, equalTo("""["some-string-value",true,42.0,"123"]"""))
//    }
//
//    @Test
//    fun read() {
//        val expected = ValueArray.builder()
//            .append("some-string-value")
//            .append(true)
//            .append(42)
//            .build()
//
//        val result = serde.read<ValueArray>("""["some-string-value",true,42.0]""")
//        assertThat(result, equalTo(expected))
//    }
//
//    private val serde = Serde.hon()
//        .register(SerdeModuleValueHon)
//
//
//}

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
            .register(TestObject::class, BooleanVariable(::TestObject))


        inner class TestObject(override val value: ValueBoolean) : ValueVariableBoolean()
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
            .register(TestObject::class, InstantVariable(::TestObject))

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
            .register(TestObject::class, NumberVariable(::TestObject))


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
                equalTo("""{"type":"Unknown","value":{"some-string":{"type":"String","value":"some-string-value"},"some-boolean":{"type":"Boolean","value":"true"},"some-number":{"type":"Number","value":"42.0"},"some-decimal":{"type":"Decimal","value":"123"}}}""")
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
                """{"type":"Unknown","value":{"some-string":{"type":"String","value":"some-string-value"},"some-boolean":{"type":"Boolean","value":"true"},"some-number":{"type":"Number","value":"42.0"},"some-decimal":{"type":"Decimal","value":"123"}}}"""
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
                equalTo("""{"type":"Unknown","value":{"some-string":{"type":"String","value":"some-string-value"},"some-boolean":{"type":"Boolean","value":"true"},"some-number":{"type":"Number","value":"42.0"},"some-decimal":{"type":"Decimal","value":"123"}}}""")
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
                """{"type":"Unknown","value":{"some-string":{"type":"String","value":"some-string-value"},"some-boolean":{"type":"Boolean","value":"true"},"some-number":{"type":"Number","value":"42.0"},"some-decimal":{"type":"Decimal","value":"123"}}}"""
            )
            assertThat(result, equalTo(expected))
        }

        private val serde = Serde.hon()
            .register(SerdeModuleValueHon)
            .register(TestObject::class, ObjectVariable(::TestObject))


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
            .register(TestObject::class, SnowflakeIdVariable(::TestObject))


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
            .register(TestObject::class, StringVariable(::TestObject))


        inner class TestObject(override val value: ValueString) : ValueVariableString()
    }

}