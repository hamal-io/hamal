package io.hamal.plugin.net.http.function

import com.google.gson.JsonElement
import io.hamal.lib.http.*
import io.hamal.lib.kua.converter.convertToType
import io.hamal.lib.kua.converter.toJson
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*


class HttpExecuteFunction : Function1In2Out<KuaArray, KuaError, KuaTableType>(
    FunctionInput1Schema(KuaArray::class),
    FunctionOutput2Schema(KuaError::class, KuaTableType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaArray): Pair<KuaError?, KuaTableType?> {
        val results = mutableListOf<KuaMap>()
        for (idx in 0 until arg1.size) {
            val map = arg1.getMap(idx + 1)

            val method = map.getString("method")
            val url = map.getString("url")

            val headers = map.get("headers")
            if (method == "GET") {
                val template = HttpTemplateImpl().get(url).header("accept", "application/json")

                if (headers is KuaMap) {
                    headers.value.forEach { key, value ->
                        template.header(
                            key,
                            when (value) {
                                is KuaString -> value.value
                                is KuaFalse -> "false"
                                is KuaTrue -> "true"
                                is KuaCode -> value.value
                                is KuaDecimal -> value.toString()
                                is KuaError -> value.value
                                is KuaNil -> ""
                                is KuaNumber -> value.value.toString()
                                is KuaAny -> TODO()
                                is KuaArray -> TODO()
                                is KuaMap -> throw IllegalArgumentException("MapType not supported")
                                is KuaFunction<*, *, *, *> -> TODO()
                                is KuaTableType -> TODO()
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
                if (json !is KuaNil) {
                    template.body(json.toJson())
                }

                if (headers is KuaMap) {
                    headers.value.forEach { key, value ->
                        template.header(
                            key,
                            when (value) {
                                is KuaString -> value.value
                                is KuaFalse -> "false"
                                is KuaTrue -> "true"
                                is KuaCode -> value.value
                                is KuaDecimal -> value.toString()
                                is KuaError -> value.value
                                is KuaNil -> ""
                                is KuaNumber -> value.value.toString()
                                is KuaAny -> TODO()
                                is KuaArray -> TODO()
                                is KuaFunction<*, *, *, *> -> TODO()
                                is KuaTableType -> TODO()
                                is KuaMap -> throw IllegalArgumentException("MapType not supported")
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
                if (json !is KuaNil) {
                    template.body(json.toJson())
                }

                if (headers is KuaMap) {
                    headers.value.forEach { key, value ->
                        template.header(
                            key,
                            when (value) {
                                is KuaString -> value.value
                                is KuaFalse -> "false"
                                is KuaTrue -> "true"
                                is KuaCode -> value.value
                                is KuaDecimal -> value.toString()
                                is KuaError -> value.value
                                is KuaNil -> ""
                                is KuaNumber -> value.value.toString()
                                is KuaAny -> TODO()
                                is KuaArray -> TODO()
                                is KuaFunction<*, *, *, *> -> TODO()
                                is KuaTableType -> TODO()
                                is KuaMap -> throw IllegalArgumentException("MapType not supported")
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

                if (headers is KuaMap) {
                    headers.value.forEach { key, value ->
                        template.header(
                            key,
                            when (value) {
                                is KuaString -> value.value
                                is KuaFalse -> "false"
                                is KuaTrue -> "true"
                                is KuaCode -> value.value
                                is KuaDecimal -> value.toString()
                                is KuaError -> value.value
                                is KuaNil -> ""
                                is KuaNumber -> value.value.toString()
                                is KuaAny -> TODO()
                                is KuaArray -> TODO()
                                is KuaFunction<*, *, *, *> -> TODO()
                                is KuaTableType -> TODO()
                                is KuaMap -> throw IllegalArgumentException("MapType not supported")
                            }
                        )
                    }
                }

                // FIXME
                if (json !is KuaNil) {
                    template.body(json.toJson())
                }

                val response = template.execute()
                results.add(response.toMap())
            }

            if (method == "DELETE") {
                val template = HttpTemplateImpl().delete(url).header("accept", "application/json")

                if (headers is KuaMap) {
                    headers.value.forEach { key, value ->
                        template.header(
                            key,
                            when (value) {
                                is KuaString -> value.value
                                is KuaFalse -> "false"
                                is KuaTrue -> "true"
                                is KuaCode -> value.value
                                is KuaDecimal -> value.toString()
                                is KuaError -> value.value
                                is KuaNil -> ""
                                is KuaNumber -> value.value.toString()
                                is KuaAny -> TODO()
                                is KuaArray -> TODO()
                                is KuaFunction<*, *, *, *> -> TODO()
                                is KuaTableType -> TODO()
                                is KuaMap -> throw IllegalArgumentException("MapType not supported")
                            }
                        )
                    }
                }

                val response = template.execute()
                results.add(response.toMap())
            }

        }

        return null to KuaArray(results.mapIndexed { index, value -> index + 1 to value }.toMap().toMutableMap())
    }
}


private fun HttpResponse.toMap() = KuaMap().also {
    it["status_code"] = KuaNumber(statusCode.value)
    it["content_type"] = headers.find("content-type")?.let { type -> KuaString(type) } ?: KuaNil
    it["content_length"] = headers.find("content-length")?.let { length -> KuaNumber(length.toInt()) } ?: KuaNil
    it["headers"] = headers()
    it["content"] = content()
}

private fun HttpResponse.content() = when (this) {
    is HttpSuccessResponse -> {
        if (isNotEmpty) {
            val el = result(JsonElement::class)
            el.convertToType()
        } else {
            KuaMap()
        }
    }

    is HttpErrorResponse -> {
        if (isNotEmpty) {
            val el = error(JsonElement::class)
            el.convertToType()
        } else {
            KuaMap()
        }
    }

    else -> KuaNil
}


private fun HttpResponse.headers() = KuaMap(
    headers.map {
        it.key.lowercase() to KuaString(it.value)
    }.toMap().toMutableMap()
)