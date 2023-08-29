package io.hamal.lib.web3.util

import org.bouncycastle.jcajce.provider.digest.Keccak

object Web3HashUtils {
    fun keccak256(bytes: ByteArray): ByteArray {
        val instance: Keccak.Digest256 = Keccak.Digest256()
        instance.update(bytes, 0, bytes.size)
        return instance.digest()
    }

}