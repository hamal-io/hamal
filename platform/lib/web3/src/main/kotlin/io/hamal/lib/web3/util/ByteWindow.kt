package io.hamal.lib.web3.util

import io.hamal.lib.web3.evm.abi.type.EvmHexString
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import java.nio.ByteBuffer

class ByteWindow internal constructor(
    private val buffer: ByteBuffer,
    val windowSize: Int
) : Iterable<ByteArray> {

    companion object {
        fun of(data: EvmHexString, windowSize: Int = 32): ByteWindow {
            val bytes = data.toByteArray()
            if (bytes.size < windowSize) {
                return of(EvmHexString(Web3Formatter.formatFixLength(bytes, windowSize * 2)))
            }
            return of(bytes, windowSize)
        }

        fun of(data: EvmPrefixedHexString, windowSize: Int = 32): ByteWindow {
            return of(data.toHexString(), windowSize)
        }

        fun of(data: ByteArray, windowSize: Int = 32): ByteWindow {
            val buffer = ByteBuffer.wrap(data)
            return ByteWindow(buffer, windowSize)
        }
    }

    init {
        require(windowSize >= 1) { "window size must be >= 1" }
        require(buffer.remaining() >= windowSize) { "Input size must >= window size $windowSize" }
        require(buffer.remaining() % windowSize == 0) { "Input size is not multiple of window size $windowSize" }
    }

    fun next(): ByteArray {
        return next(windowSize)
    }

    fun next(numberOfBytes: Int): ByteArray {
        require(numberOfBytes > 0) { "Tries to consume 0 bytes" }
        require(remaining() >= numberOfBytes) {
            "Tries to consume $numberOfBytes bytes but only ${remaining()} bytes are available"
        }
        require(numberOfBytes % windowSize == 0) {
            "Tries to consume $numberOfBytes bytes which is not multiple of window size $windowSize"
        }
        val result = ByteArray(numberOfBytes)
        buffer[result]
        return result
    }

    override fun iterator(): Iterator<ByteArray> {
        val self = this
        return object : Iterator<ByteArray> {
            override fun hasNext() = self.remaining() > 0
            override fun next() = self.next()
        }
    }

    override fun toString(): String {
        return "ByteWindow(" + buffer.position() + '/' + remaining() + ')'
    }

    fun remaining(): Int {
        return buffer.remaining()
    }
}


