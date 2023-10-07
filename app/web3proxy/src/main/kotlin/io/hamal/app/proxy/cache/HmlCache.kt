package io.hamal.app.proxy.cache

import io.hamal.app.proxy.repository.*
import io.hamal.lib.common.LruCacheImpl
import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.hml.domain.HmlBlock
import io.hamal.lib.web3.util.Web3Formatter
import java.math.BigInteger

//FIXME single cache which stores persisted objects and convertion happens on controller level
interface HmlCache {
    fun findBlock(blockId: EthUint64): HmlBlock?
}

class HmlLruCache(
    private val proxyRepository: ProxyRepository,
    private val addressRepository: AddressRepository,
    private val blockRepository: BlockRepository,
    private val callRepository: CallRepository,
    private val transactionRepository: TransactionRepository
) : HmlCache {

    override fun findBlock(blockId: EthUint64): HmlBlock? {
        return blockStore.find(blockId) ?: loadBlockFromDb(blockId)
    }


    private fun loadBlockFromDb(number: EthUint64): HmlBlock? {
        return blockRepository.find(number.value.toLong().toULong())
            ?.let { persistedEthBlock ->
                val transactions = transactionRepository.list(persistedEthBlock.id)

                val addressIds = transactions.map { it.fromAddressId }
                    .plus(transactions.map { it.toAddressId })
                    .plus(persistedEthBlock.minerAddressId)
                    .toSet()

                val addresses = addressRepository.find(addressIds)

                HmlBlock(
                    number = EthUint64(BigInteger(persistedEthBlock.id.toString())),
                    hash = EthHash(EthBytes32(persistedEthBlock.hash)),
                    miner = addresses[persistedEthBlock.minerAddressId]!!,
                    minerId = EthUint64(persistedEthBlock.minerAddressId.toLong()),
                    gasLimit = EthUint64(0),
                    gasUsed = EthUint64(BigInteger.valueOf(persistedEthBlock.gasUsed.toLong())),
                    timestamp = EthUint64(BigInteger.valueOf(persistedEthBlock.timestamp.toLong())),
                    transactions = transactions.map { tx ->
                        HmlBlock.Transaction(
                            blockIndex = EthUint32(BigInteger.valueOf(tx.blockId.toLong())),
                            type = EthUint8(tx.type),
                            from = addresses[tx.fromAddressId]!!,
                            fromId = EthUint64(tx.fromAddressId.toLong()),
                            to = addresses[tx.toAddressId],
                            toId = EthUint64(tx.toAddressId.toLong()),
                            input = EthPrefixedHexString("0x" + Web3Formatter.formatToHex(tx.input)),
                            value = EthUint256(BigInteger(tx.value)),
                            gas = EthUint64(tx.gas.toLong()),
                        )
                    }
                )
            }
    }

    private val blockStore = LruCacheImpl<EthUint64, HmlBlock>(1000)
}