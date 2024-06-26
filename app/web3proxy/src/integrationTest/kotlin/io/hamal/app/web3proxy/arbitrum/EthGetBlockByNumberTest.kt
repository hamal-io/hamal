package io.hamal.app.web3proxy.arbitrum

import io.hamal.lib.common.serialization.json.JsonArray
import io.hamal.lib.common.serialization.json.JsonObject
import io.hamal.lib.http.body
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.domain.EvmRequestId
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockByNumberRequest
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockResponse
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class ArbitrumGetBlockBySerdeNumberTest : ArbitrumBaseTest() {

    @Test
    fun `Requests the same block multiple times`() {
        testTemplate
            .body(
                listOf(
                    ArbitrumGetBlockByNumberRequest(EvmRequestId("3"), EvmUint64("0x1284810"), true),
                    ArbitrumGetBlockByNumberRequest(EvmRequestId("1"), EvmUint64("0x1284810"), true),
                    ArbitrumGetBlockByNumberRequest(EvmRequestId("2"), EvmUint64("0x1284810"), true)
                )
            )
            .executeList(ArbitrumGetBlockResponse::class) { responses ->
                assertThat(responses, hasSize(3))
                assertThat(responses[0].id, equalTo(EvmRequestId("3")))
                assertThat(responses[1].id, equalTo(EvmRequestId("1")))
                assertThat(responses[2].id, equalTo(EvmRequestId("2")))
            }
    }

    @Test
    fun `Requests future block which does not exist yet`() {
        testTemplate
            .body(
                listOf(
                    ArbitrumGetBlockByNumberRequest(EvmRequestId("3"), EvmUint64("0xB4DC0DE"), true)
                )
            )
            .executeList(ArbitrumGetBlockResponse::class) { responses ->
                assertThat(responses, hasSize(1))
                assertThat(responses[0].id, equalTo(EvmRequestId("3")))
                assertThat(responses[0].result, nullValue())
            }
    }

    @Test
    fun `Requests future block which does not exist yet and existing blocks`() {
        testTemplate
            .body(
                listOf(
                    ArbitrumGetBlockByNumberRequest(EvmRequestId("4"), EvmUint64("0xB4DC0DE"), true),
                    ArbitrumGetBlockByNumberRequest(EvmRequestId("3"), EvmUint64("0x1284810"), true),
                    ArbitrumGetBlockByNumberRequest(EvmRequestId("2"), EvmUint64("0xB4DC0DE"), true),
                    ArbitrumGetBlockByNumberRequest(EvmRequestId("1"), EvmUint64("0x100002"), true)

                )
            )
            .executeList(ArbitrumGetBlockResponse::class) { responses ->
                assertThat(responses, hasSize(4))

                assertThat(responses[0].id, equalTo(EvmRequestId("4")))
                assertThat(responses[0].result, nullValue())

                assertThat(responses[1].id, equalTo(EvmRequestId("3")))
                assertBlock0x1284810(responses[1].result)

                assertThat(responses[2].id, equalTo(EvmRequestId("2")))
                assertThat(responses[2].result, nullValue())

                assertThat(responses[3].id, equalTo(EvmRequestId("1")))
                assertBlock0x100002(responses[3].result)
            }
    }


    @Test
    fun `Request single block with full transaction list by ArbitrumGetBlockByNumberRequest`() {
        testTemplate
            .body(ArbitrumGetBlockByNumberRequest(EvmRequestId("231123"), EvmUint64("0x1284810"), true))
            .execute(ArbitrumGetBlockResponse::class) {
                assertThat(id, equalTo(EvmRequestId("231123")))
                assertBlock0x1284810(result)
            }
    }

    @Test
    fun `Request single block with full transaction list by hot request`() {
        testTemplate
            .body(
                JsonObject.builder()
                    .set("id", "23")
                    .set("jsonrpc", "2.0")
                    .set("method", "eth_getBlockByNumber")
                    .set("params", JsonArray.builder().append("0x100002").append(true).build())
                    .build()
            )
            .execute(ArbitrumGetBlockResponse::class) {
                assertThat(id, equalTo(EvmRequestId("23")))
                assertBlock0x100002(result)
            }
    }

    @Test
    fun `Request single block with full transaction list by json request`() {
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
            .execute(ArbitrumGetBlockResponse::class) {
                assertThat(id, equalTo(EvmRequestId("23")))
                assertBlock0x100002(result)
            }
    }

    @Test
    fun `Request multiple blocks with full transaction list by ArbitrumGetBlockByNumberRequest`() {
        testTemplate.body(
            listOf(
                ArbitrumGetBlockByNumberRequest(EvmRequestId("2"), EvmUint64("0x100002"), true),
                ArbitrumGetBlockByNumberRequest(EvmRequestId("1"), EvmUint64("0x1284810"), true)
            )
        ).executeList(ArbitrumGetBlockResponse::class) { responses ->
            assertThat(responses, hasSize(2))

            assertThat(responses[0].id, equalTo(EvmRequestId("2")))
            assertBlock0x100002(responses[0].result)


            assertThat(responses[1].id, equalTo(EvmRequestId("1")))
            assertBlock0x1284810(responses[1].result)
        }
    }

    @Test
    fun `Request multiple blocks with full transaction list by hot request`() {
        testTemplate
            .body(
                JsonArray.builder()
                    .append(
                        JsonObject.builder()
                            .set("id", "2")
                            .set("jsonrpc", "2.0")
                            .set("method", "eth_getBlockByNumber")
                            .set("params", JsonArray.builder().append("0x100002").append(true).build())
                            .build()
                    )
                    .append(
                        JsonObject.builder()
                            .set("id", "1")
                            .set("jsonrpc", "2.0")
                            .set("method", "eth_getBlockByNumber")
                            .set("params", JsonArray.builder().append("0x1284810").append(true).build())
                            .build()
                    )
                    .build()
            )
            .executeList(ArbitrumGetBlockResponse::class) { responses ->
                assertThat(responses, hasSize(2))
                assertThat(responses[0].id, equalTo(EvmRequestId("2")))
                assertBlock0x100002(responses[0].result)
                assertThat(responses[1].id, equalTo(EvmRequestId("1")))
                assertBlock0x1284810(responses[1].result)
            }
    }

    @Test
    fun `Request multiple blocks with full transaction list by json request`() {
        testTemplate
            .body(
                """
                [{
                    "id": "2",
                    "jsonrpc": "2.0",
                    "method": "eth_getBlockByNumber",
                    "params": ["0x100002",true]
                },
                {
                    "id": "1",
                    "jsonrpc": "2.0",
                    "method": "eth_getBlockByNumber",
                    "params": ["0x1284810",true]
                }]
            """.trimIndent()
            )
            .executeList(ArbitrumGetBlockResponse::class) { responses ->
                assertThat(responses, hasSize(2))
                assertThat(responses[0].id, equalTo(EvmRequestId("2")))
                assertBlock0x100002(responses[0].result)
                assertThat(responses[1].id, equalTo(EvmRequestId("1")))
                assertBlock0x1284810(responses[1].result)
            }
    }
}