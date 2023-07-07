package io.hamal.lib.web3.eth.abi

import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test


internal object Web3EventSignatureTest {
    @Test
    fun `encoded - Transfer(address,address,uint256)`() {
        val testInstance = EthSignature("Transfer(address,address,uint256)")
        val result = testInstance.encoded
        assertThat(
            result,
            equalTo(EthHash(EthPrefixedHexString("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef")))
        )
    }
}