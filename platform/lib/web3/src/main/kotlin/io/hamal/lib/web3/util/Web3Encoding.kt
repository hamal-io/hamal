package io.hamal.lib.web3.util

object Web3Encoding {

    fun encodeRunLength(binary: ByteArray): ByteArray {
        val compressed = ArrayList<Byte>()
        var counter = 1
        var i = 0
        while (i < binary.size) {
            while (i + 1 < binary.size && binary[i] == binary[i + 1]) {
                counter++
                if (counter == 256) {
                    compressed.add(0.toByte())
                    counter = 1
                }
                i++
            }
            compressed.add(counter.toByte())
            compressed.add(binary[i])
            counter = 1
            i++
        }
        return toPrimitives(compressed.toTypedArray())
    }

    fun decodeRunLength(compressed: ByteArray): ByteArray {
        val decompressed = ArrayList<Byte>()
        var tmp: Byte
        var count = 0
        var i = 0
        while (i < compressed.size) {
            tmp = compressed[i]
            if (tmp.toInt() == 0) {
                count += 255
                i++
                continue
            }
            count += tmp.toInt()
            for (j in 0 until count) {
                decompressed.add(compressed[i + 1])
            }
            i++
            count = 0
            i++
        }
        return toPrimitives(decompressed.toTypedArray())
    }

    private fun toPrimitives(otherBytes: Array<Byte>): ByteArray {
        val bytes = ByteArray(otherBytes.size)
        for (idx in otherBytes.indices) {
            bytes[idx] = otherBytes[idx]
        }
        return bytes
    }

}