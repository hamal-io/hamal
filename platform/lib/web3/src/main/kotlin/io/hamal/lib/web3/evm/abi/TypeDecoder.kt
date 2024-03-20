package io.hamal.lib.web3.evm.abi

import io.hamal.lib.web3.evm.abi.type.*
import io.hamal.lib.web3.util.ByteWindow
import java.math.BigInteger


data class DecodedEvmType<VALUE_TYPE : EvmType<*>>(
    val name: String,
    val value: VALUE_TYPE
)

interface EvmTypeDecoder<out VALUE_TYPE : EvmType<*>> {
    fun decode(window: ByteWindow): VALUE_TYPE

    companion object {

        val Address: EvmTypeDecoder<EvmAddress> = object : EvmTypeDecoder<EvmAddress> {
            override fun decode(window: ByteWindow) = EvmAddress(BigInteger(window.next()))
        }

        val Byte32: EvmTypeDecoder<EvmBytes32> = object : EvmTypeDecoder<EvmBytes32> {
            override fun decode(window: ByteWindow) = EvmBytes32(window.next())
        }

        val Bool: EvmTypeDecoder<EvmBool> = object : EvmTypeDecoder<EvmBool> {
            override fun decode(window: ByteWindow) = EvmBool(BigInteger(window.next()))
        }

        val Uint8: EvmTypeDecoder<EvmUint8> = object : EvmTypeDecoder<EvmUint8> {
            override fun decode(window: ByteWindow) = EvmUint8(BigInteger(window.next()))
        }

        val String: EvmTypeDecoder<EvmString> = object : EvmTypeDecoder<EvmString> {
            override fun decode(window: ByteWindow): EvmString {
                window.next()
                val length = Uint32.decode(window).value.toInt()
                val padding = padding(length)
                val bytes = window.next(length + padding)

                return EvmString(String(bytes).substring(0, length))
            }
        }

        val Uint16: EvmTypeDecoder<EvmUint16> = object : EvmTypeDecoder<EvmUint16> {
            override fun decode(window: ByteWindow) = EvmUint16(BigInteger(window.next()))
        }

        val Uint32: EvmTypeDecoder<EvmUint32> = object : EvmTypeDecoder<EvmUint32> {
            override fun decode(window: ByteWindow) = EvmUint32(BigInteger(window.next()))
        }

        val Uint64: EvmTypeDecoder<EvmUint64> = object : EvmTypeDecoder<EvmUint64> {
            override fun decode(window: ByteWindow) = EvmUint64(BigInteger(window.next()))
        }

        val Uint112: EvmTypeDecoder<EvmUint112> = object : EvmTypeDecoder<EvmUint112> {
            override fun decode(window: ByteWindow) = EvmUint112(BigInteger(window.next()))
        }

        val Uint128: EvmTypeDecoder<EvmUint128> = object : EvmTypeDecoder<EvmUint128> {
            override fun decode(window: ByteWindow) = EvmUint128(BigInteger(window.next()))
        }

        val Uint160: EvmTypeDecoder<EvmUint160> = object : EvmTypeDecoder<EvmUint160> {
            override fun decode(window: ByteWindow) = EvmUint160(BigInteger(window.next()))
        }

        val Uint256: EvmTypeDecoder<EvmUint256> = object : EvmTypeDecoder<EvmUint256> {
            override fun decode(window: ByteWindow) = EvmUint256(BigInteger(withLeadingSignBit(window.next())))
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

