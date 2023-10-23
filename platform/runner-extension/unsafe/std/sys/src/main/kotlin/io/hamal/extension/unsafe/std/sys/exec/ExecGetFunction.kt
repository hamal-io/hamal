package io.hamal.extension.unsafe.std.sys.exec

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.*
import io.hamal.lib.sdk.ApiSdk

class ExecGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        return try {
            val exec = sdk.exec.get(ExecId(arg1.value))
            null to MapType().also {
                it["id"] = exec.id.value.value.toString(16)
                it["status"] = StringType(exec.status.name)
                it["inputs"] = MapType() // FIXME
                exec.correlation?.correlationId?.value?.let { corId ->
                    it["correlation_id"] = corId
                } // FIXME set nil value to table --> makes the api nicer
                it["code"] = exec.code.let { code ->
                    MapType(
                        mutableMapOf(
                            "id" to (code.id?.value?.value?.toString(16)?.let(::StringType) ?: NilType),
                            "version" to (code.version?.value?.let(::NumberType) ?: NilType),
                            "value" to (code.value?.value?.let(::CodeType) ?: NilType)
                        )
                    )
                }
            }
        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}