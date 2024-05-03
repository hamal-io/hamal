package io.hamal.lib.common.compress

object CompressorNop : Compressor {

    override fun compress(uncompressed: ByteArray) = uncompressed

    override fun toArray(compressed: ByteArray) = compressed

}