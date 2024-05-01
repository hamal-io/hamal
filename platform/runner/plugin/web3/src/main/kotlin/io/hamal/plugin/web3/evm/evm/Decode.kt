package io.hamal.plugin.web3.evm.evm

import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.common.value.Value
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.web3.evm.abi.EvmTypeDecoder
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.util.ByteWindow

object EvmDecodeParameterFunction : Function2In2Out<ValueString, ValueString, ValueError, Value>(
    FunctionInput2Schema(ValueString::class, ValueString::class),
    FunctionOutput2Schema(ValueError::class, Value::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ValueString, arg2: ValueString): Pair<ValueError?, Value?> {
//        EthTypeDecoder.Uint256.decode(arg1.value.)

        if (arg1.stringValue == "string") {
            return null to
                    //FIXME must be decimal value
                    ValueString(
                        EvmTypeDecoder.String.decode(
                            ByteWindow.of(
                                EvmPrefixedHexString(arg2.stringValue)
                            )
                        ).value
                    )
        }

        if (arg1.stringValue == "uint256") {
            return null to
                    //FIXME must be decimal value
                    ValueNumber(
                        EvmTypeDecoder.Uint256.decode(
                            ByteWindow.of(
                                EvmPrefixedHexString(arg2.stringValue)
                            )
                        ).value.toDouble()
                    )
        }

        return null to null
    }
}