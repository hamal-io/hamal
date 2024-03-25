package io.hamal.lib.web3

import io.hamal.lib.domain.vo.Web3Address
import io.hamal.lib.domain.vo.Web3Challenge
import org.junit.jupiter.api.Test
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.crypto.Sign
import java.nio.charset.StandardCharsets


class SomeTest {
    @Test
    fun test() {
        val privateAccountKey = "503f38a9c967ed597e47fe25643985f032b072db8075426a92110f82df48dfcb"
        val messageToBeSigned = "Hello"
        val credentials: Credentials = Credentials.create(privateAccountKey)

        val messageBytes: ByteArray = messageToBeSigned.toByteArray(StandardCharsets.UTF_8)
        val signature = Sign.signPrefixedMessage(messageBytes, credentials.ecKeyPair)


        val retval = ByteArray(65)
        System.arraycopy(signature.r, 0, retval, 0, 32)
        System.arraycopy(signature.s, 0, retval, 32, 32)
        System.arraycopy(signature.v, 0, retval, 64, 1)

        println(credentials.address)

        val pubKey = Sign.signedPrefixedMessageToKey(messageBytes, signature).toString(16)
        println(Keys.getAddress(pubKey))
    }

    @Test
    fun sigTest() {
        val address = Web3Address("0xb72d984fb7e4763b57526cef25a749eec5334b93")
        val challenge = Web3Challenge(
            "Please sign this message to login: ${address.value.substring(2, 10)}"
        )
//        0x511005f84ca22940f94c281d2888987d824377fd661f11fc1deae9dbcde4ac4d6c77fdd4d4f2c75dddf56148218937adbd4c0ebd31cb5f834641d31b5e775a001b

        val signature =
            Sign.signatureDataFromHex("0x511005f84ca22940f94c281d2888987d824377fd661f11fc1deae9dbcde4ac4d6c77fdd4d4f2c75dddf56148218937adbd4c0ebd31cb5f834641d31b5e775a001b")

        val pubKey = Sign.signedPrefixedMessageToKey(challenge.value.toByteArray(), signature).toString(16)

        println(Keys.getAddress(pubKey))
    }
}