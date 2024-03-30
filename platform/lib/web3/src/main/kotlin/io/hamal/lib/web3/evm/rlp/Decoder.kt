package io.hamal.lib.web3.evm.rlp

import org.web3j.rlp.RlpDecoder
import org.web3j.rlp.RlpList
import org.web3j.rlp.RlpString
import org.web3j.rlp.RlpType

object DecodeRlp {

    operator fun invoke(data: ByteArray): RlpValue {
        val result = RlpDecoder.decode(data).toRlpValue()
        return if (result is RlpValue.List && result.values.size == 1) {
            result.values[0]
        } else {
            result
        }
    }

}

private fun RlpType.toRlpValue(): RlpValue = when (this) {
    is RlpString -> RlpValue.String(bytes)
    is RlpList -> RlpValue.List(values.map { it.toRlpValue() })
    else -> TODO()
}