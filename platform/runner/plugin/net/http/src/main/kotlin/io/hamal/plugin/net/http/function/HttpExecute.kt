package io.hamal.plugin.net.http.function

import io.hamal.lib.common.serialization.json.JsonNode
import io.hamal.lib.common.value.*
import io.hamal.lib.http.*
import io.hamal.lib.http.serde.*
import io.hamal.lib.kua.absIndex
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.topPop
import io.hamal.lib.kua.value.*


class HttpExecuteFunction : Function1In2Out<KuaTable, ValueError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(ValueError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<ValueError?, KuaTable?> {
        val results = mutableListOf<KuaReference>()

        ctx.nilPush()
        while (ctx.tableNext(arg1.index).booleanValue) {
            val request = ctx.tableGet(ctx.absIndex(-1))

            ctx.checkpoint {
                val url = request.getString("url")
                val headers = request.getTable("headers")

                val method = request.getString("method")


                val produces = request.getString("produces")
                val consumes = request.getString("consumes")
                val consumesError = request.getString("consumes_error")

                val baseTemplate = HttpTemplateImpl(
                    serdeFactory = object : HttpSerdeFactory {
                        override var contentSerializer: HttpContentSerializer = when (produces) {
                            ValueString("JSON") -> HttpContentJsonSerializer
                            ValueString("HON") -> HttpContentHonSerializer
                            else -> TODO()
                        }
                        override var contentDeserializer: HttpContentDeserializer = when (consumes) {
                            ValueString("JSON") -> HttpContentJsonDeserializer
                            ValueString("HON") -> HttpContentHonDeserializer
                            else -> TODO()
                        }
                        override var errorDeserializer: HttpErrorDeserializer = when (consumesError) {
                            ValueString("JSON") -> HttpErrorJsonDeserializer
                            ValueString("HON") -> HttpErrorHonDeserializer
                            else -> TODO()
                        }
                    }
                )

                val body = request.get("body")

                val template = when (method) {
                    ValueString("GET") -> baseTemplate.get(url.stringValue)
                    ValueString("PATCH") -> baseTemplate.patch(url.stringValue)
                    ValueString("POST") -> baseTemplate.post(url.stringValue)
                    ValueString("PUT") -> baseTemplate.put(url.stringValue)
                    ValueString("DELETE") -> baseTemplate.delete(url.stringValue)
                    else -> TODO()
                }

                if (body !is ValueNil) {
                    if (template is HttpRequestWithBody) {
                        ctx.checkpoint { template.body(body.toHotNode()) }
                    }
                }

                when (produces) {
                    ValueString("JSON") -> template.header("content-type", "application/json;charset=UTF-8")
                    ValueString("HON") -> template.header("content-type", "application/hon;charset=UTF-8")
                }

                when (consumes) {
                    ValueString("JSON") -> template.header("accept", "application/json;charset=UTF-8")
                    ValueString("HON") -> template.header("accept", "application/hon;charset=UTF-8")
                }

                headers.asEntries().forEach { (key, value) ->
                    template.header(
                        key.stringValue, when (value) {
                            is ValueString -> value.stringValue
                            is ValueBoolean -> value.booleanValue.toString()
                            is ValueCode -> value.stringValue
                            is ValueDecimal -> value.toString()
                            is ValueError -> value.stringValue
                            is ValueNil -> ""
                            is ValueNumber -> value.doubleValue.toString()
                            is KuaTable -> TODO()
                            is KuaFunction<*, *, *, *> -> TODO()
                            else -> TODO()
                        }
                    )
                }

                val response = template.execute()
                results.add(response.toMap(ctx))
            }

            ctx.topPop(1)
        }


        return null to ctx.tableCreate(results.size, 0).also { table ->
            results.forEach { reference ->
                ctx.referencePush(reference)
                ctx.tableAppend(table.index)
            }
        }
    }
}

private fun HttpResponse.toMap(ctx: FunctionContext): KuaReference {

    ctx.tableCreate().also { response ->
        ctx.checkpoint {
            response["status_code"] = ValueNumber(statusCode.value)
            response["content_type"] = headers.find("content-type")?.let { type -> ValueString(type) } ?: ValueNil
            response["content_length"] = headers.find("content-length")
                ?.let { length -> ValueNumber(length.toInt()) }
                ?: ValueNil
            response["headers"] = headers(ctx)
            response["content"] = content(ctx)
        }

    }
    return ctx.referenceAcquire()
}

private fun HttpResponse.content(ctx: FunctionContext) = when (this) {
    is HttpSuccessResponse -> {
        if (isNotEmpty) {
            val contentType = headers.find("content-type")
            if (contentType?.startsWith("application/hon") == true) {
                result(ValueArray::class)
            } else {
                result(JsonNode::class).toKua(ctx)
            }
        } else {
            ctx.tableCreate()
        }
    }

    is HttpErrorResponse -> {
        if (isNotEmpty) {
            error(JsonNode::class).toKua(ctx)
        } else {
            ctx.tableCreate()
        }
    }

    else -> ValueNil
}


private fun HttpResponse.headers(ctx: FunctionContext) = ctx.tableCreate(
    headers.map {
        it.key.lowercase() to ValueString(it.value)
    }.toMap()
)