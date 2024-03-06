package io.hamal.plugin.net.http.function

import io.hamal.lib.http.HttpResponse
import io.hamal.lib.http.HttpSuccessResponse
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.http.get
import io.hamal.lib.kua.absIndex
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.topPop
import io.hamal.lib.kua.type.*


class HttpExecuteFunction : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        val results = mutableListOf<KuaReference>()

        ctx.nilPush()
        while (ctx.tableNext(arg1.index).booleanValue) {
            val requestIndex = ctx.numberGet(ctx.absIndex(-2))
            val request = ctx.tableGet(ctx.absIndex(-1))

            ctx.checkpoint {
//            val startStackSize = ctx.topGet()

                val url = request.getString("url")
                val method = request.getString("method")

                val headers = request.getTable("headers")

                val template = HttpTemplateImpl()
                    .get(url)
                    .header("accept", "application/json")

                headers.asEntries().forEach { (key, value) ->
//                println("$key $value")
                    template.header(
                        key.stringValue, when (value) {
                            is KuaString -> value.stringValue
                            is KuaFalse -> "false"
                            is KuaTrue -> "true"
                            is KuaCode -> value.stringValue
                            is KuaDecimal -> value.toString()
                            is KuaError -> value.value
                            is KuaNil -> ""
                            is KuaNumber -> value.doubleValue.toString()
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


//            ctx.nilPush()
//            while (ctx.tableNext(request.index).booleanValue) {
//                val key = ctx.stringGet(ctx.absIndex(-2))
//                val value = ctx.get(ctx.absIndex(-1))
//                println(key)
//                println(value)
//                ctx.topPop(1)
//            }
//
//            ctx.topPop(1)
        }


        return null to ctx.tableCreate(results.size, 0).also { table ->
            results.forEach { reference ->
                ctx.referencePush(reference)
                ctx.tableAppend(table.index)
            }
        }


//        arg1.forEach { request ->
//            require(request is KuaTable)
//            println(request)
//            request.forEach { key, value ->
//                println(key)
//                println(value)
//            }
//        }

//        ctx.pushNil()
//        while (ctx.native.tableNext(arg1.index)) {
//            val requestIndex = ctx.getNumberType(ctx.absIndex(-2))
//            val request = ctx.getTableMap(ctx.absIndex(-1))
//            println(requestIndex)
//            println(request)
//
//            ctx.pushNil()
//            while (ctx.native.tableNext(request.index)) {
//                val key = ctx.getString(ctx.absIndex(-2))
//                val value = ctx.getAny(ctx.absIndex(-1))
//                println(key)
//                println(value)
//                ctx.native.pop(1)
//            }
//
//            ctx.native.pop(1)
//        }

//        KuaTableEntryIterator(
//            index = arg1.index,
//            state = ctx,
//            keyExtractor = { state, index -> state.getNumberType(index) },
//            valueExtractor = { state, index -> state.getTableMap(index) }
//        ).asSequence().forEach { (_, request) ->
//            println(ctx.getStringType(request.index))
////            println(request.getString("method"))
////            println(request)
//        }


//        for (idx in 0 until arg1.length) {
//            val map = arg1.getTableMap(idx + 1)
//
//
//
//            val method = map.getString("method")
//            val url = map.getString("url")
//
//            val headers = map.getTableMap("headers")
//            if (method == "GET") {
//                val template = HttpTemplateImpl().get(url).header("accept", "application/json")
//
//                headers.asSequence().forEach { (key, value) ->
//                    template.header(
//                        key.value, when (value) {
//                            is KuaString -> value.value
//                            is KuaFalse -> "false"
//                            is KuaTrue -> "true"
//                            is KuaCode -> value.value
//                            is KuaDecimal -> value.toString()
//                            is KuaError -> value.value
//                            is KuaNil -> ""
//                            is KuaNumber -> value.value.toString()
//                            is KuaAny -> TODO()
//                            is KuaTable -> TODO()
//                            is KuaTable -> TODO()
//                            is KuaTable -> throw IllegalArgumentException("MapType not supported")
//                            is KuaFunction<*, *, *, *> -> TODO()
//                            else -> TODO()
//                        }
//                    )
//                }
//
//
//                val response = template.execute()
//                results.add(response.toMap(ctx))
//            }
//
//
//
//            if (method == "POST") {
//
//                val json = map.get("json")
//
//                val template = HttpTemplateImpl().post(url)
//
//                template.header("accept", "application/json")
//                template.header("content-type", "application/json")
//
//                // FIXME
////                if (json !is KuaNil) {
////                    template.body(json.toJson())
////                }
//
//                headers.asSequence().forEach { (key, value) ->
//                    template.header(
//                        key.value, when (value) {
//                            is KuaString -> value.value
//                            is KuaFalse -> "false"
//                            is KuaTrue -> "true"
//                            is KuaCode -> value.value
//                            is KuaDecimal -> value.toString()
//                            is KuaError -> value.value
//                            is KuaNil -> ""
//                            is KuaNumber -> value.value.toString()
//                            is KuaAny -> TODO()
//                            is KuaTable -> TODO()
//                            is KuaTable -> TODO()
//                            is KuaTable -> throw IllegalArgumentException("MapType not supported")
//                            is KuaFunction<*, *, *, *> -> TODO()
//                            else -> TODO()
//                        }
//                    )
//                }
//
//
//                val response = template.execute()
//                results.add(response.toMap(ctx))
//            }

//
//            if (method == "PATCH") {
//                val json = map.get("json")
//
//                val template = HttpTemplateImpl().patch(url)
//
//                template.header("accept", "application/json")
//                template.header("content-type", "application/json")
//
//                // FIXME
//                if (json !is KuaNil) {
//                    template.body(json.toJson())
//                }
//
//                if (headers is KuaTable) {
//                    headers.value.forEach { key, value ->
//                        template.header(
//                            key,
//                            when (value) {
//                                is KuaString -> value.value
//                                is KuaFalse -> "false"
//                                is KuaTrue -> "true"
//                                is KuaCode -> value.value
//                                is KuaDecimal -> value.toString()
//                                is KuaError -> value.value
//                                is KuaNil -> ""
//                                is KuaNumber -> value.value.toString()
//                                is KuaAny -> TODO()
//                                is KuaTable -> TODO()
//                                is KuaTable -> TODO()
//                                is KuaFunction<*, *, *, *> -> TODO()
//                                else -> TODO()
//                            }
//                        )
//                    }
//                }
//
//                val response = template.execute()
//                results.add(response.toMap())
//            }
//
//            if (method == "PUT") {
//                val json = map.get("json")
//
//                val template = HttpTemplateImpl().put(url)
//
//                template.header("accept", "application/json")
//                template.header("content-type", "application/json")
//
//                if (headers is KuaTable) {
//                    headers.value.forEach { key, value ->
//                        template.header(
//                            key,
//                            when (value) {
//                                is KuaString -> value.value
//                                is KuaFalse -> "false"
//                                is KuaTrue -> "true"
//                                is KuaCode -> value.value
//                                is KuaDecimal -> value.toString()
//                                is KuaError -> value.value
//                                is KuaNil -> ""
//                                is KuaNumber -> value.value.toString()
//                                is KuaAny -> TODO()
//                                is KuaTable -> TODO()
//                                is KuaTable -> TODO()
//                                is KuaFunction<*, *, *, *> -> TODO()
//                                is KuaTable -> throw IllegalArgumentException("MapType not supported")
//                                else -> TODO()
//                            }
//                        )
//                    }
//                }
//
//                // FIXME
//                if (json !is KuaNil) {
//                    template.body(json.toJson())
//                }
//
//                val response = template.execute()
//                results.add(response.toMap())
//            }
//
//            if (method == "DELETE") {
//                val template = HttpTemplateImpl().delete(url).header("accept", "application/json")
//
//                if (headers is KuaTable) {
//                    headers.value.forEach { key, value ->
//                        template.header(
//                            key,
//                            when (value) {
//                                is KuaString -> value.value
//                                is KuaFalse -> "false"
//                                is KuaTrue -> "true"
//                                is KuaCode -> value.value
//                                is KuaDecimal -> value.toString()
//                                is KuaError -> value.value
//                                is KuaNil -> ""
//                                is KuaNumber -> value.value.toString()
//                                is KuaAny -> TODO()
//                                is KuaTable -> TODO()
//                                is KuaTable -> TODO()
//                                is KuaFunction<*, *, *, *> -> TODO()
//                                is KuaTable -> throw IllegalArgumentException("MapType not supported")
//                                else -> TODO()
//                            }
//                        )
//                    }
//                }
//
//                val response = template.execute()
//                results.add(response.toMap())
//            }
//
//        }

//            return null to KuaArray(results.mapIndexed { index, value -> index + 1 to value }.toMap().toMukuaTableMap())
//        return null to ctx.tableCreate(results)
    }
}

private fun HttpResponse.toMap(ctx: FunctionContext): KuaReference {

    ctx.tableCreate().also { response ->
        ctx.checkpoint {
            response["status_code"] = KuaNumber(statusCode.value)
            response["content_type"] = headers.find("content-type")?.let { type -> KuaString(type) } ?: KuaNil
            response["content_length"] = headers.find("content-length")
                ?.let { length -> KuaNumber(length.toInt()) }
                ?: KuaNil
            response["headers"] = headers(ctx)
            response["content"] = content(ctx)
        }

    }
    return ctx.referenceAcquire()
}

private fun HttpResponse.content(ctx: FunctionContext) = when (this) {
    is HttpSuccessResponse -> {
////        if (isNotEmpty) {
////            val el = result(JsonElement::class)
////            el.convertToType()
////        } else {
////            ctx.toMap()
////        }
        ctx.tableCreate()
    }

//
//    is HttpErrorResponse -> {
////        if (isNotEmpty) {
////            val el = error(JsonElement::class)
////            el.convertToType()
////        } else {
////            ctx.toMap()
////        }
//    }
//
    else -> KuaNil
}


private fun HttpResponse.headers(ctx: FunctionContext) = ctx.tableCreate(
    headers.map {
        it.key.lowercase() to KuaString(it.value)
    }.toMap()
)