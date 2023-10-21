package io.hamal.capability.web3.evm

import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.AnyType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.web3.eth.abi.EthTypeDecoder
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.util.ByteWindow

object EthDecodeParameterFunction : Function2In2Out<StringType, StringType, ErrorType, AnyType>(
    FunctionInput2Schema(StringType::class, StringType::class),
    FunctionOutput2Schema(ErrorType::class, AnyType::class)
) {
    override fun invoke(ctx: FunctionContext, type: StringType, value: StringType): Pair<ErrorType?, AnyType?> {
//        EthTypeDecoder.Uint256.decode(arg1.value.)

        if (type.value == "string") {
            return null to AnyType(
                //FIXME must be decimal value
                StringType(
                    EthTypeDecoder.String.decode(
                        ByteWindow.Companion.of(
                            EthPrefixedHexString(value.value)
                        )
                    ).value
                )
            )
        }

        if (type.value == "uint256") {
            return null to AnyType(
                //FIXME must be decimal value
                NumberType(
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