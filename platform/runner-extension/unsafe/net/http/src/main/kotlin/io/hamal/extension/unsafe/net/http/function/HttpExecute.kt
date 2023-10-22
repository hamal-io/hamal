package io.hamal.extension.unsafe.net.http.function

import io.hamal.extension.unsafe.net.http.converter.convertToType
import io.hamal.lib.http.ErrorHttpResponse
import io.hamal.lib.http.HttpResponse
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.SuccessHttpResponse
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

            if (method == "GET") {
                val response = HttpTemplateImpl().get(url).execute()
                results.add(response.toMap())
            }

            if (method == "POST") {
                val response = HttpTemplateImpl().post(url).execute()
                results.add(response.toMap())
            }


            if (method == "PATCH") {
                val response = HttpTemplateImpl().patch(url).execute()
                results.add(response.toMap())
            }

            if (method == "PUT") {
                val response = HttpTemplateImpl().put(url).execute()
                results.add(response.toMap())
            }

            if (method == "DELETE") {
                val response = HttpTemplateImpl().delete(url).execute()
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
