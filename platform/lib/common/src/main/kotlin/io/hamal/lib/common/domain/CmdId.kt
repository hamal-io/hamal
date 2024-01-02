package io.hamal.lib.common.domain

import java.math.BigInteger
import java.security.SecureRandom

private val rnd = SecureRandom.getInstance("SHA1PRNG", "SUN")

data class CmdId(val value: String) : Comparable<CmdId> {

    // FIXME make sure its max 255 chars
    constructor(value: ByteArray) : this(String(value))
    constructor(value: Int) : this(value.toString())
    constructor(value: Long) : this(value.toString())
    constructor(value: BigInteger) : this(value.toString())
    constructor(value: ValueObjectId) : this(value.value.value)
    
    override fun compareTo(other: CmdId) = value.compareTo(other.value)

    operator fun plus(hashcode: Int): CmdId = CmdId("${value}_$hashcode")

    companion object {
        fun random() = CmdId(BigInteger(128, rnd))
    }
}
