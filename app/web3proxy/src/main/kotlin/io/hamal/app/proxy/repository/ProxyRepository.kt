package io.hamal.app.proxy.repository

import io.hamal.app.proxy.domain.EthCall
import io.hamal.lib.web3.eth.domain.EthBlock

interface ProxyRepository {
    fun store(block: EthBlock)
    fun store(call: EthCall)
}


class SqliteProxyRepository(
    val addressRepository: AddressRepository,
    val blockRepository: BlockRepository,
    val callRepository: CallRepository,
    val transactionRepository: TransactionRepository
) : ProxyRepository {


    override fun store(block: EthBlock) {
// FIXME locking?
        val resolvedAddresses = addressRepository.resolve(
            listOf(block.miner)
                .plus(block.transactions.map { it.from })
                .plus(block.transactions.mapNotNull { it.to })
        ).toMap()


        blockRepository.store(
            PersistedEthBlock(
                number = block.number.value.toLong().toULong(),
                hash = block.hash.toByteArray(),
                parentHash = block.parentHash.toByteArray(),
                sha3Uncles = block.sha3Uncles.toByteArray(),
                minerAddressId = resolvedAddresses[block.miner]!!,
                stateRoot = block.stateRoot.toByteArray(),
                transactionsRoot = block.transactionsRoot.toByteArray(),
                receiptsRoot = block.receiptsRoot.toByteArray(),
                gasLimit = block.gasLimit.value.toLong().toULong(),
                gasUsed = block.gasUsed.value.toLong().toULong(),
                timestamp = block.timestamp.value.toLong().toULong(),
//                extraData = block.extraData.toByteArray()
                extraData = byteArrayOf()
            )
        )

        block.transactions.forEachIndexed { index, tx ->
////            val encoded = Web3Encoding.encodeRunLength(tx.input)
////            val input = if (encoded.size > 2_000) {
////                ByteArray(0)
////            } else {
////                encoded
//            }

            transactionRepository.store(
                PersistedEthTransaction(
                    type = tx.type.value.toByte(),
                    blockId = block.number.value.toLong().toULong(),
                    blockIndex = index.toUShort(),
                    fromAddressId = resolvedAddresses[tx.from]!!,
                    toAddressId = tx.to?.let { resolvedAddresses[it] } ?: 0UL,
                    input = tx.input.toByteArray(),
                    value = tx.value.value.toByteArray(),
                    gas = tx.gas.value.toLong().toULong(),
                )
            )
        }

    }

    override fun store(call: EthCall) {
        callRepository.store(
            PersistedEthCall(
                blockId = call.blockId.value.toLong().toULong(),
                toAddressId = addressRepository.resolve(listOf(call.to)).values.first(),
                data = call.data.toByteArray(),
                result = call.result.toByteArray()
            )
        )
    }
}