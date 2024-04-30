package io.hamal.plugin.net.http.function

import io.hamal.lib.common.hot.HotNode
import io.hamal.lib.http.*
import io.hamal.lib.kua.absIndex
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.topPop
import io.hamal.lib.kua.type.*
import io.hamal.lib.value.*


class HttpExecuteFunction : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        val results = mutableListOf<KuaReference>()

        ctx.nilPush()
        while (ctx.tableNext(arg1.index).booleanValue) {
            val request = ctx.tableGet(ctx.absIndex(-1))

            ctx.checkpoint {
                val url = request.getString("url")
                val headers = request.getTable("headers")

                val method = request.getString("method")

                val json = request.get("json")

                val template = when (method) {
                    ValueString("GET") -> HttpTemplateImpl().get(url.stringValue)
                    ValueString("PATCH") -> HttpTemplateImpl().patch(url.stringValue)
                    ValueString("POST") -> HttpTemplateImpl().post(url.stringValue)
                    ValueString("PUT") -> HttpTemplateImpl().put(url.stringValue)
                    ValueString("DELETE") -> HttpTemplateImpl().delete(url.stringValue)
                    else -> TODO()
                }

                if (json !is ValueNil) {
                    if (template is HttpRequestWithBody) {
                        ctx.checkpoint { template.body(json.toHotNode()) }
                    }
                }

                template.header("accept", "application/json")

                headers.asEntries().forEach { (key, value) ->
//                println("$key $value")
                    template.header(
                        key.stringValue, when (value) {
                            is ValueString -> value.stringValue
                            is ValueFalse -> "false"
                            is ValueTrue -> "true"
                            is KuaCode -> value.stringValue
                            is ValueDecimal -> value.toString()
                            is KuaError -> value.value
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
            result(HotNode::class).toKua(ctx)
        } else {
            ctx.tableCreate()
        }
    }

    is HttpErrorResponse -> {
        if (isNotEmpty) {
            error(HotNode::class).toKua(ctx)
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