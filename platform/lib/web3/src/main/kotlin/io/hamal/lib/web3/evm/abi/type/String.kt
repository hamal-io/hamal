package io.hamal.lib.web3.evm.abi.type

import io.hamal.lib.web3.util.ByteWindow
import io.hamal.lib.web3.util.Web3Parser
import java.nio.charset.Charset


sealed interface EvmBaseString : EvmType<String> {
    operator fun get(idx: Int): Char = value[idx]

    fun lowercase(): String = value.lowercase()

    fun filter(predicate: (Char) -> Boolean): String {
        return value.filter(predicate)
    }

    fun all(predicate: (Char) -> Boolean): Boolean {
        return value.all(predicate)
    }
}

data class EvmString(
    override val value: String
) : EvmBaseString {
    override fun toByteArray(): ByteArray = value.toByteArray()
    override fun toByteWindow() = ByteWindow.of(toByteArray())
    override fun toString(): String = value
}

data class EvmHexString(
    override val value: String
) : EvmBaseString {

    init {
        ValidateHexString(value)
    }

    override fun toByteArray(): ByteArray = Web3Parser.parseHex(value)
    override fun toByteWindow() = ByteWindow.of(this)
    override fun toString(): String = value
}

data class EvmPrefixedHexString(
    override val value: String
) : EvmBaseString {
    constructor(byteArray: ByteArray) : this(String(byteArray, Charset.forName("UTF-8")))
    constructor(value: EvmPrefixedHexString) : this(value.value)

    init {
        require(value.startsWith("0x")) { "$value does not start with 0x" }
        ValidateHexString(value.substring(2))
    }

    fun toHexString(): EvmHexString = EvmHexString(value.substring(2))
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