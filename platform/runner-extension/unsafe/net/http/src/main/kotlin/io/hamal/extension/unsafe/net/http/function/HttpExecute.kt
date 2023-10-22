package io.hamal.extension.unsafe.net.http.function

import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.SuccessHttpResponse
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import kotlinx.serialization.json.*

fun JsonElement.toTable(): TableType {
    val el = this
    return when (el) {
        is JsonPrimitive -> TODO()
        is JsonArray -> {
            ArrayType()
        }

        is JsonObject -> {
            val m = mutableMapOf<String, SerializableType>()
            el.entries.forEach { (key, value) ->
                when (value) {
                    is JsonArray -> {
                        m[key] = ArrayType()
                    }

                    is JsonObject -> {
                        m[key] = MapType()
                    }

                    is JsonPrimitive -> TODO()
                    JsonNull -> TODO()
                }
            }
            MapType(m)
        }

    }
}

class HttpExecuteFunction : Function1In2Out<ArrayType, ErrorType, TableType>(
    FunctionInput1Schema(ArrayType::class),
    FunctionOutput2Schema(ErrorType::class, TableType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ArrayType): Pair<ErrorType?, TableType?> {
        val results = mutableListOf<MapType>()
        for (idx in 0 until arg1.size) {
            val map = arg1.getMap(idx + 1)

            val method = map.getString("method")
            val url = map.getString("url")

            if (method == "GET") {
                try {

                    val response = HttpTemplateImpl().get(url).execute()

                    val content: TableType = when (response) {
                        is SuccessHttpResponse -> {
                            val el = response.result(JsonElement::class)
                            println(el)
                            el.toTable().also { println(it) }
                        }

                        is ErrorHttpResponse -> {
                            val bytes = response.inputStream.readAllBytes()
                            println(String(bytes))
                            val el = Json { }.decodeFromString(JsonElement.serializer(), String(bytes))
//                            val el = response.error(JsonElement::class)
                            println(el)
                            MapType()
                        }

                        else -> {
                            MapType()
                        }
                    }

                    results.add(MapType().also {
                        it["status_code"] = NumberType(response.statusCode.value)
                        it["content_type"] = StringType(response.headers["Content-Type"].split(";")[0])
                        it["content_length"] = NumberType(response.headers["Content-Length", "0"].toInt())
                        it["content"] = content
                    })

                } catch (t: Throwable) {
                    // FIXME
                    t.printStackTrace()
                }
            }

            if (method == "POST") {
                try {
                    val response = HttpTemplateImpl().post(url)
                        .execute()

                    results.add(MapType().also {
                        it["status_code"] = NumberType(response.statusCode.value)
                    })
                } catch (t: Throwable) {
                    // FIXME
                }
            }


            if (method == "PATCH") {
                try {
                    val response = HttpTemplateImpl().patch(url).execute()
                    results.add(MapType().also {
                        it["status_code"] = NumberType(response.statusCode.value)
                    })
                } catch (t: Throwable) {
                    // FIXME
                }
            }

            if (method == "PUT") {
                try {
                    val response = HttpTemplateImpl().put(url).execute()
                    results.add(MapType().also {
                        it["status_code"] = NumberType(response.statusCode.value)
                    })
                } catch (t: Throwable) {
                    // FIXME
                }
            }

            if (method == "DELETE") {
                try {
                    val response = HttpTemplateImpl().delete(url).execute()

                    results.add(MapType().also {
                        it["status_code"] = NumberType(response.statusCode.value)
                    })
                } catch (t: Throwable) {
                    // FIXME
                }
            }

        }

        return null to ArrayType(results.mapIndexed { index, value -> index + 1 to value }.toMap().toMutableMap())
    }
}