package io.hamal.plugin.web3.evm.evm

import io.hamal.lib.common.logger
import io.hamal.lib.http.HttpTemplateImpl
import io.hamal.lib.kua.absIndex
import io.hamal.lib.kua.function.Function1In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.topPop
import io.hamal.lib.kua.type.*
import io.hamal.lib.web3.evm.abi.type.EvmPrefixedHexString
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.impl.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.evm.impl.eth.http.EthHttpBatchService

private val log = logger(EthExecuteFunction::class)

class EthExecuteFunction : Function1In2Out<KuaTable, KuaError, KuaTable>(
    FunctionInput1Schema(KuaTable::class),
    FunctionOutput2Schema(KuaError::class, KuaTable::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: KuaTable): Pair<KuaError?, KuaTable?> {
        try {

            val url = arg1.getString("url")
            val batchService = EthHttpBatchService(HttpTemplateImpl(url.stringValue))

            arg1.getTable("requests").also { requestsTable ->
                ctx.checkpoint {
                    ctx.nilPush()

                    while (ctx.tableNext(requestsTable.index).booleanValue) {
                        val request = ctx.tableGet(ctx.absIndex(-1))
                        ctx.checkpoint {
                            when (request.getString("type")) {
                                KuaString("get_block") -> {

                                    val block = when (request.type("block")) {
                                        KuaNumber::class -> batchService.getBlock(EvmUint64(request.getLong("block")))
                                        //FIXME support get by hash too
                                        KuaString::class -> {
                                            val block = request.getString("block").stringValue
                                            if (block.isDecimal()) {
                                                batchService.getBlock(EvmUint64(block.toBigInteger()))
                                            } else {
                                                batchService.getBlock(EvmUint64(EvmPrefixedHexString(block)))
                                            }
                                        }

                                        else -> TODO()
                                    }
                                    log.debug("Requesting block $block")
                                }

                                else -> TODO()
                            }
                        }
                        ctx.topPop(1)
                    }
                }
            }

            return null to batchService.execute().let {
                ctx.tableCreate(it.size, 0).also { result ->
                    it.forEach { response ->
                        when (response) {
                            is EthGetBlockResponse -> {
                                if (response.result != null) {
                                    result.append(
                                        ctx.tableCreate().also { result ->
                                            result["id"] = KuaString(response.id.value)
                                            result["result"] = ctx.tableCreate().also { blockResult ->
                                                blockResult["base_fee_per_gas"] = response.result?.baseFeePerGas?.toPrefixedHexString()?.value?.let(::KuaString)
                                                    ?: KuaNil
                                                blockResult["extra_data"] = response.result?.extraData?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["gas_limit"] = response.result?.gasLimit?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["gas_used"] = response.result?.gasUsed?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["hash"] = response.result?.hash?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["logs_bloom"] = response.result?.logsBloom?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["miner"] = response.result?.miner?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["mix_hash"] = response.result?.mixHash?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["number"] = response.result?.number?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["parent_hash"] =
                                                    response.result?.parentHash?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["receipts_root"] =
                                                    response.result?.receiptsRoot?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["sha3_uncles"] =
                                                    response.result?.sha3Uncles?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["size"] = response.result?.size?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["state_root"] = response.result?.stateRoot?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["timestamp"] = response.result?.timestamp?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                blockResult["total_difficulty"] =
                                                    response.result?.totalDifficulty?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil

                                                blockResult["transactions"] = ctx.tableCreate(response.result?.transactions?.size ?: 0, 0)
                                                    .also { txTable ->
                                                        response.result?.transactions?.forEach { trans ->
                                                            txTable.append(ctx.tableCreate(0, 0).also { tx ->
                                                                tx["block_hash"] = trans.blockHash?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                                tx["block_number"] = trans.blockNumber?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                                tx["from"] = trans.from.toPrefixedHexString().value.let(::KuaString)
                                                                tx["gas"] = trans.gas.toPrefixedHexString().value.let(::KuaString)
                                                                tx["gas_price"] = trans.gasPrice.toPrefixedHexString().value.let(::KuaString)
                                                                tx["max_priority_fee_per_gas"] =
                                                                    trans.maxPriorityFeePerGas?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                                tx["max_fee_per_gas"] =
                                                                    trans.maxFeePerGas?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                                tx["hash"] = trans.hash.toPrefixedHexString().value.let(::KuaString)
                                                                tx["input"] = trans.input?.value?.let(::KuaString) ?: KuaNil
                                                                tx["nonce"] = trans.nonce.toPrefixedHexString().value.let(::KuaString)
                                                                tx["to"] = trans.to?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                                tx["transaction_index"] =
                                                                    trans.transactionIndex?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                                tx["value"] = trans.value?.toPrefixedHexString()?.value?.let(::KuaString) ?: KuaNil
                                                                tx["type"] = trans.type.toPrefixedHexString().value.let(::KuaString)

                                                                tx["access_list"] = ctx.tableCreate(trans.accessList?.size ?: 0, 0).also { al ->
                                                                    trans.accessList?.forEach { item ->
                                                                        al.append(
                                                                            ctx.tableCreate(0, 0).also { ali ->
                                                                                ali["address"] = item.address.toPrefixedHexString().value.let(::KuaString)
                                                                                ali["storage_keys"] = ctx.tableCreate(item.storageKeys.size, 0).also { sk ->
                                                                                    item.storageKeys.forEach { storageKey ->
                                                                                        sk.append(storageKey.value.toPrefixedHexString().value.let(::KuaString))
                                                                                    }
                                                                                }
                                                                            }
                                                                        )
                                                                    }

                                                                }
                                                            })
                                                        }
                                                    }

                                                blockResult["transactions_root"] =
                                                    response.result?.transactionsRoot?.toPrefixedHexString()?.value?.let(::KuaString)
                                                        ?: KuaNil

                                                val withdrawals = response.result?.withdrawals
                                                blockResult["withdrawals"] = ctx.tableCreate(withdrawals?.size ?: 0, 0).also { wds ->
                                                    withdrawals?.forEach { wd ->
                                                        wds.append(ctx.tableCreate(0, 0).also { w ->
                                                            w["index"] = wd.index.toPrefixedHexString().value.let(::KuaString)
                                                            w["validator_index"] = wd.validatorIndex.toPrefixedHexString().value.let(::KuaString)
                                                            w["address"] = wd.address.toPrefixedHexString().value.let(::KuaString)
                                                            w["amount"] = wd.amount.toPrefixedHexString().value.let(::KuaString)
                                                        })
                                                    }
                                                }

                                                blockResult["withdrawals_root"] =
                                                    response.result?.withdrawalsRoot?.toPrefixedHexString()?.value?.let(::KuaString)
                                                        ?: KuaNil
                                            }
                                        }
                                    )
                                }
                            }

                            else -> TODO()
                        }
                    }
                }
            }

        } catch (t: Throwable) {
            t.printStackTrace()
            return KuaError(t.message ?: "Unknown error") to null
        }
    }
}