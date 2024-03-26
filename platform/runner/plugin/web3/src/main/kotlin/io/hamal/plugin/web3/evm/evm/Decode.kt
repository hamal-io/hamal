package io.hamal.plugin.web3.evm.evm

import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaType
import io.hamal.lib.web3.evm.abi.EvmTypeDecoder
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.util.ByteWindow

object EvmDecodeParameterFunction : Function2In2Out<KuaString, KuaString, KuaError, KuaType>(
    FunctionInput2Schema(KuaString::class, KuaString::class),
    FunctionOutput2Schema(KuaError::class, KuaType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaString, arg2: KuaString): Pair<KuaError?, KuaType?> {
//        EthTypeDecoder.Uint256.decode(arg1.value.)

        if (arg1.stringValue == "string") {
            return null to
                    //FIXME must be decimal value
                    KuaString(
                        EvmTypeDecoder.String.decode(
                            ByteWindow.Companion.of(
                                EvmPrefixedHexString(arg2.stringValue)
                            )
                        ).value
                    )
        }

        if (arg1.stringValue == "uint256") {
            return null to
                    //FIXME must be decimal value
                    KuaNumber(
                        EvmTypeDecoder.Uint256.decode(
                            ByteWindow.Companion.of(
                                EvmPrefixedHexString(arg2.stringValue)
                            )
                        ).value.toDouble()
                    )
        }

        return null to null
    }
}