package io.hamal.extension.net.http

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType

class HttpExecuteFunction : Function1In2Out<ArrayType, ErrorType, ArrayType>(
    FunctionInput1Schema(ArrayType::class),
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ArrayType): Pair<ErrorType?, ArrayType?> {
        for (idx in 0 until arg1.size) {
            val map = arg1.getMap(idx + 1)

            val method = map.getString("method")
            val url = map.getString("url")

            if (method == "GET") {
                try {
                    val response = HttpTemplate().get(url).execute()

                    return null to ArrayType().also {
                        it.append(MapType().also { map ->
                            map["statusCode"] = NumberType(response.statusCode.value)
                        })
                    }
                } catch (t: Throwable) {
                    // FIXME
                }
            }

            if (method == "POST") {
                try {
                    val response = HttpTemplate().post(url).execute()

                    return null to ArrayType().also {
                        it.append(MapType().also { map ->
                            map["statusCode"] = NumberType(response.statusCode.value)
                        })
                    }
                } catch (t: Throwable) {
                    // FIXME
                }
            }


            if (method == "PATCH") {
                try {
                    val response = HttpTemplate().patch(url).execute()

                    return null to ArrayType().also {
                        it.append(MapType().also { map ->
                            map["statusCode"] = NumberType(response.statusCode.value)
                        })
                    }
                } catch (t: Throwable) {
                    // FIXME
                }
            }

            if (method == "PUT") {
                try {
                    val response = HttpTemplate().put(url).execute()

                    return null to ArrayType().also {
                        it.append(MapType().also { map ->
                            map["statusCode"] = NumberType(response.statusCode.value)
                        })
                    }
                } catch (t: Throwable) {
                    // FIXME
                }
            }

            if (method == "DELETE") {
                try {
                    val response = HttpTemplate().delete(url).execute()

                    return null to ArrayType().also {
                        it.append(MapType().also { map ->
                            map["statusCode"] = NumberType(response.statusCode.value)
                        })
                    }
                } catch (t: Throwable) {
                    // FIXME
                }
            }

        }

        return null to null
    }

}