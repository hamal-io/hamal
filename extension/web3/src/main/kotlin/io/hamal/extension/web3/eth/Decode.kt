package io.hamal.extension.web3.eth

import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.value.AnyValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.web3.eth.abi.EthTypeDecoder
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.util.ByteWindow

object DecodeParameterFunction : Function2In2Out<StringValue, StringValue, ErrorValue, AnyValue>(
    FunctionInput2Schema(StringValue::class, StringValue::class),
    FunctionOutput2Schema(ErrorValue::class, AnyValue::class)
) {
    override fun invoke(ctx: FunctionContext, type: StringValue, value: StringValue): Pair<ErrorValue?, AnyValue?> {
//        EthTypeDecoder.Uint256.decode(arg1.value.)

        if (type.value == "string") {
            return null to AnyValue(
                //FIXME must be decimal value
                StringValue(
                    EthTypeDecoder.String.decode(
                        ByteWindow.Companion.of(
                            EthPrefixedHexString(value.value)
                        )
                    ).value
                )
            )
        }

        if (type.value == "uint256") {
            return null to AnyValue(
                //FIXME must be decimal value
                NumberValue(
                    EthTypeDecoder.Uint256.decode(
                        ByteWindow.Companion.of(
                            EthPrefixedHexString(value.value)
                        )
                    ).value.toDouble()
                )
            )
        }

        return null to null
    }
}