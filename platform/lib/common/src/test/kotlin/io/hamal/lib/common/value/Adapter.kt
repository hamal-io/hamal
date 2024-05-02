package io.hamal.lib.common.value

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hamal.lib.common.snowflake.SnowflakeId
import io.hamal.lib.common.value.ValueJsonAdapters.BooleanVariable
import io.hamal.lib.common.value.ValueJsonAdapters.InstantVariable
import io.hamal.lib.common.value.ValueJsonAdapters.NumberVariable
import io.hamal.lib.common.value.ValueJsonAdapters.ObjectVariable
import io.hamal.lib.common.value.ValueJsonAdapters.SnowflakeIdVariable
import io.hamal.lib.common.value.ValueJsonAdapters.StringVariable
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal object ValueBooleanJsonAdapterTest {
    @Test
    fun serialize() {
        val someBoolean = ValueTrue
        val result = testDelegate.toJson(someBoolean)
        assertThat(result, equalTo("true"))
    }

    @Test
    fun deserialize() {
        val expectedBoolean = ValueFalse
        val result = testDelegate.fromJson("false", ValueBoolean::class.java)
        assertThat(result, equalTo(expectedBoolean))
    }

    private val testDelegate: Gson = GsonBuilder()
        .registerTypeAdapter(ValueBoolean::class.java, ValueJsonAdapters.Boolean)
        .create()
}

internal object ValueBooleanVariableJsonAdapterTest {

    @Test
    fun serialize() {
        val someBoolean = TestObject(ValueBoolean(true))
        val result = testDelegate.toJson(someBoolean)
        assertThat(result, equalTo("true"))
    }

    @Test
    fun deserialize() {
        val expectedBoolean = TestObject(ValueBoolean(false))
        val result = testDelegate.fromJson("false", TestObject::class.java)
        assertThat(result, equalTo(expectedBoolean))
    }

    private val testDelegate: Gson = GsonBuilder()
        .registerTypeAdapter(ValueBoolean::class.java, ValueJsonAdapters.Boolean)
        .registerTypeAdapter(TestObject::class.java, BooleanVariable(::TestObject))
        .create()

    private class TestObject(override val value: ValueBoolean) : ValueVariableBoolean()
}

internal object ValueDecimalJsonAdapterTest {
    @Test
    fun serialize() {
        val someDecimal = ValueDecimal(42)
        val result = testDelegate.toJson(someDecimal)
        assertThat(result, equalTo("\"42\""))
    }

    @Test
    fun deserialize() {
        val expectedDecimal = ValueDecimal(42)
        val result = testDelegate.fromJson("42.0", ValueDecimal::class.java)
        assertThat(result, equalTo(expectedDecimal))
    }

    private val testDelegate: Gson = GsonBuilder()
        .registerTypeAdapter(ValueDecimal::class.java, ValueJsonAdapters.Decimal)
        .create()
}

internal object ValueInstantJsonAdapterTest {

    @Test
    fun serialize() {
        val someInstant = ValueInstant(Instant.ofEpochSecond(1641454487))
        val result = testDelegate.toJson(someInstant)
        assertThat(result, equalTo("\"2022-01-06T07:34:47Z\""))
    }

    @Test
    fun deserialize() {
        val expectedInstant = ValueInstant(Instant.ofEpochSecond(1641454487))
        val result = testDelegate.fromJson("\"2022-01-06T07:34:47Z\"", ValueInstant::class.java)
        assertThat(result, equalTo(expectedInstant))
    }

    private val testDelegate: Gson = GsonBuilder()
        .registerTypeAdapter(ValueInstant::class.java, ValueJsonAdapters.Instant)
        .create()

}

internal object ValueInstantVariableJsonAdapterTest {

    @Test
    fun serialize() {
        val someInstant = TestObject(ValueInstant(Instant.ofEpochSecond(1641454487)))
        val result = testDelegate.toJson(someInstant)
        assertThat(result, equalTo("\"2022-01-06T07:34:47Z\""))
    }

    @Test
    fun deserialize() {
        val expectedInstant = TestObject(ValueInstant(Instant.ofEpochSecond(1641454487)))
        val result = testDelegate.fromJson("\"2022-01-06T07:34:47Z\"", TestObject::class.java)
        assertThat(result, equalTo(expectedInstant))
    }

    private val testDelegate: Gson = GsonBuilder()
        .registerTypeAdapter(ValueInstant::class.java, ValueJsonAdapters.Instant)
        .registerTypeAdapter(TestObject::class.java, InstantVariable(::TestObject))
        .create()

    private class TestObject(override val value: ValueInstant) : ValueVariableInstant()
}

