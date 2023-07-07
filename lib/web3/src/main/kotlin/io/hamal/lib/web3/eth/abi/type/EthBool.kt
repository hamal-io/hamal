package io.hamal.lib.web3.eth.abi.type

import io.hamal.lib.web3.util.ByteWindow
import java.math.BigInteger


class EthBool(override val value: Boolean) : EthType<Boolean> {
    constructor(value: BigInteger) : this(value.compareTo(BigInteger.ZERO) != 0)

    override fun toByteArray(): ByteArray = ByteArray(1).also { it[0] = if (value) 1 else 0 }
    override fun toByteWindow() = ByteWindow.of(toByteArray())
}