package io.hamal.app.web3proxy.eth

import io.hamal.lib.web3.evm.domain.EvmRequestId
import io.hamal.lib.web3.evm.chain.eth.domain.EthErrorResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class EthInvalidInputTest : EthBaseTest() {

    @Test
    fun `Requests block with negative block number`() {
        testTemplate
            .body(
                """
                {
                    "id": "23",
                    "jsonrpc": "2.0",
                    "method": "eth_getBlockByNumber",
                    "params": ["-0x0",true]
                }
            """.trimIndent()
            )
            .execute(EthErrorResponse::class) {
                assertThat(id, equalTo(EvmRequestId("23")))
                assertThat(error.code, equalTo(-32602))
                assertThat(error.message, equalTo("invalid argument: hex string without 0x prefix"))
            }
    }

    @Test
    fun `Requests block with invalid hex string`() {
        testTemplate
            .body(
                """
                {
                    "id": "23",
                    "jsonrpc": "2.0",
                    "method": "eth_getBlockByNumber",
                    "params": ["0xINVALID_HEX_STRING",true]
                }
            """.trimIndent()
            )
            .execute(EthErrorResponse::class) {
                assertThat(id, equalTo(EvmRequestId("23")))
                assertThat(error.code, equalTo(-32602))
                assertThat(error.message, equalTo("invalid argument: hex string"))
            }
    }

    @Test
    fun `Requests block with missing parameter value`() {
        testTemplate
            .body(
                """
                {
                    "id": "23",
                    "jsonrpc": "2.0",
                    "method": "eth_getBlockByNumber",
                    "params": ["0x12345"]
                }
            """.trimIndent()
            )
            .execute(EthErrorResponse::class) {
                assertThat(id, equalTo(EvmRequestId("23")))
                assertThat(error.code, equalTo(-32602))
                assertThat(error.message, equalTo("missing argument"))
            }
    }

    @Test
    fun `Request method not supported`() {
        testTemplate
            .body(
                """
                {
                    "id": "23",
                    "jsonrpc": "2.0",
                    "method": "eth_INVALID_METHOD",
                    "params": ["0x12345",true]
                }
            """.trimIndent()
            )
            .execute(EthErrorResponse::class) {
                assertThat(id, equalTo(EvmRequestId("23")))
                assertThat(error.code, equalTo(-32601))
                assertThat(error.message, equalTo("method not supported"))
            }
    }
}

