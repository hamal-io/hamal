package io.hamal.extension.web3.hml

import io.hamal.lib.common.logger
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
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.hml.domain.HmlCallResponse
import io.hamal.lib.web3.hml.domain.HmlGetBlockResponse
import io.hamal.lib.web3.hml.http.HmlHttpBatchService

private val log = logger(HmlExecuteFunction::class)


class HmlExecuteFunction(
    private val config: ExtensionConfig
) : Function1In2Out<ArrayType, ErrorType, ArrayType>(
    FunctionInput1Schema(ArrayType::class),
    FunctionOutput2Schema(ErrorType::class, ArrayType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: ArrayType): Pair<ErrorType?, ArrayType?> {
        try {
            val batchService = HmlHttpBatchService(HttpTemplate((config.value["host"] as StringType).value))

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

                    else -> TODO()
                }
            }

            return null to batchService.execute().let {
                val result = ArrayType()
                it.forEach { ethRes ->
                    when (ethRes) {
                        is HmlGetBlockResponse -> {
                            val res = MapType()
                            res["id"] = ethRes.result.number.value.toLong()
                            res["hash"] = ethRes.result.hash.toPrefixedHexString().value
                            res["gas_used"] = ethRes.result.gasUsed.value.toLong()
                            res["gas_limit"] = ethRes.result.gasLimit.value.toLong()
                            result.append(res)
                            log.trace("${ethRes.id} - block response: $res")
                        }

                        is HmlCallResponse -> TODO()
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