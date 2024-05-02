package io.hamal.lib.common.value

import com.google.gson.Gson
import io.hamal.lib.common.serialization.GsonFactoryBuilder
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

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .build()
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

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .register(TestObject::class, BooleanVariable(::TestObject))
        .build()

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
        val result = testDelegate.fromJson("\"42.0\"", ValueDecimal::class.java)
        assertThat(result, equalTo(expectedDecimal))
    }

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .build()
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

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .build()
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

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .register(TestObject::class, InstantVariable(::TestObject))
        .build()

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

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .build()
}

internal object ValueVariableNumberJsonAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(TestObject(ValueNumber(1337)))
        assertThat(result, equalTo("1337.0"))
    }

    @Test
    fun deserialize() {
        val expected = TestObject(ValueNumber(1337))
        val result = testDelegate.fromJson("1337", TestObject::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .register(TestObject::class, NumberVariable(::TestObject))
        .build()

    private class TestObject(override val value: ValueNumber) : ValueVariableNumber()
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

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .build()

}

internal object ValueObjectVariableJsonAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(
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
    fun deserialize() {
        val expected = TestObject(
            ValueObject.builder()
                .set("some-string", "some-string-value")
                .set("some-boolean", true)
                .set("some-number", 42)
                .build()
        )
        val result = testDelegate.fromJson(
            """{"some-string":"some-string-value","some-boolean":true,"some-number":42.0}""",
            TestObject::class.java
        )
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .register(TestObject::class, ObjectVariable(::TestObject))
        .build()

    private class TestObject(override val value: ValueObject) : ValueVariableObject()
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

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .build()
}

internal object ValueSnowflakeIdVariableJsonAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(TestObject(ValueSnowflakeId(SnowflakeId(42))))
        assertThat(result, equalTo("\"2a\""))
    }

    @Test
    fun deserialize() {
        val expected = TestObject(ValueSnowflakeId(SnowflakeId(42)))
        val result = testDelegate.fromJson("\"2a\"", TestObject::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .register(TestObject::class, SnowflakeIdVariable(::TestObject))
        .build()

    private class TestObject(
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

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .build()
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

    private val testDelegate: Gson = GsonFactoryBuilder()
        .register(ValueJsonModule)
        .register(TestObject::class, StringVariable(::TestObject))
        .build()

    private class TestObject(override val value: ValueString) : ValueVariableString()
}
