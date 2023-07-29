package io.hamal.app.proxy.cache

import io.hamal.app.proxy.domain.EthCall
import io.hamal.app.proxy.repository.AddressRepository
import io.hamal.app.proxy.repository.BlockRepository
import io.hamal.app.proxy.repository.CallRepository
import io.hamal.app.proxy.repository.ProxyRepository
import io.hamal.lib.common.DefaultLruCache
import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.eth.domain.EthBlock
import io.hamal.lib.web3.util.Web3Formatter
import java.math.BigInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

interface Cache {
    fun findBlock(blockId: EthUint64): EthBlock?
    fun store(block: EthBlock) // FIXME Ethblock domain object of the stripped down version
    fun store(call: EthCall)
    fun findCall(blockId: EthUint64, to: EthAddress, data: EthPrefixedHexString): EthCall?
}

class LruCache(
    private val proxyRepository: ProxyRepository,
    private val addressRepository: AddressRepository,
    private val blockRepository: BlockRepository,
    private val callRepository: CallRepository
) : Cache {

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

    private val blockStore = DefaultLruCache<EthUint64, EthBlock>(1000)
    private val blockNumberMapping = mutableMapOf<EthHash, EthUint64>()
    private val lock = ReentrantLock()
}