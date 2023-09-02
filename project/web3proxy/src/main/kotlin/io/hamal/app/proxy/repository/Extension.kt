package io.hamal.app.proxy.repository

import io.hamal.lib.sqlite.NamedPreparedStatementDelegate
import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthUint64

operator fun NamedPreparedStatementDelegate.set(
    param: String,
    value: EthUint64
): NamedPreparedStatementDelegate {
    delegate[param] = value.value
    return this
}

operator fun NamedPreparedStatementDelegate.set(
    param: String,
    value: EthHash
): NamedPreparedStatementDelegate {
    delegate[param] = value.value.toByteArray()
    return this
}