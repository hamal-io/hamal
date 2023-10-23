package io.hamal.extension.unsafe.net.http.converter

import io.hamal.lib.kua.type.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class ConvertToJsonTest {

    @Test
    fun `ArrayType - empty`() {
        val result = ArrayType().toJson()
        assertThat(result, equalTo(JsonArray(listOf())))
    }

    @Test
    fun `ArrayType - content`() {
        val result = ArrayType(
            mutableMapOf(
                1 to NumberType(42.0),
                2 to StringType("hamal")
            )
        ).toJson()

        assertThat(
            result, equalTo(
                JsonArray(
                    listOf(
                        JsonPrimitive(42.0),
                        JsonPrimitive("hamal")
                    )
                )
            )
        )
    }

    @Test
    fun `CodeType`() {
        val testInstance = CodeType("hack_the_planet()")
        val result = testInstance.toJson()
        assertThat(result, equalTo(JsonPrimitive("hack_the_planet()")))
    }

    @Test
    fun `DecimalType`() {
        val testInstance = DecimalType("123456789.987654321")
        val result = testInstance.toJson()
        assertThat(result, equalTo(JsonPrimitive("123456789.987654321")))
    }

    @Test
    fun `ErrorType`() {
        val testInstance = ErrorType("That went not as planned")
        val result = testInstance.toJson()
        assertThat(result, equalTo(JsonPrimitive("That went not as planned")))
    }

    @Test
    fun `False`() {
        val result = False.toJson()
        assertThat(result, equalTo(JsonPrimitive(false)))
    }

    @Test
    fun `MapType - empty`() {
        val result = MapType().toJson()
        assertThat(result, equalTo(JsonObject(mapOf())))
    }

    @Test
    fun `MapType - content`() {
        val result = MapType(
            mutableMapOf(
                "answer" to NumberType(42.0),
                "boolean_true" to True
            )
        ).toJson()

        assertThat(
            result, equalTo(
                JsonObject(
                    mapOf(
                        "answer" to JsonPrimitive(42.0),
                        "boolean_true" to JsonPrimitive(true)
                    )
                )
            )
        )
    }

    @Test
    fun `NilType`() {
        val result = NilType.toJson()
        assertThat(result, equalTo(JsonNull))
    }

    @Test
    fun `NumberType`() {
        val testInstance = NumberType(42.24)
        val result = testInstance.toJson()
        assertThat(result, equalTo(JsonPrimitive(42.24)))
    }

    @Test
    fun `StringType`() {
        val testInstance = StringType("hamal")
        val result = testInstance.toJson()
        assertThat(result, equalTo(JsonPrimitive("hamal")))
    }

    @Test
    fun `True`() {
        val result = True.toJson()
        assertThat(result, equalTo(JsonPrimitive(true)))
    }

}

internal class ConvertToTypeTest {

    @Nested
    inner class ArrayTest {
        @Test
        fun `Empty array`() {
            val testInstance = JsonArray(emptyList())
            val result = testInstance.convertToType()
            assertThat(result, equalTo(ArrayType()))
        }

        @Test
        fun `Array array`() {
            val testInstance = JsonArray(
                listOf(
                    JsonArray(
                        listOf(
                            JsonPrimitive("answer"),
                            JsonPrimitive(42)
                        )
                    ),
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    ArrayType(
                        mutableMapOf(
                            1 to ArrayType(
                                mutableMapOf(
                                    1 to StringType("answer"),
                                    2 to NumberType(42)
                                )
                            )
                        )
                    )
                )
            )
        }

        @Test
        fun `Boolean array`() {
            val testInstance = JsonArray(
                listOf(
                    JsonPrimitive(true),
                    JsonPrimitive(false)
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    ArrayType(
                        mutableMapOf(
                            1 to True,
                            2 to False
                        )
                    )
                )
            )
        }

        @Test
        fun `Decimal array`() {
            val testInstance = JsonArray(
                listOf(
                    JsonPrimitive("42.24"),
                    JsonPrimitive("1337")
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    ArrayType(
                        mutableMapOf(
                            1 to DecimalType("42.24"),
                            2 to DecimalType("1337")
                        )
                    )
                )
            )
        }


        @Test
        fun `Map array`() {
            val testInstance = JsonArray(
                listOf(
                    JsonObject(
                        mutableMapOf(
                            "key" to JsonPrimitive("answer"),
                            "value" to JsonPrimitive(42)
                        )
                    ),
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    ArrayType(
                        mutableMapOf(
                            1 to MapType(
                                mutableMapOf(
                                    "key" to StringType("answer"),
                                    "value" to NumberType(42)
                                )
                            )
                        )
                    )
                )
            )
        }

        @Test
        fun `Nil array`() {
            val testInstance = JsonArray(
                listOf(JsonNull)
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    ArrayType(
                        mutableMapOf(
                            1 to NilType,
                        )
                    )
                )
            )
        }