internal object ValueNumberJsonAdapterTest {
    @Test
    fun serialize() {
        val someNumber = ValueNumber(42)
        val result = testDelegate.toJson(someNumber)
        assertThat(result, equalTo("42.0"))
    }

    @Test
    fun deserialize() {
        val expectedNumber = ValueNumber(42)
        val result = testDelegate.fromJson("42.0", ValueNumber::class.java)
        assertThat(result, equalTo(expectedNumber))
    }

    private val testDelegate: Gson = GsonBuilder()
        .registerTypeAdapter(ValueNumber::class.java, ValueJsonAdapters.Number)
        .create()
}

internal object ValueVariableNumberJsonAdapterTest {

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
        TestValueVariableNumber::class.java, NumberVariable(::TestValueVariableNumber)
    ).create()

    private class TestValueVariableNumber(override val value: ValueNumber) : ValueVariableNumber()
}

internal object ValueObjectJsonAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(
            ValueObject.builder()
                .set("some-string", "some-string-value")
                .set("some-boolean", true)
                .set("some-number", 42)
                .build()
        )
        assertThat(result, equalTo("""{"some-string":"some-string-value","some-boolean":true,"some-number":42.0}"""))
    }

    @Test
    fun deserialize() {
        val expected = ValueObject.builder()
            .set("some-string", "some-string-value")
            .set("some-boolean", true)
            .set("some-number", 42)
            .build()

        val result = testDelegate.fromJson(
            """{"some-string":"some-string-value","some-boolean":true,"some-number":42.0}""",
            ValueObject::class.java
        )
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson =
        GsonBuilder()
            .registerTypeAdapter(ValueObject::class.java, ValueJsonAdapters.Object)
            .create()
}

internal object ValueObjectVariableJsonAdapterTest {

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
            .registerTypeAdapter(ValueObject::class.java, ValueJsonAdapters.Object)
            .registerTypeAdapter(TestHotObjectValueObject::class.java, ObjectVariable(::TestHotObjectValueObject))
            .create()

    private class TestHotObjectValueObject(override val value: ValueObject) : ValueVariableObject()
}

internal object ValueSnowflakeIdJsonAdapterTest {
    @Test
    fun serialize() {
        val someSnowflakeId = ValueSnowflakeId(SnowflakeId(42))
        val result = testDelegate.toJson(someSnowflakeId)
        assertThat(result, equalTo("\"2a\""))
    }

    @Test
    fun deserialize() {
        val expectedSnowflakeId = ValueSnowflakeId(SnowflakeId(42))
        val result = testDelegate.fromJson("\"2a\"", ValueSnowflakeId::class.java)
        assertThat(result, equalTo(expectedSnowflakeId))
    }

    private val testDelegate: Gson = GsonBuilder()
        .registerTypeAdapter(ValueSnowflakeId::class.java, ValueJsonAdapters.SnowflakeId)
        .create()
}

internal object ValueSnowflakeIdVariableJsonAdapterTest {

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
        TestIdValueObject::class.java, SnowflakeIdVariable(::TestIdValueObject)
    ).create()

    private class TestIdValueObject(
        override val value: ValueSnowflakeId
    ) : ValueVariableSnowflakeId()
}

internal object ValueStringJsonAdapterTest {
    @Test
    fun serialize() {
        val someString = ValueString("Hamal Rocks")
        val result = testDelegate.toJson(someString)
        assertThat(result, equalTo("\"Hamal Rocks\""))
    }

    @Test
    fun deserialize() {
        val expectedString = ValueString("Hamal Rocks")
        val result = testDelegate.fromJson("\"Hamal Rocks\"", ValueString::class.java)
        assertThat(result, equalTo(expectedString))
    }

    private val testDelegate: Gson = GsonBuilder()
        .registerTypeAdapter(ValueString::class.java, ValueJsonAdapters.String)
        .create()
}

internal object ValueStringVariableJsonAdapterTest {

    @Test
    fun serialize() {
        val someString = TestObject(ValueString("Hamal Rocks"))
        val result = testDelegate.toJson(someString)
        assertThat(result, equalTo("\"Hamal Rocks\""))
    }

    @Test
    fun deserialize() {
        val expectedString = TestObject(ValueString("Hamal Rocks"))
        val result = testDelegate.fromJson("\"Hamal Rocks\"", TestObject::class.java)
        assertThat(result, equalTo(expectedString))
    }

    private val testDelegate: Gson = GsonBuilder()
        .registerTypeAdapter(ValueString::class.java, ValueJsonAdapters.String)
        .registerTypeAdapter(TestObject::class.java, StringVariable(::TestObject))
        .create()

    private class TestObject(override val value: ValueString) : ValueVariableString()
}
