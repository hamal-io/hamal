package io.hamal.app.web3proxy.arbitrum.repository

import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmUint32
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumBlockData
import io.hamal.lib.web3.evm.impl.arbitrum.domain.ArbitrumTransactionData
import io.hamal.lib.web3.evm.impl.arbitrum.http.ArbitrumBatchService
import java.nio.file.Path

interface ArbitrumRepository {

    fun listBlocks(blockNumbers: List<EvmUint64>): List<ArbitrumBlockData?>

    fun clear()
}

class ArbitrumRepositoryImpl(
    path: Path,
    batchService: ArbitrumBatchService<*>,
) : ArbitrumRepository {

    override fun listBlocks(blockNumbers: List<EvmUint64>): List<ArbitrumBlockData?> {
        val blocks = blockRepository.list(blockNumbers)
        val addresses = addressRepository.list(collectArbitrumAddressIds(blocks.filterNotNull()))
        return blocks.map { block -> block?.toObject(addresses) }
    }

    override fun clear() {
        addressRepository.clear()
        blockRepository.clear()
    }

    private val addressRepository: ArbitrumAddressRepository = ArbitrumAddressRepositoryImpl(path)
    private val indexRepository: ArbitrumIndexRepository = ArbitrumIndexRepositoryImpl(path)
    private val blockRepository: ArbitrumBlockRepository = ArbitrumBlockRepositoryImpl(path, batchService, addressRepository, indexRepository)
}

private fun collectArbitrumAddressIds(blocks: List<BlockEntity>): Set<ArbitrumAddressId> {
    return blocks.flatMap { block ->
        listOf(block.miner)
            .plus(block.transactions.flatMap { transaction ->
                listOfNotNull(
                    transaction.from,
                    transaction.to
                )
            }
            )
    }.toSet()
}

private fun BlockEntity.toObject(addresses: Map<ArbitrumAddressId, EvmAddress>) = ArbitrumBlockData(
    baseFeePerGas = baseFeePerGas,
    extraData = extraData,
    gasLimit = gasLimit,
    gasUsed = gasUsed,
    hash = hash,
    l1BlockNumber = l1BlockNumber,
    logsBloom = logsBloom,
    miner = addresses[miner]!!,
    mixHash = mixHash,
    number = number,
    sendCount = sendCount,
    sendRoot = sendRoot,
    parentHash = parentHash,
    receiptsRoot = receiptsRoot,
    sha3Uncles = sha3Uncles,
    size = size,
    stateRoot = stateRoot,
    timestamp = timestamp,
    totalDifficulty = totalDifficulty,
    transactions = transactions.mapIndexed { index, tx ->
        ArbitrumTransactionData(
            blockHash = hash,
            blockNumber = number,
            from = tx.from.let { addresses[it]!! },
            gas = tx.gas,
            gasPrice = tx.gasPrice,
            hash = tx.hash,
            input = tx.input,
            nonce = tx.nonce,
            to = tx.to?.let { addresses[it] },
            value = tx.value,
            type = tx.type,
            transactionIndex = EvmUint32(index),
            requestId = tx.requestId
        )
    },
    transactionsRoot = transactionsRoot
)
