package io.hamal.lib.common.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hamal.lib.common.domain.*
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal object HotArrayAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(HotArray.builder().append(1).append("latest").build())
        assertThat(result, equalTo("[1.0,\"latest\"]"))
    }

    @Test
    fun deserialize() {
        val expected = HotArray.builder().append(1).append("latest").build()
        val result = testDelegate.fromJson("[1.0,\"latest\"]", HotArray::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(HotArray::class.java, HotArrayAdapter).create()
}

internal object HotObjectAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(HotObject.builder().set("KEY_1", 1).set("KEY_2", "latest").build())
        assertThat(result, equalTo("{\"KEY_1\":1.0,\"KEY_2\":\"latest\"}"))
    }

    @Test
    fun deserialize() {
        val expected = HotObject.builder().set("KEY_1", 1).set("KEY_2", "latest").build()
        val result = testDelegate.fromJson("{\"KEY_1\":1,\"KEY_2\":\"latest\"}", HotObject::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(HotObject::class.java, HotObjectAdapter).create()
}

internal object InstantAdapterTest {

    @Test
    fun serialize() {
        val someInstant = Instant.ofEpochSecond(1641454487)
        val result = testDelegate.toJson(someInstant)
        assertThat(result, equalTo("\"2022-01-06T07:34:47Z\""))
    }

    @Test
    fun deserialize() {
        val expectedInstant = Instant.ofEpochSecond(1641454487)
        val result = testDelegate.fromJson("\"2022-01-06T07:34:47Z\"", Instant::class.java)
        assertThat(result, equalTo(expectedInstant))
    }

    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(Instant::class.java, InstantAdapter).create()

}

internal object ValueObjectIdAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(TestIdValueObject(SnowflakeId(42)))
        assertThat(result, equalTo("\"2a\""))
    }

    @Test
    fun deserialize() {
        val expected = TestIdValueObject(SnowflakeId(42))
        val result = testDelegate.fromJson("\"2a\"", TestIdValueObject::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(
        TestIdValueObject::class.java, ValueObjectIdAdapter(::TestIdValueObject)
    ).create()

    private class TestIdValueObject(
        override val value: SnowflakeId
    ) : ValueObjectId()
}


internal object ValueVariableStringAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(TestStringValueVariable(ValueString("Hamal Rocks")))
        assertThat(result, equalTo("\"Hamal Rocks\""))
    }

    @Test
    fun deserialize() {
        val expected = TestStringValueVariable(ValueString("Hamal Rocks"))
        val result = testDelegate.fromJson("\"Hamal Rocks\"", TestStringValueVariable::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(
        TestStringValueVariable::class.java, JsonAdapters.String(::TestStringValueVariable)
    ).create()

    private class TestStringValueVariable(
        override val value: ValueString
    ) : ValueVariableString()
}


internal object ValueObjectIntAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(TestIntValueObject(1337))
        assertThat(result, equalTo("1337"))
    }

    @Test
    fun deserialize() {
        val expected = TestIntValueObject(1337)
        val result = testDelegate.fromJson("1337", TestIntValueObject::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(
        TestIntValueObject::class.java, ValueObjectIntAdapter(::TestIntValueObject)
    ).create()

    private class TestIntValueObject(
        override val value: Int
    ) : ValueObjectInt()
}

internal object ValueObjectULongAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(TestULongValueObject(Long.MAX_VALUE))
        assertThat(result, equalTo("9223372036854775807"))
    }

    @Test
    fun deserialize() {
        val expected = TestULongValueObject(Long.MAX_VALUE)
        val result = testDelegate.fromJson("9223372036854775807", TestULongValueObject::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(
        TestULongValueObject::class.java, ValueObjectLongAdapter(::TestULongValueObject)
    ).create()

    private class TestULongValueObject(
        override val value: Long
    ) : ValueObjectLong()
}

internal object ValueObjectInstantAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(TestInstantValueObject(Instant.ofEpochMilli(112233445000)))
        assertThat(result, equalTo("\"1973-07-22T23:57:25Z\""))
    }

    @Test
    fun deserialize() {
        val expected = TestInstantValueObject(Instant.ofEpochMilli(112233445000))
        val result = testDelegate.fromJson("\"1973-07-22T23:57:25Z\"", TestInstantValueObject::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson =
        GsonBuilder().registerTypeAdapter(Instant::class.java, InstantAdapter).registerTypeAdapter(
            TestInstantValueObject::class.java, ValueObjectInstantAdapter(::TestInstantValueObject)
        ).create()

    private class TestInstantValueObject(
        override val value: Instant
    ) : ValueObjectInstant()
}


internal object ValueObjectHotObjectAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(
            TestHotObjectValueObject(
                HotObject.builder()
                    .set("some-string", "some-string-value")
                    .set("some-boolean", true)
                    .set("some-number", 42)
                    .build()
            )
        )
        assertThat(result, equalTo("""{"some-string":"some-string-value","some-boolean":true,"some-number":42.0}"""))
    }

    @Test
    fun deserialize() {
        val expected = TestHotObjectValueObject(
            HotObject.builder()
                .set("some-string", "some-string-value")
                .set("some-boolean", true)
                .set("some-number", 42)
                .build()
        )
        val result = testDelegate.fromJson(
            """{"some-string":"some-string-value","some-boolean":true,"some-number":42.0}""",
            TestHotObjectValueObject::class.java
        )
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson =
        GsonBuilder()
            .registerTypeAdapter(HotObject::class.java, HotObjectAdapter)
            .registerTypeAdapter(
                TestHotObjectValueObject::class.java,
                ValueObjectHotObjectAdapter(::TestHotObjectValueObject)
            )
            .create()

    private class TestHotObjectValueObject(
        override val value: HotObject
    ) : ValueObjectHotObject()
}
