package io.hamal.plugin.web3.evm.dep

import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType

class CallFunction : Function1In2Out<TableProxyMap, ErrorType, StringType>(
    FunctionInput1Schema(TableProxyMap::class),
    FunctionOutput2Schema(ErrorType::class, StringType::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableProxyMap): Pair<ErrorType?, StringType?> {

//        val b = EthHttpBatchService(
////            HttpTemplateImpl((config.value["host"] as StringType).value)
//        ).call(
//            EthBatchService.EthCallRequest(
//                to = EthAddress(EthPrefixedHexString(arg1.getString("to"))),
//                data = EthPrefixedHexString(arg1.getString("data")),
//                blockNumber = EthUint64(BigInteger.valueOf(arg1.getLong("block")))
//            )
//        ).execute().first() as EthCallResponse
//
//        return null to StringType(b.result.value)

        TODO()
    }
}