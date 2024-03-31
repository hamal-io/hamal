package io.hamal.lib.web3.evm.rlp

import org.web3j.rlp.RlpEncoder
import org.web3j.rlp.RlpList
import org.web3j.rlp.RlpString
import org.web3j.rlp.RlpType

object EncodeRlp {
    operator fun invoke(rlp: RlpValue): ByteArray {
        return RlpEncoder.encode(rlp.toRlpType())
    }
}

private fun RlpValue.toRlpType(): RlpType = when (this) {
    is RlpValue.String -> RlpString.create(value)
    is RlpValue.List -> RlpList(values.map { it.toRlpType() })
}