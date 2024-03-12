package io.hamal.lib.web3.eth.domain

import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthUint64


data class EthWithdrawal(
    val index: EthUint64,
    val validatorIndex: EthUint64,
    val address: EthAddress,
    val amount: EthUint64,
)
