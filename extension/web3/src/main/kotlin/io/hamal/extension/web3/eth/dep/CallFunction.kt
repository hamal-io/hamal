package io.hamal.extension.web3.eth.dep

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.kua.extension.ExtensionConfig
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthCallResp
import io.hamal.lib.web3.eth.http.EthHttpBatchService
import java.math.BigInteger

class CallFunction(
    val config: ExtensionConfig
) : Function1In2Out<TableMapValue, ErrorValue, StringValue>(
    FunctionInput1Schema(TableMapValue::class),
    FunctionOutput2Schema(ErrorValue::class, StringValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMapValue): Pair<ErrorValue?, StringValue?> {

        val b = EthHttpBatchService(
            HttpTemplate((config.value["host"] as StringValue).value)
        ).call(
            EthBatchService.EthCallRequest(
                to = EthAddress(EthPrefixedHexString(arg1.getString("to"))),
                data = EthPrefixedHexString(arg1.getString("data")),
                blockNumber = EthUint64(BigInteger.valueOf(arg1.getLong("block")))
            )
        ).execute().first() as EthCallResp

        return null to StringValue(b.result.value)
    }
}