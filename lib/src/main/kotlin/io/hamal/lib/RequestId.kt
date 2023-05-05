package io.hamal.lib

import java.math.BigInteger

@JvmInline
value class RequestId(val value: BigInteger) {
    constructor(value: ByteArray) : this(BigInteger(value))
    constructor(value: Int) : this(value.toBigInteger())
}
