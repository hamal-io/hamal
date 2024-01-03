package io.hamal.plugin.std.sys.flow

import io.hamal.lib.domain.vo.FlowInputs
import io.hamal.lib.domain.vo.FlowName
import io.hamal.lib.domain.vo.FlowType
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.ApiSdk
import io.hamal.lib.sdk.api.ApiFlowCreateRequest

class FlowCreateFunction(
    private val sdk: ApiSdk
) : Function1In2Out<MapType, ErrorType, MapType>(
    FunctionInput1Schema(MapType::class),
    FunctionOutput2Schema(ErrorType::class, MapType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: MapType): Pair<ErrorType?, MapType?> {

        val type = if (arg1.findString("type") != null) {
            FlowType(arg1.getString("type"))
        } else {
            null
        }

        return try {
            val res = sdk.flow.create(
                ctx[GroupId::class],
                ApiFlowCreateRequest(
                    name = FlowName(arg1.getString("name")),
                    inputs = FlowInputs(),
                    type = type
                )
            )

            null to MapType(
                mutableMapOf(
                    "id" to StringType(res.id.value.value.toString(16)),
                    "status" to StringType(res.status.name),
                    "flow_id" to StringType(res.flowId.value.value.toString(16))
                )
            )

        } catch (t: Throwable) {
            ErrorType(t.message!!) to null
        }
    }
}