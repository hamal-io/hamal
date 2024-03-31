package io.hamal.lib.web3.evm.abi

import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString

data class EvmFunction<INPUTS : EvmInputTuple, OUTPUTS : EvmOutputTuple>(
    val name: String,
    val inputs: INPUTS,
    val outputs: OUTPUTS
) {
    val signature = EvmSignature(name + inputs.signature)
    fun decode(data: EvmPrefixedHexString) = outputs.decode(data)
    fun decodeToMap(data: EvmPrefixedHexString) = outputs.decodeToMap(data)
}