package io.hamal.lib.web3.evm.domain

import io.hamal.lib.web3.evm.EvmSignature
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

class EvmSignedMessageTest {
    @Test
    fun `Address of signed message`() {
        val testInstance = EvmSignedMessage(
            data = "Please sign this message to login: b72d984f".toByteArray(),
            signature = EvmSignature(EvmPrefixedHexString("0x511005f84ca22940f94c281d2888987d824377fd661f11fc1deae9dbcde4ac4d6c77fdd4d4f2c75dddf56148218937adbd4c0ebd31cb5f834641d31b5e775a001b"))
        )
        assertThat(testInstance.address, equalTo(EvmAddress("0xB72D984fb7E4763B57526CEf25A749EeC5334B93")))
    }
}