package io.hamal.lib.web3.evm

import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import org.web3j.crypto.Sign

class EvmSignature(val value: Sign.SignatureData) {
    constructor(hexString: EvmPrefixedHexString) : this(Sign.signatureDataFromHex(hexString.value))
}