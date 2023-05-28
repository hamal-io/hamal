package io.hamal.lib.domain

import io.hamal.lib.domain.value.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.math.BigDecimal

@Serializable
data class Input(val values: List<TableEntry>)

fun main() {
    val i = Input(
        listOf(
            StringValue("a") to StringValue("ABC"),
            StringValue("t") to TableValue(
                listOf(
                    StringValue("hamal") to StringValue("rocks"),
                    StringValue("anwser") to NumberValue(42),
                    StringValue("Nested") to TableValue(
                        listOf(
                            StringValue("x") to StringValue("y")
                        )
                    ),
//                    NumberValue(42) to StringValue("Thats the answer")
                )
            )
        )
    )

    val n = NumberValue(BigDecimal("1234.43211231231231231231231231231231231231231231231231231324"))
//    val n = NumberValue(123)

    println(Json { }.encodeToString(Input.serializer(), i))
    println(Json { }.encodeToString(NumberValue.serializer(), n))

}