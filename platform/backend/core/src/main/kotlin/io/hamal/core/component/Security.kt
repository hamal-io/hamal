package io.hamal.core.component

import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.Password
import io.hamal.lib.domain.vo.PasswordHash
import io.hamal.lib.domain.vo.PasswordSalt
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

interface EncodePassword {
    operator fun invoke(password: Password, salt: PasswordSalt): PasswordHash
}

object PBKDF2 : EncodePassword {
    override fun invoke(password: Password, salt: PasswordSalt): PasswordHash {
        val input = "${salt.value}${password.value}".toByteArray()
        val spec = PBEKeySpec(password.value.toCharArray(), input, 210_000, 256)
        val key = factory.generateSecret(spec)
        return PasswordHash(HexFormat.of().formatHex(key.encoded))
    }

    private val factory: SecretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512")
}

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