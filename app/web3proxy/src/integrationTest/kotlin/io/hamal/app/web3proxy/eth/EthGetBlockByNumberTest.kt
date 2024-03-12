package io.hamal.app.web3proxy.eth

import io.hamal.lib.http.body
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthGetBlockByNumberRequest
import io.hamal.lib.web3.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.eth.domain.EthRequestId
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class EthGetBlockByNumberTest : BaseTest() {

    @Test
    fun `Gets single block with full transaction list by EthGetBlockByNumberRequest`() {
        testTemplate
            .body(EthGetBlockByNumberRequest(EthRequestId(231123), EthUint64("0x1284810"), true))
            .execute(EthGetBlockResponse::class) {
                assertThat(id, equalTo(EthRequestId(231123)))
                assertBlock0x1284810(result)
            }
    }

    @Test
    fun `Gets single block with full transaction list by json request`() {
        testTemplate
            .body(
                """
                {
                    "id": "23",
                    "jsonrpc": "2.0",
                    "method": "eth_getBlockByNumber",
                    "params": ["0x100002",true]
                }
            """.trimIndent()
            )
            .execute(EthGetBlockResponse::class) {
                assertThat(id, equalTo(EthRequestId(23)))
                assertBlock0x100002(result)
            }
    }

}