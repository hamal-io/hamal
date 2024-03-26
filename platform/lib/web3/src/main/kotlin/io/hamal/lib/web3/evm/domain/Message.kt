package io.hamal.lib.web3.evm.domain

import io.hamal.lib.web3.evm.EvmSignature
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import org.web3j.crypto.Keys
import org.web3j.crypto.Sign

data class EvmSignedMessage(
    val data: ByteArray,
    val signature: EvmSignature
) {
    val address by lazy {
        val pubKey = Sign.signedPrefixedMessageToKey(data, signature.value)
        EvmAddress(EvmPrefixedHexString("0x${Keys.getAddress(pubKey)}"))
    }
}