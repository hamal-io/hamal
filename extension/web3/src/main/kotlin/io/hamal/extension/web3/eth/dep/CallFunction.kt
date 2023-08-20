package io.hamal.extension.web3.eth.dep

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthCallResponse
import io.hamal.lib.web3.eth.http.EthHttpBatchService
import java.math.BigInteger

class CallFunction(
    val config: ExtensionConfig
) : Function1In2Out<TableProxyMap, ErrorType, StringType>(
    FunctionInput1Schema(TableProxyMap::class),
    FunctionOutput2Schema(ErrorType::class, StringType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableProxyMap): Pair<ErrorType?, StringType?> {

        val b = EthHttpBatchService(
            HttpTemplate((config.value["host"] as StringType).value)
        ).call(
            EthBatchService.EthCallRequest(
                to = EthAddress(EthPrefixedHexString(arg1.getString("to"))),
                data = EthPrefixedHexString(arg1.getString("data")),
                blockNumber = EthUint64(BigInteger.valueOf(arg1.getLong("block")))
            )
        ).execute().first() as EthCallResponse

        return null to StringType(b.result.value)
    }
}