package io.hamal.app.proxy.cache

import io.hamal.app.proxy.domain.EthCall
import io.hamal.app.proxy.repository.*
import io.hamal.lib.common.DefaultLruCache
import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.eth.domain.EthBlock
import io.hamal.lib.web3.util.Web3Formatter
import java.math.BigInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

//FIXME single cache which stores persisted objects and convertion happens on controller level
interface EthCache {
    fun findBlock(blockId: EthUint64): EthBlock?
    fun store(block: EthBlock) // FIXME Ethblock domain object of the stripped down version
    fun store(call: EthCall)
    fun findCall(blockId: EthUint64, to: EthAddress, data: EthPrefixedHexString): EthCall?
}

class EthLruCache(
    private val proxyRepository: ProxyRepository,
    private val addressRepository: AddressRepository,
    private val blockRepository: BlockRepository,
    private val callRepository: CallRepository,
    private val transactionRepository: TransactionRepository
) : EthCache {

    override fun findBlock(blockId: EthUint64): EthBlock? {
        return blockStore.find(blockId) ?: loadBlockFromDb(blockId)
    }


    override fun store(block: EthBlock) {
        return lock.withLock {
            proxyRepository.store(block)
            blockStore[block.number] = block
            blockNumberMapping[block.hash] = block.number
        }
    }

    override fun store(call: EthCall) {
        return lock.withLock {
            proxyRepository.store(call)
        }
    }

    override fun findCall(
        blockId: EthUint64,
        to: EthAddress,
        data: EthPrefixedHexString
    ): EthCall? {
        return callRepository.find(
            blockId.value.toLong().toULong(),
            addressRepository.resolve(to),
            data.toByteArray()
        )?.let { persistedEthCall ->
            EthCall(
                blockId = EthUint64(BigInteger(persistedEthCall.blockId.toString())),
                to = EthAddress(BigInteger.ZERO),
                data = EthPrefixedHexString("0x" + Web3Formatter.formatToHex(persistedEthCall.data)),
                result = EthPrefixedHexString("0x" + Web3Formatter.formatToHex(persistedEthCall.result))
            )
        }
    }

    private fun loadBlockFromDb(number: EthUint64): EthBlock? {
//        return lock.withLock {
//            blockRepository.find(number)?.also { block ->
//                blockStore[block.number] = block
//                blockNumberMapping[block.hash] = block.number
//            }
//        }
        // FIXME
        return blockRepository.find(number.value.toLong().toULong())
            ?.let { persistedEthBlock ->
                val transactions = transactionRepository.list(persistedEthBlock.id)

                val addressIds = transactions.map { it.fromAddressId }
                    .plus(transactions.map { it.toAddressId })
                    .plus(persistedEthBlock.minerAddressId)
                    .toSet()

                val addresses = addressRepository.find(addressIds)


                EthBlock(
                    number = EthUint64(BigInteger(persistedEthBlock.id.toString())),
                    hash = EthHash(EthBytes32(persistedEthBlock.hash)),
                    parentHash = EthHash(EthBytes32(ByteArray(0))),
                    sha3Uncles = EthHash(EthBytes32(ByteArray(0))),
                    miner = addresses[persistedEthBlock.minerAddressId]!!,
                    stateRoot = EthHash(ByteArray(0)),
                    transactionsRoot = EthHash(ByteArray(0)),
                    receiptsRoot = EthHash(ByteArray(0)),
                    gasLimit = EthUint64(0),
                    gasUsed = EthUint64(BigInteger.valueOf(persistedEthBlock.gasUsed.toLong())),
                    timestamp = EthUint64(BigInteger.valueOf(persistedEthBlock.timestamp.toLong())),
                    extraData = EthBytes32(ByteArray(0)),
                    transactions = transactions.map { tx ->
                        EthBlock.Transaction(
                            type = EthUint8(tx.type),
                            hash = EthHash(EthBytes32(ByteArray(0))),
                            from = addresses[tx.fromAddressId]!!,
                            to = addresses[tx.toAddressId],
                            input = EthPrefixedHexString("0x" + Web3Formatter.formatToHex(tx.input)),
                            value = EthUint256(BigInteger(tx.value)),
                            gas = EthUint64(tx.gas.toLong()),
                            gasPrice = EthUint64(0)
                        )
                    }
                )
            }
    }

    private val blockStore = DefaultLruCache<EthUint64, EthBlock>(1000)
    private val blockNumberMapping = mutableMapOf<EthHash, EthUint64>()
    private val lock = ReentrantLock()
}