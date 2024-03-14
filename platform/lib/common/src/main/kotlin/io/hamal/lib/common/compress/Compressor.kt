package io.hamal.lib.common.compress


interface Compressor {
    fun compress(uncompressed: ByteArray): ByteArray
    fun compress(uncompressed: String): ByteArray = compress(uncompressed.toByteArray())

    fun toArray(compressed: ByteArray): ByteArray
    fun toString(compressed: ByteArray): String = String(toArray(compressed))
}