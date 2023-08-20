package io.hamal.extension.std.sys.func

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.req.CreateFuncReq
import io.hamal.lib.domain.vo.FuncInputs
import io.hamal.lib.domain.vo.FuncName
import io.hamal.lib.domain.vo.NamespaceId
import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.http.body
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.domain.ApiSubmittedReqWithId

class CreateFuncFunction(
    private val templateSupplier: () -> HttpTemplate
) : Function1In2Out<TableProxyMap, ErrorType, TableProxyMap>(
    FunctionInput1Schema(TableProxyMap::class),
    FunctionOutput2Schema(ErrorType::class, TableProxyMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableProxyMap): Pair<ErrorType?, TableProxyMap> {
        try {
            val namespaceId = if (arg1.type("namespace_id") == StringType::class) {
                NamespaceId(SnowflakeId(arg1.getString("namespace_id")))
            } else {
                null
            }

            val r = CreateFuncReq(
                namespaceId = namespaceId,
                name = FuncName(arg1.getString("name")),
                inputs = FuncInputs(),
                code = arg1.getCode("code")
            )

            val res = templateSupplier()
                .post("/v1/funcs")
                .body(r)
                .execute(ApiSubmittedReqWithId::class)

            return null to ctx.tableCreateMap(1).also {
                it["req_id"] = res.reqId
                it["status"] = res.status.name
                it["id"] = res.id
            }

        } catch (t: Throwable) {
            t.printStackTrace()
            throw t
        }
    }
}