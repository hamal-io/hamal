package io.hamal.lib.web3.evm.impl.eth.domain

import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmUint64

data class Withdrawal(
    val index: EvmUint64,
    val validatorIndex: EvmUint64,
    val address: EvmAddress,
    val amount: EvmUint64,
)
