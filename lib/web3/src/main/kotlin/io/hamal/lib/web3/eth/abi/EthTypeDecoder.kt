package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.util.ByteWindow
import java.math.BigInteger

data class DecodedEthType<VALUE_TYPE : EthType<*>>(
    val name: String,
    val value: VALUE_TYPE
)

interface EthTypeDecoder<VALUE_TYPE : EthType<*>> {
    fun decode(window: ByteWindow): VALUE_TYPE

    companion object {

        val Address: EthTypeDecoder<EthAddress> = object : EthTypeDecoder<EthAddress> {
            override fun decode(window: ByteWindow) = EthAddress(BigInteger(window.next()))
        }

        val Byte32: EthTypeDecoder<EthBytes32> = object : EthTypeDecoder<EthBytes32> {
            override fun decode(window: ByteWindow) = EthBytes32(window.next())
        }

        val Bool: EthTypeDecoder<EthBool> = object : EthTypeDecoder<EthBool> {
            override fun decode(window: ByteWindow) = EthBool(BigInteger(window.next()))
        }

        val Uint8: EthTypeDecoder<EthUint8> = object : EthTypeDecoder<EthUint8> {
            override fun decode(window: ByteWindow) = EthUint8(BigInteger(window.next()))
        }

        val String: EthTypeDecoder<EthString> = object : EthTypeDecoder<EthString> {
            override fun decode(window: ByteWindow): EthString {
                val length = Uint32.decode(window).value.toInt()
                val padding = padding(length)
                val bytes = window.next(length + padding)

                return EthString(String(bytes).substring(0, length))
            }
        }

        val Uint16: EthTypeDecoder<EthUint16> = object : EthTypeDecoder<EthUint16> {
            override fun decode(window: ByteWindow) = EthUint16(BigInteger(window.next()))
        }

        val Uint32: EthTypeDecoder<EthUint32> = object : EthTypeDecoder<EthUint32> {
            override fun decode(window: ByteWindow) = EthUint32(BigInteger(window.next()))
        }

        val Uint64: EthTypeDecoder<EthUint64> = object : EthTypeDecoder<EthUint64> {
            override fun decode(window: ByteWindow) = EthUint64(BigInteger(window.next()))
        }

        val Uint112: EthTypeDecoder<EthUint112> = object : EthTypeDecoder<EthUint112> {
            override fun decode(window: ByteWindow) = EthUint112(BigInteger(window.next()))
        }

        val Uint128: EthTypeDecoder<EthUint128> = object : EthTypeDecoder<EthUint128> {
            override fun decode(window: ByteWindow) = EthUint128(BigInteger(window.next()))
        }

        val Uint160: EthTypeDecoder<EthUint160> = object : EthTypeDecoder<EthUint160> {
            override fun decode(window: ByteWindow) = EthUint160(BigInteger(window.next()))
        }

        val Uint256: EthTypeDecoder<EthUint256> = object : EthTypeDecoder<EthUint256> {
            override fun decode(window: ByteWindow) = EthUint256(BigInteger(withLeadingSignBit(window.next())))
        }

        private fun padding(length: Int): Int {
            val mod = length % 32
            return 32 - mod
        }

        private fun withLeadingSignBit(bs: ByteArray): ByteArray {
            // big integer requires for uint256 - 33 bytes - 1 sign bit and 32 bytes for the value
            return ByteArray(33).also { System.arraycopy(bs, 0, it, 1, 32) }
        }
    }

}

