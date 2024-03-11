package io.hamal.lib.common.compress

object NopCompressor : Compressor {

    override fun compress(uncompressed: ByteArray) = uncompressed

    override fun toArray(compressed: ByteArray) = compressed

}