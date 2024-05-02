package io.hamal.lib.common.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.*
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

internal object ValueVariableSnowflakeIdAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(TestIdValueObject(ValueSnowflakeId(SnowflakeId(42))))
        assertThat(result, equalTo("\"2a\""))
    }

    @Test
    fun deserialize() {
        val expected = TestIdValueObject(ValueSnowflakeId(SnowflakeId(42)))
        val result = testDelegate.fromJson("\"2a\"", TestIdValueObject::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(
        TestIdValueObject::class.java, JsonAdapters.SnowflakeId(::TestIdValueObject)
    ).create()

    private class TestIdValueObject(
        override val value: ValueSnowflakeId
    ) : ValueVariableSnowflakeId()
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


internal object ValueVariableNumberAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(TestValueVariableNumber(ValueNumber(1337)))
        assertThat(result, equalTo("1337.0"))
    }

    @Test
    fun deserialize() {
        val expected = TestValueVariableNumber(ValueNumber(1337))
        val result = testDelegate.fromJson("1337", TestValueVariableNumber::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(
        TestValueVariableNumber::class.java, JsonAdapters.Number(::TestValueVariableNumber)
    ).create()

    private class TestValueVariableNumber(
        override val value: ValueNumber
    ) : ValueVariableNumber()
}


internal object ValueVariableInstantAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(TestValueVariableInstant(ValueInstant(Instant.ofEpochMilli(112233445000))))
        assertThat(result, equalTo("\"1973-07-22T23:57:25Z\""))
    }

    @Test
    fun deserialize() {
        val expected = TestValueVariableInstant(ValueInstant(Instant.ofEpochMilli(112233445000)))
        val result = testDelegate.fromJson("\"1973-07-22T23:57:25Z\"", TestValueVariableInstant::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson =
        GsonBuilder()
            .registerTypeAdapter(Instant::class.java, InstantAdapter)
            .registerTypeAdapter(TestValueVariableInstant::class.java, JsonAdapters.Instant(::TestValueVariableInstant))
            .create()

    private class TestValueVariableInstant(override val value: ValueInstant) : ValueVariableInstant()
}


internal object ValueVariableObjectAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(
            TestHotObjectValueObject(
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
    fun deserialize() {
        val expected = TestHotObjectValueObject(
            ValueObject.builder()
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
                JsonAdapters.Object(::TestHotObjectValueObject)
            )
            .create()

    private class TestHotObjectValueObject(
        override val value: ValueObject
    ) : ValueVariableObject()
}
