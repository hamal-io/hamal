package io.hamal.lib.common.value

import com.google.gson.Gson
import io.hamal.lib.common.serialization.Serde
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

internal object ValueArraySerdeAdapterTest {

    @Test
    fun write() {
        val result = testDelegate.toJson(
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

        val result = testDelegate.fromJson(
            """["some-string-value",true,42.0]""",
            ValueArray::class.java
        )
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .gson

}

internal object ValueBooleanSerdeAdapterTest {
    @Test
    fun write() {
        val someBoolean = ValueTrue
        val result = testDelegate.toJson(someBoolean)
        assertThat(result, equalTo("true"))
    }

    @Test
    fun read() {
        val expectedBoolean = ValueFalse
        val result = testDelegate.fromJson("false", ValueBoolean::class.java)
        assertThat(result, equalTo(expectedBoolean))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .gson
}

internal object ValueBooleanVariableSerdeAdapterTest {

    @Test
    fun write() {
        val someBoolean = TestObject(ValueBoolean(true))
        val result = testDelegate.toJson(someBoolean)
        assertThat(result, equalTo("true"))
    }

    @Test
    fun read() {
        val expectedBoolean = TestObject(ValueBoolean(false))
        val result = testDelegate.fromJson("false", TestObject::class.java)
        assertThat(result, equalTo(expectedBoolean))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .register(TestObject::class, BooleanVariable(::TestObject))
        .gson

    private class TestObject(override val value: ValueBoolean) : ValueVariableBoolean()
}

internal object ValueDecimalSerdeAdapterTest {
    @Test
    fun write() {
        val someDecimal = ValueDecimal(42)
        val result = testDelegate.toJson(someDecimal)
        assertThat(result, equalTo("\"42\""))
    }

    @Test
    fun read() {
        val expectedDecimal = ValueDecimal(42)
        val result = testDelegate.fromJson("\"42.0\"", ValueDecimal::class.java)
        assertThat(result, equalTo(expectedDecimal))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .gson
}

internal object ValueInstantSerdeAdapterTest {

    @Test
    fun write() {
        val someInstant = ValueInstant(Instant.ofEpochSecond(1641454487))
        val result = testDelegate.toJson(someInstant)
        assertThat(result, equalTo("\"2022-01-06T07:34:47Z\""))
    }

    @Test
    fun read() {
        val expectedInstant = ValueInstant(Instant.ofEpochSecond(1641454487))
        val result = testDelegate.fromJson("\"2022-01-06T07:34:47Z\"", ValueInstant::class.java)
        assertThat(result, equalTo(expectedInstant))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .gson
}

internal object ValueInstantVariableSerdeAdapterTest {

    @Test
    fun write() {
        val someInstant = TestObject(ValueInstant(Instant.ofEpochSecond(1641454487)))
        val result = testDelegate.toJson(someInstant)
        assertThat(result, equalTo("\"2022-01-06T07:34:47Z\""))
    }

    @Test
    fun read() {
        val expectedInstant = TestObject(ValueInstant(Instant.ofEpochSecond(1641454487)))
        val result = testDelegate.fromJson("\"2022-01-06T07:34:47Z\"", TestObject::class.java)
        assertThat(result, equalTo(expectedInstant))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .register(TestObject::class, InstantVariable(::TestObject))
        .gson

    private class TestObject(override val value: ValueInstant) : ValueVariableInstant()
}

internal object ValueNilSerdeAdapterTest {
    @Test
    fun write() {
        val someNil = ValueNil
        val result = testDelegate.toJson(someNil)
        assertThat(result, equalTo("null"))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .gson
}

internal object ValueNumberSerdeAdapterTest {
    @Test
    fun write() {
        val someNumber = ValueNumber(42)
        val result = testDelegate.toJson(someNumber)
        assertThat(result, equalTo("42.0"))
    }

    @Test
    fun read() {
        val expectedNumber = ValueNumber(42)
        val result = testDelegate.fromJson("42.0", ValueNumber::class.java)
        assertThat(result, equalTo(expectedNumber))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .gson
}

internal object ValueVariableNumberSerdeAdapterTest {

    @Test
    fun write() {
        val result = testDelegate.toJson(TestObject(ValueNumber(1337)))
        assertThat(result, equalTo("1337.0"))
    }

    @Test
    fun read() {
        val expected = TestObject(ValueNumber(1337))
        val result = testDelegate.fromJson("1337", TestObject::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .register(TestObject::class, NumberVariable(::TestObject))
        .gson

    private class TestObject(override val value: ValueNumber) : ValueVariableNumber()
}

internal object ValueObjectSerdeAdapterTest {

    @Test
    fun write() {
        val result = testDelegate.toJson(
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

        val result = testDelegate.fromJson(
            """{"some-string":"some-string-value","some-boolean":true,"some-number":42.0}""",
            ValueObject::class.java
        )
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .gson

}

internal object ValueObjectVariableSerdeAdapterTest {

    @Test
    fun write() {
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
    fun read() {
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

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .register(TestObject::class, ObjectVariable(::TestObject))
        .gson

    private class TestObject(override val value: ValueObject) : ValueVariableObject()
}

internal object ValueSnowflakeIdSerdeAdapterTest {
    @Test
    fun write() {
        val someSnowflakeId = ValueSnowflakeId(SnowflakeId(42))
        val result = testDelegate.toJson(someSnowflakeId)
        assertThat(result, equalTo("\"2a\""))
    }

    @Test
    fun read() {
        val expectedSnowflakeId = ValueSnowflakeId(SnowflakeId(42))
        val result = testDelegate.fromJson("\"2a\"", ValueSnowflakeId::class.java)
        assertThat(result, equalTo(expectedSnowflakeId))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .gson
}

internal object ValueSnowflakeIdVariableSerdeAdapterTest {

    @Test
    fun write() {
        val result = testDelegate.toJson(TestObject(ValueSnowflakeId(SnowflakeId(42))))
        assertThat(result, equalTo("\"2a\""))
    }

    @Test
    fun read() {
        val expected = TestObject(ValueSnowflakeId(SnowflakeId(42)))
        val result = testDelegate.fromJson("\"2a\"", TestObject::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .register(TestObject::class, SnowflakeIdVariable(::TestObject))
        .gson

    private class TestObject(
        override val value: ValueSnowflakeId
    ) : ValueVariableSnowflakeId()
}

internal object ValueStringSerdeAdapterTest {
    @Test
    fun write() {
        val someString = ValueString("Hamal Rocks")
        val result = testDelegate.toJson(someString)
        assertThat(result, equalTo("\"Hamal Rocks\""))
    }

    @Test
    fun read() {
        val expectedString = ValueString("Hamal Rocks")
        val result = testDelegate.fromJson("\"Hamal Rocks\"", ValueString::class.java)
        assertThat(result, equalTo(expectedString))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .gson
}

internal object ValueStringVariableSerdeAdapterTest {

    @Test
    fun write() {
        val someString = TestObject(ValueString("Hamal Rocks"))
        val result = testDelegate.toJson(someString)
        assertThat(result, equalTo("\"Hamal Rocks\""))
    }

    @Test
    fun read() {
        val expectedString = TestObject(ValueString("Hamal Rocks"))
        val result = testDelegate.fromJson("\"Hamal Rocks\"", TestObject::class.java)
        assertThat(result, equalTo(expectedString))
    }

    private val testDelegate: Gson = Serde.json()
        .register(SerdeModuleJsonValue)
        .register(TestObject::class, StringVariable(::TestObject))
        .gson

    private class TestObject(override val value: ValueString) : ValueVariableString()
}
