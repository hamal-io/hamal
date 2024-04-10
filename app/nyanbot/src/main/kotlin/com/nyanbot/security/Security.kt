package com.nyanbot.security

import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.PasswordSalt
import java.security.SecureRandom
import java.util.*

interface GenerateSalt {
    operator fun invoke(): PasswordSalt
}

object SecureRandomSalt : GenerateSalt {
    override fun invoke(): PasswordSalt {
        val random = SecureRandom()
        val salt = ByteArray(64)
        random.nextBytes(salt)
        return PasswordSalt(HexFormat.of().formatHex(salt))
    }
}

interface GenerateToken {
    operator fun invoke(): AuthToken
}

object DomainGenerateToken : GenerateToken {
    override fun invoke(): AuthToken {
        val random = SecureRandom()
        val salt = ByteArray(64)
        random.nextBytes(salt)
        return AuthToken(HexFormat.of().formatHex(salt))
    }
}