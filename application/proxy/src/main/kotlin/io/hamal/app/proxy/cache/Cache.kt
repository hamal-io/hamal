package io.hamal.app.proxy.cache

import io.hamal.app.proxy.repository.BlockRepository
import io.hamal.app.proxy.repository.ProxyRepository
import io.hamal.lib.common.DefaultLruCache
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthBytes32
import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthBlock
import io.hamal.lib.web3.eth.domain.EthReceipt
import java.math.BigInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface Cache {
    fun findBlock(number: EthUint64): EthBlock?
    fun findBlock(hash: EthHash): EthBlock?
    fun findReceipt(hash: EthHash): EthReceipt?
    fun store(block: EthBlock)
    fun store(receipt: EthReceipt)
}

class LruCache(
    private val proxyRepository: ProxyRepository,
    private val blockRepository: BlockRepository
) : Cache {

    override fun findBlock(number: EthUint64): EthBlock? {
        return blockStore.find(number) ?: loadBlockFromDb(number)
    }

    override fun findBlock(hash: EthHash): EthBlock? {
        return when (val number = blockNumberMapping[hash]) {
            is EthUint64 -> findBlock(number)
            else -> loadBlockFromDb(hash)
        }
    }

    override fun findReceipt(hash: EthHash): EthReceipt? {
        TODO("Not yet implemented")
    }

    override fun store(block: EthBlock) {
        return lock.withLock {
            proxyRepository.store(block)
            blockStore[block.number] = block
            blockNumberMapping[block.hash] = block.number
        }
    }

    override fun store(receipt: EthReceipt) {
        TODO()
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
                EthBlock(
                    number = EthUint64(BigInteger(persistedEthBlock.id.toString())),
                    hash = EthHash(EthBytes32(persistedEthBlock.hash)),
                    parentHash = EthHash(EthBytes32(ByteArray(0))),
                    sha3Uncles = EthHash(EthBytes32(ByteArray(0))),
                    miner = EthAddress(BigInteger.ONE),
                    stateRoot = EthHash(ByteArray(0)),
                    transactionsRoot = EthHash(ByteArray(0)),
                    receiptsRoot = EthHash(ByteArray(0)),
                    gasLimit = EthUint64(0),
                    gasUsed = EthUint64(0),
                    timestamp = EthUint64(0),
                    extraData = EthBytes32(ByteArray(0)),
                    transactions = listOf()
                )
            }
    }

    private fun loadBlockFromDb(hash: EthHash): EthBlock? {
        TODO()
//        return lock.withLock {
//            blockRepository.findBlock(hash)?.also { block ->
////                blockStore[block.number] = block
////                blockNumberMapping[block.hash] = block.number
//            }
//        }
    }


    private val blockStore = DefaultLruCache<EthUint64, EthBlock>(1000)
    private val blockNumberMapping = mutableMapOf<EthHash, EthUint64>()
    private val lock = ReentrantLock()
}