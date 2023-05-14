package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.util.ByteWindow
import io.hamal.lib.web3.util.Web3Parser

sealed class EthString : EthType<String> {
    operator fun get(idx: Int): Char = value[idx]

    fun lowercase(): String = value.lowercase()

    fun filter(predicate: (Char) -> Boolean): String {
        return value.filter(predicate)
    }

    fun all(predicate: (Char) -> Boolean): Boolean {
        return value.all(predicate)
    }
}

data class EthHexString(
    override val value: String
) : EthString() {

    init {
        ValidateHexString(value)
    }

    override fun toByteArray(): ByteArray = Web3Parser.parseHex(value)
    override fun toByteWindow() = ByteWindow.of(this)
    override fun toString(): String = value
}

data class EthPrefixedHexString(
    override val value: String
) : EthString() {
    init {
        require(value.startsWith("0x")) { "$value does not start with 0x" }
        ValidateHexString(value.substring(2))
    }

    fun toHexString(): EthHexString = EthHexString(value.substring(2))
    override fun toByteArray(): ByteArray = toHexString().toByteArray()
    override fun toByteWindow() = ByteWindow.of(this)
    override fun toString(): String = value
}


internal object ValidateHexString {
    operator fun invoke(hexCandidate: String) {
        require(hexPattern.find(hexCandidate) != null) { "$hexCandidate does not match hex pattern" }

    }

    private val hexPattern = Regex("""^[0-9a-fA-F]*$""")
}