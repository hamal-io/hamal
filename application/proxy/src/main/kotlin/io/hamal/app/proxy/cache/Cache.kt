package io.hamal.app.proxy.cache

import io.hamal.app.proxy.repository.BlockRepository
import io.hamal.lib.common.DefaultLruCache
import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthBlock
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface Cache {
    fun findBlock(number: EthUint64): EthBlock?
    fun findBlock(hash: EthHash): EthBlock?
    fun store(block: EthBlock)
}

class LruCache(
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

    override fun store(block: EthBlock) {
        return lock.withLock {
            blockRepository.store(block)
            blockStore[block.number] = block
            blockNumberMapping[block.hash] = block.number
        }
    }

    private fun loadBlockFromDb(number: EthUint64): EthBlock? {
        return lock.withLock {
            blockRepository.findBlock(number)?.also { block ->
                blockStore[block.number] = block
                blockNumberMapping[block.hash] = block.number
            }
        }
    }

    private fun loadBlockFromDb(hash: EthHash): EthBlock? {
        return lock.withLock {
            blockRepository.findBlock(hash)?.also { block ->
                blockStore[block.number] = block
                blockNumberMapping[block.hash] = block.number
            }
        }
    }


    private val blockStore = DefaultLruCache<EthUint64, EthBlock>(100_000)
    private val blockNumberMapping = mutableMapOf<EthHash, EthUint64>()
    private val lock = ReentrantLock()
}