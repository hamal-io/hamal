package io.hamal.lib.web3.util

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

object RunLengthTest {

    @Test
    fun `Encodes & Decodes small sized web3 input`() {
        val given =
            "0xa9059cbb0000000000000000000000003d55ccb2a943d88d39dd2e62daf767c69fd0179f000000000000000000000000000000000000000000000000000000001d8119c0"
        val encoded = Web3Encoding.encodeRunLength(given.toByteArray())
        assertThat(encoded.size, equalTo(108))

        val decoded = String(Web3Encoding.decodeRunLength(encoded))
        assertThat(decoded, equalTo(given))
    }

    @Test
    fun `Encodes & Decodes medium sized web3 input`() {
        val given =
            "0x1509af71000000000000000000000000232ffdbfe4497193257026a82a7f4e053fef0aea000000000000000000000000c02aaa39b223fe8d0a0e5c4f27ead9083c756cc20000000000000000000000007a250d5630b4cf539739df2c5dacb4c659f2488d00000000000000000000000025abeba863505aaa27cd391363bd521615cce3fe0000000000000000000000000000000000000000000000000000000000000064000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000064c11ca7"
        val encoded = Web3Encoding.encodeRunLength(given.toByteArray())
        assertThat(encoded.size, equalTo(354))

        val decoded = String(Web3Encoding.decodeRunLength(encoded))
        assertThat(decoded, equalTo(given))
    }

}