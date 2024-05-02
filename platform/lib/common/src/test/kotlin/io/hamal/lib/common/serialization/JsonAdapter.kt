package io.hamal.lib.common.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hamal.lib.common.value.ValueInstant
import io.hamal.lib.common.value.ValueVariableInstant
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

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
        .registerTypeAdapter(ValueInstant::class.java, JsonAdapters.Instant)
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
        .registerTypeAdapter(ValueInstant::class.java, JsonAdapters.Instant)
        .registerTypeAdapter(TestObject::class.java, JsonAdapters.InstantVariable(::TestObject))
        .create()

    private class TestObject(override val value: ValueInstant) : ValueVariableInstant()
}

//internal object ValueVariableSnowflakeIdVariableAdapterTest {
//
//    @Test
//    fun serialize() {
//        val result = testDelegate.toJson(TestIdValueObject(ValueSnowflakeId(SnowflakeId(42))))
//        assertThat(result, equalTo("\"2a\""))
//    }
//
//    @Test
//    fun deserialize() {
//        val expected = TestIdValueObject(ValueSnowflakeId(SnowflakeId(42)))
//        val result = testDelegate.fromJson("\"2a\"", TestIdValueObject::class.java)
//        assertThat(result, equalTo(expected))
//    }
//
//    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(
//        TestIdValueObject::class.java, JsonAdapters.SnowflakeIdVariable(::TestIdValueObject)
//    ).create()
//
//    private class TestIdValueObject(
//        override val value: ValueSnowflakeId
//    ) : ValueVariableSnowflakeId()
//}
//
//
//internal object ValueVariableStringVariableAdapterTest {
//
//    @Test
//    fun serialize() {
//        val result = testDelegate.toJson(TestStringValueVariable(ValueString("Hamal Rocks")))
//        assertThat(result, equalTo("\"Hamal Rocks\""))
//    }
//
//    @Test
//    fun deserialize() {
//        val expected = TestStringValueVariable(ValueString("Hamal Rocks"))
//        val result = testDelegate.fromJson("\"Hamal Rocks\"", TestStringValueVariable::class.java)
//        assertThat(result, equalTo(expected))
//    }
//
//    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(
//        TestStringValueVariable::class.java, JsonAdapters.StringVariable(::TestStringValueVariable)
//    ).create()
//
//    private class TestStringValueVariable(
//        override val value: ValueString
//    ) : ValueVariableString()
//}
//
//
//internal object ValueVariableNumberVariableAdapterTest {
//
//    @Test
//    fun serialize() {
//        val result = testDelegate.toJson(TestValueVariableNumber(ValueNumber(1337)))
//        assertThat(result, equalTo("1337.0"))
//    }
//
//    @Test
//    fun deserialize() {
//        val expected = TestValueVariableNumber(ValueNumber(1337))
//        val result = testDelegate.fromJson("1337", TestValueVariableNumber::class.java)
//        assertThat(result, equalTo(expected))
//    }
//
//    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(
//        TestValueVariableNumber::class.java, JsonAdapters.NumberVariable(::TestValueVariableNumber)
//    ).create()
//
//    private class TestValueVariableNumber(
//        override val value: ValueNumber
//    ) : ValueVariableNumber()
//}
//
//
//internal object ValueVariableInstantVariableAdapterTest {
//
//    @Test
//    fun serialize() {
//        val result = testDelegate.toJson(TestValueVariableInstant(ValueInstant(Instant.ofEpochMilli(112233445000))))
//        assertThat(result, equalTo("\"1973-07-22T23:57:25Z\""))
//    }
//
//    @Test
//    fun deserialize() {
//        val expected = TestValueVariableInstant(ValueInstant(Instant.ofEpochMilli(112233445000)))
//        val result = testDelegate.fromJson("\"1973-07-22T23:57:25Z\"", TestValueVariableInstant::class.java)
//        assertThat(result, equalTo(expected))
//    }
//
//    private val testDelegate: Gson =
//        GsonBuilder()
//            .registerTypeAdapter(Instant::class.java, InstantAdapter)
//            .registerTypeAdapter(TestValueVariableInstant::class.java, JsonAdapters.InstantVariable(::TestValueVariableInstant))
//            .create()
//
//    private class TestValueVariableInstant(override val value: ValueInstant) : ValueVariableInstant()
//}
//
//
//internal object ValueVariableObjectVariableAdapterTest {
//
//    @Test
//    fun serialize() {
//        val result = testDelegate.toJson(
//            TestHotObjectValueObject(
//                ValueObject.builder()
//                    .set("some-string", "some-string-value")
//                    .set("some-boolean", true)
//                    .set("some-number", 42)
//                    .build()
//            )
//        )
//        assertThat(result, equalTo("""{"some-string":"some-string-value","some-boolean":true,"some-number":42.0}"""))
//    }
//
//    @Test
//    fun deserialize() {
//        val expected = TestHotObjectValueObject(
//            ValueObject.builder()
//                .set("some-string", "some-string-value")
//                .set("some-boolean", true)
//                .set("some-number", 42)
//                .build()
//        )
//        val result = testDelegate.fromJson(
//            """{"some-string":"some-string-value","some-boolean":true,"some-number":42.0}""",
//            TestHotObjectValueObject::class.java
//        )
//        assertThat(result, equalTo(expected))
//    }
//
//    private val testDelegate: Gson =
//        GsonBuilder()
//            .registerTypeAdapter(HotObject::class.java, HotObjectAdapter)
//            .registerTypeAdapter(
//                TestHotObjectValueObject::class.java,
//                JsonAdapters.ObjectVariable(::TestHotObjectValueObject)
//            )
//            .create()
//
//    private class TestHotObjectValueObject(
//        override val value: ValueObject
//    ) : ValueVariableObject()
//}
