package io.hamal.lib.web3.util

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class Web3HashUtilsTest {
    @Nested
    @DisplayName("keccak256()")
    inner class Keccak256Test {
        @Test
        fun `Keccak of hamal`() {
            val result = Web3HashUtils.keccak256("hamal".toByteArray())
            assertThat(
                Web3Formatter.formatToHex(result),
                equalTo("ee2f90bf943a2cb813078a4f2463fc29b4fd50f248b8180c553b3d0364b42156")
            )
        }
    }
}