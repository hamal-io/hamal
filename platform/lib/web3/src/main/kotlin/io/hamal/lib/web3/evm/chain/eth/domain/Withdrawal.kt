package io.hamal.lib.web3.evm.chain.eth.domain

import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmUint64

data class EthWithdrawalData(
    val index: EvmUint64,
    val validatorIndex: EvmUint64,
    val address: EvmAddress,
    val amount: EvmUint64,
)
