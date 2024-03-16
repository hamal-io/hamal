package io.hamal.lib.web3.util

import java.util.*

object Web3Parser {
    fun parseHex(str: String): ByteArray {
        if (str == "0") {
            return byteArrayOf(0)
        }
        return HexFormat.of().parseHex(str)
    }
}