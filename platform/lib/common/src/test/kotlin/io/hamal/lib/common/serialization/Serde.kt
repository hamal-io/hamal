package io.hamal.lib.common.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.common.hot.HotArray
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.snowflake.SnowflakeId
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

internal object HotArrayAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(HotArray.builder().add(1).add("latest").build())
        assertThat(result, equalTo("[1,\"latest\"]"))
    }

    @Test
    fun deserialize() {
        val expected = HotArray.builder().add(1).add("latest").build()
        val result = testDelegate.fromJson("[1, \"latest\"]", HotArray::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(HotArray::class.java, HotArrayAdapter).create()
}

internal object HotObjectAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(HotObject.builder().set("KEY_1", 1).set("KEY_2", "latest").build())
        assertThat(result, equalTo("{\"KEY_1\":1,\"KEY_2\":\"latest\"}"))
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
        TestIdValueObject::class.java,
        ValueObjectIdAdapter(::TestIdValueObject)
    ).create()

    private class TestIdValueObject(
        override val value: SnowflakeId
    ) : ValueObjectId()
}


internal object ValueObjectStringAdapterTest {

    @Test
    fun serialize() {
        val result = testDelegate.toJson(TestStringValueObject("Hamal Rocks"))
        assertThat(result, equalTo("\"Hamal Rocks\""))
    }

    @Test
    fun deserialize() {
        val expected = TestStringValueObject("Hamal Rocks")
        val result = testDelegate.fromJson("\"Hamal Rocks\"", TestStringValueObject::class.java)
        assertThat(result, equalTo(expected))
    }

    private val testDelegate: Gson = GsonBuilder().registerTypeAdapter(
        TestStringValueObject::class.java,
        ValueObjectStringAdapter(::TestStringValueObject)
    ).create()

    private class TestStringValueObject(
        override val value: String
    ) : ValueObjectString()
}
