package io.hamal.extension.unsafe.net.http.function

import io.hamal.extension.unsafe.net.http.converter.convertToType
import io.hamal.extension.unsafe.net.http.converter.toJson
import io.hamal.lib.http.*
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import kotlinx.serialization.json.JsonElement


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

            val headers = map.get("headers")
            if (method == "GET") {
                val template = HttpTemplateImpl().get(url).header("accept", "application/json")

                if (headers is MapType) {
                    headers.value.forEach { key, value ->
                        template.header(
                            key,
                            when (value) {
                                is StringType -> value.value
                                is False -> "false"
                                is True -> "true"
                                is CodeType -> value.value
                                is DecimalType -> value.toString()
                                is ErrorType -> value.value
                                is NilType -> ""
                                is NumberType -> value.value.toString()
                                is AnySerializableType -> TODO()
                                is ArrayType -> TODO()
                                is MapType -> throw IllegalArgumentException("MapType not supported")
                            }
                        )
                    }
                }


                val response = template.execute()
                results.add(response.toMap())
            }

            if (method == "POST") {

                val json = map.get("json")

                val template = HttpTemplateImpl().post(url)

                template.header("accept", "application/json")
                template.header("content-type", "application/json")

                // FIXME
                if (json !is NilType) {
                    template.body(json.toJson())
                }

                if (headers is MapType) {
                    headers.value.forEach { key, value ->
                        template.header(
                            key,
                            when (value) {
                                is StringType -> value.value
                                is False -> "false"
                                is True -> "true"
                                is CodeType -> value.value
                                is DecimalType -> value.toString()
                                is ErrorType -> value.value
                                is NilType -> ""
                                is NumberType -> value.value.toString()
                                is AnySerializableType -> TODO()
                                is ArrayType -> TODO()
                                is MapType -> throw IllegalArgumentException("MapType not supported")
                            }
                        )
                    }
                }

                val response = template.execute()
                results.add(response.toMap())
            }


            if (method == "PATCH") {
                val json = map.get("json")

                val template = HttpTemplateImpl().patch(url)

                template.header("accept", "application/json")
                template.header("content-type", "application/json")

                // FIXME
                if (json !is NilType) {

                    template.body(json.toJson())
                }

                if (headers is MapType) {
                    headers.value.forEach { key, value ->
                        template.header(
                            key,
                            when (value) {
                                is StringType -> value.value
                                is False -> "false"
                                is True -> "true"
                                is CodeType -> value.value
                                is DecimalType -> value.toString()
                                is ErrorType -> value.value
                                is NilType -> ""
                                is NumberType -> value.value.toString()
                                is AnySerializableType -> TODO()
                                is ArrayType -> TODO()
                                is MapType -> throw IllegalArgumentException("MapType not supported")
                            }
                        )
                    }
                }

                val response = template.execute()
                results.add(response.toMap())
            }

            if (method == "PUT") {
                val json = map.get("json")

                val template = HttpTemplateImpl().put(url)

                template.header("accept", "application/json")
                template.header("content-type", "application/json")

                if (headers is MapType) {
                    headers.value.forEach { key, value ->
                        template.header(
                            key,
                            when (value) {
                                is StringType -> value.value
                                is False -> "false"
                                is True -> "true"
                                is CodeType -> value.value
                                is DecimalType -> value.toString()
                                is ErrorType -> value.value
                                is NilType -> ""
                                is NumberType -> value.value.toString()
                                is AnySerializableType -> TODO()
                                is ArrayType -> TODO()
                                is MapType -> throw IllegalArgumentException("MapType not supported")
                            }
                        )
                    }
                }

                // FIXME
                if (json !is NilType) {

                    template.body(json.toJson())
                }

                val response = template.execute()
                results.add(response.toMap())
            }

            if (method == "DELETE") {
                val template = HttpTemplateImpl().delete(url).header("accept", "application/json")

                if (headers is MapType) {
                    headers.value.forEach { key, value ->
                        template.header(
                            key,
                            when (value) {
                                is StringType -> value.value
                                is False -> "false"
                                is True -> "true"
                                is CodeType -> value.value
                                is DecimalType -> value.toString()
                                is ErrorType -> value.value
                                is NilType -> ""
                                is NumberType -> value.value.toString()
                                is AnySerializableType -> TODO()
                                is ArrayType -> TODO()
                                is MapType -> throw IllegalArgumentException("MapType not supported")
                            }
                        )
                    }
                }

                val response = template.execute()
                results.add(response.toMap())
            }

        }

        return null to ArrayType(results.mapIndexed { index, value -> index + 1 to value }.toMap().toMutableMap())
    }
}


private fun HttpResponse.toMap() = MapType().also {
    it["status_code"] = NumberType(statusCode.value)
    it["content_type"] = headers.find("content-type")?.let { type -> StringType(type) } ?: NilType
    it["content_length"] = headers.find("content-length")?.let { length -> NumberType(length.toInt()) } ?: NilType
    it["headers"] = headers()
    it["content"] = content()
}

private fun HttpResponse.content() = when (this) {
    is SuccessHttpResponse -> {
        if (isNotEmpty) {
            val el = result(JsonElement::class)
            el.convertToType()
        } else {
            MapType()
        }
    }

    is ErrorHttpResponse -> {
        if (isNotEmpty) {
            val el = error(JsonElement::class)
            el.convertToType()
        } else {
            MapType()
        }
    }

    else -> NilType
}


private fun HttpResponse.headers() = MapType(
    headers.map {
        it.key.lowercase() to StringType(it.value)
    }.toMap().toMutableMap()
)