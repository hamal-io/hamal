package io.hamal.extension.std.sys.code

import io.hamal.lib.domain.vo.CodeId
import io.hamal.lib.domain.vo.CodeValue
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk

class CodeGetFunction(
    private val sdk: ApiSdk
) : Function1In2Out<StringType, ErrorType, MapType>(
    FunctionInput1Schema(StringType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: StringType): Pair<ErrorType?, MapType?> {
        TODO("Not yet implemented")
        /* return try {
           null to sdk.code.get(CodeId(arg1.value))
               .let { code ->
                   MapType(
                       mutableMapOf(
                           "id" to StringType(code.id.value.value.toString(16)),
                           "code" to CodeValue(code.value.value)
                       )
                   )
               }
       } catch (t: Throwable) {
           ErrorType(t.message!!) to null
       }*/
    }
}