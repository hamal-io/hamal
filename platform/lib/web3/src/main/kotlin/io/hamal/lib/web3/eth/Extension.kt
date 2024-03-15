package io.hamal.lib.web3.eth

import io.hamal.lib.web3.eth.abi.type.EthHexString
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.util.Web3Encoding

fun Web3Encoding.encodeRunLength(prefixedHexString: EthPrefixedHexString) = encodeRunLength(prefixedHexString.toHexString())
fun Web3Encoding.encodeRunLength(hexString: EthHexString) = encodeRunLength(hexString.toByteArray())