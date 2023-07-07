package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.eth.abi.type.EthType
import kotlin.reflect.KClass

data class EthInput<VALUE_TYPE : EthType<*>>(
    val name: String,
    val clazz: KClass<VALUE_TYPE>,
    val decoder: EthTypeDecoder<VALUE_TYPE>,
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


