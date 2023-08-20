package io.hamal.extension.web3.eth

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.ArrayType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthCallResponse
import io.hamal.lib.web3.eth.domain.EthGetBlockNumberResponse
import io.hamal.lib.web3.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.eth.domain.EthGetLiteBlockResponse
import io.hamal.lib.web3.eth.http.EthHttpBatchService
import logger
import kotlin.to

private val log = logger(EthExecuteFunction::class)

class EthExecuteFunction(
    private val config: ExtensionConfig
) : Function1In2Out<ArrayType, ErrorType, ArrayType>(
    FunctionInput1Schema(ArrayType::class),
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ArrayType): Pair<ErrorType?, ArrayType?> {
        try {
            log.trace("Setting up batch service")

            val batchService = EthHttpBatchService(HttpTemplate((config.value["host"] as StringType).value))


//            val iterator = TableEntryIterator(
//                arg1.index,
//                ctx,
//                keyExtractor = { state, index -> state.getNumberValue(ctx.absIndex(index)) },
//                valueExtractor = { state, index -> state.toTableType(state.getTableMap(ctx.absIndex(index))) }
//            )

            arg1.entries.forEach { entry ->
                println(entry.key)
                println(entry.value)

                val v = entry.value
                require(v is MapType)

                when (v.getString("type")) {
                    "get_block" -> {
                        val block = v.getLong("block")
                        batchService.getBlock(EthUint64(block))
                        log.trace("Requesting block $block")
                    }

                    "call" -> {
                        val block = v.getLong("block")
                        batchService.call(
                            EthBatchService.EthCallRequest(
                                to = EthAddress(EthPrefixedHexString(v.getString("to"))),
                                data = EthPrefixedHexString(v.getString("data")),
                                blockNumber = EthUint64(block),
                            )
                        )
                        log.trace("Requesting block $block")
                    }
                }
            }

            return null to batchService.execute().let {
                val result = ArrayType()
                it.forEach { ethRes ->
                    when (ethRes) {
                        is EthGetBlockNumberResponse -> TODO()
                        is EthGetLiteBlockResponse -> TODO()
                        is EthGetBlockResponse -> {
                            val res = MapType()
                            res["id"] = ethRes.result.number.value.toLong()
                            res["hash"] = ethRes.result.hash.toPrefixedHexString().value
                            res["parent_hash"] = ethRes.result.parentHash.toPrefixedHexString().value
                            res["gas_used"] = ethRes.result.gasUsed.value.toLong()
                            res["gas_limit"] = ethRes.result.gasLimit.value.toLong()
                            result.append(res)
                            log.trace("${ethRes.id} - block response: $res")
                        }

                        is EthCallResponse -> {
                            result.append(ethRes.result.value)
                            log.trace("${ethRes.id} - call response: ${ethRes.result.value}")
                        }
                    }
                }
                result
            }

        } catch (t: Throwable) {
            t.printStackTrace()
            return ErrorType(t.message ?: "Unknown error") to null
        }
    }
}