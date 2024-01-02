package io.hamal.lib.web3.eth.domain

enum class EthMethod(val value: String) {
    Call("eth_call"),
    GetBlockByNumber("eth_getBlockByNumber");
}