        @Test
        fun `Number array`() {
            val testInstance = JsonArray(
                listOf(
                    JsonPrimitive(42.24),
                    JsonPrimitive(1337)
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    ArrayType(
                        mutableMapOf(
                            1 to NumberType(42.24),
                            2 to NumberType(1337)
                        )
                    )
                )
            )
        }


        @Test
        fun `String array`() {
            val testInstance = JsonArray(
                listOf(
                    JsonPrimitive("hamal-one"),
                    JsonPrimitive("hamal-two")
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    ArrayType(
                        mutableMapOf(
                            1 to StringType("hamal-one"),
                            2 to StringType("hamal-two")
                        )
                    )
                )
            )
        }

    }

    @Nested
    inner class MapTest {
        @Test
        fun `Empty map`() {
            val testInstance = JsonObject(emptyMap())
            val result = testInstance.convertToType()
            assertThat(result, equalTo(MapType()))
        }

        @Test
        fun `Array map`() {
            val testInstance = JsonObject(
                mutableMapOf(
                    "key" to JsonArray(
                        listOf(
                            JsonPrimitive("answer"),
                            JsonPrimitive(42)
                        )
                    ),
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    MapType(
                        mutableMapOf(
                            "key" to ArrayType(
                                mutableMapOf(
                                    1 to StringType("answer"),
                                    2 to NumberType(42)
                                )
                            )
                        )
                    )
                )
            )
        }

        @Test
        fun `Boolean map`() {
            val testInstance = JsonObject(
                mapOf(
                    "v1" to JsonPrimitive(true),
                    "v2" to JsonPrimitive(false)
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    MapType(
                        mutableMapOf(
                            "v1" to True,
                            "v2" to False
                        )
                    )
                )
            )
        }

        @Test
        fun `Decimal map`() {
            val testInstance = JsonObject(
                mapOf(
                    "v1" to JsonPrimitive("42.24"),
                    "v2" to JsonPrimitive("1337")
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    MapType(
                        mutableMapOf(
                            "v1" to DecimalType("42.24"),
                            "v2" to DecimalType("1337")
                        )
                    )
                )
            )
        }


        @Test
        fun `Map map`() {
            val testInstance = JsonObject(
                mapOf(
                    "v1" to JsonObject(
                        mutableMapOf(
                            "key" to JsonPrimitive("answer"),
                            "value" to JsonPrimitive(42)
                        )
                    ),
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    MapType(
                        mutableMapOf(
                            "v1" to MapType(
                                mutableMapOf(
                                    "key" to StringType("answer"),
                                    "value" to NumberType(42)
                                )
                            )
                        )
                    )
                )
            )
        }

        @Test
        fun `Nil map`() {
            val testInstance = JsonObject(
                mapOf(
                    "v1" to JsonNull,
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    MapType(
                        mutableMapOf(
                            "v1" to NilType,
                        )
                    )
                )
            )
        }

        @Test
        fun `Number map`() {
            val testInstance = JsonObject(
                mapOf(
                    "v1" to JsonPrimitive(42.24),
                    "v2" to JsonPrimitive(1337)
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    MapType(
                        mutableMapOf(
                            "v1" to NumberType(42.24),
                            "v2" to NumberType(1337)
                        )
                    )
                )
            )
        }

        @Test
        fun `String map`() {
            val testInstance = JsonObject(
                mapOf(
                    "v1" to JsonPrimitive("hamal-one"),
                    "v2" to JsonPrimitive("hamal-two")
                )
            )

            val result = testInstance.convertToType()
            assertThat(
                result, equalTo(
                    MapType(
                        mutableMapOf(
                            "v1" to StringType("hamal-one"),
                            "v2" to StringType("hamal-two")
                        )
                    )
                )
            )
        }

    }
}
