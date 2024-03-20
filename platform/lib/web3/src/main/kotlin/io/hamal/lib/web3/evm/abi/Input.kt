package io.hamal.lib.web3.evm.abi

import io.hamal.lib.web3.evm.abi.type.EvmType
import kotlin.reflect.KClass


data class EvmInput<VALUE_TYPE : EvmType<*>>(
    val name: String,
    val clazz: KClass<VALUE_TYPE>,
    val decoder: EvmTypeDecoder<VALUE_TYPE>,
) {

    companion object {
        fun Tuple0() = EthInputTuple0
    }

}

sealed interface EthInputTuple {
    val signature: String

}

object EthInputTuple0 : EthInputTuple {
    override val signature = "()"
}


