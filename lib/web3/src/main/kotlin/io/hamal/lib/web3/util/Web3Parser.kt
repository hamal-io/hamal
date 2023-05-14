package io.hamal.lib.web3.util

import java.util.*

object Web3Parser {
    fun parseHex(str: String): ByteArray = HexFormat.of().parseHex(str)
}