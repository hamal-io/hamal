package io.hamal.lib.web3.evm.domain

interface EvmResponse {
    val jsonrpc: String
    val id: EvmRequestId
}