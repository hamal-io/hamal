package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString

data class EthFunction<INPUTS : EthInputTuple, OUTPUTS : EthOutputTuple>(
    val name: String,
    val inputs: INPUTS,
    val outputs: OUTPUTS
) {
    val signature = EthSignature(name + inputs.signature)
    fun decode(data: EthPrefixedHexString) = outputs.decode(data)
    fun decodeToMap(data: EthPrefixedHexString) = outputs.decodeToMap(data)
}