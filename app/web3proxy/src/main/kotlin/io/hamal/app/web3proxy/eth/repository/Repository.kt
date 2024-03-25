package io.hamal.app.web3proxy.eth.repository

import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmUint32
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.impl.eth.domain.EthBlock
import io.hamal.lib.web3.evm.impl.eth.domain.EthTransaction
import io.hamal.lib.web3.evm.impl.eth.domain.Withdrawal
import io.hamal.lib.web3.evm.impl.eth.http.EthBatchService
import java.nio.file.Path

interface EthRepository {

    fun listBlocks(blockNumbers: List<EvmUint64>): List<EthBlock?>

    fun clear()
}

class EthRepositoryImpl(
    path: Path,
    batchService: EthBatchService<*>,
) : EthRepository {

    override fun listBlocks(blockNumbers: List<EvmUint64>): List<EthBlock?> {
        val blocks = blockRepository.list(blockNumbers)
        val addresses = addressRepository.list(collectEthAddressIds(blocks.filterNotNull()))
        return blocks.map { block -> block?.toObject(addresses) }
    }

    override fun clear() {
        addressRepository.clear()
        blockRepository.clear()
    }

    private val addressRepository: EthAddressRepository = EthAddressRepositoryImpl(path)
    private val indexRepository: EthIndexRepository = EthIndexRepositoryImpl(path)
    private val blockRepository: EthBlockRepository = EthBlockRepositoryImpl(path, batchService, addressRepository, indexRepository)
}

private fun collectEthAddressIds(blocks: List<BlockEntity>): Set<EthAddressId> {
    return blocks.flatMap { block ->
        listOf(block.miner)
            .plus(block.transactions.flatMap { transaction ->
                listOfNotNull(
                    transaction.from,
                    transaction.to
                ).plus(transaction.accessList?.map { it.address } ?: listOf())
            }
            ).plus(block.withdrawals?.map { it.address } ?: listOf())
    }.toSet()
}

private fun BlockEntity.toObject(addresses: Map<EthAddressId, EvmAddress>) = EthBlock(
    baseFeePerGas = baseFeePerGas,
    extraData = extraData,
    gasLimit = gasLimit,
    gasUsed = gasUsed,
    hash = hash,
    logsBloom = logsBloom,
    miner = addresses[miner]!!,
    mixHash = mixHash,
    number = number,
    parentHash = parentHash,
    receiptsRoot = receiptsRoot,
    sha3Uncles = sha3Uncles,
    size = size,
    stateRoot = stateRoot,
    timestamp = timestamp,
    totalDifficulty = totalDifficulty,
    transactions = transactions.mapIndexed { index, tx ->
        EthTransaction(
            blockHash = hash,
            blockNumber = number,
            from = tx.from.let { addresses[it]!! },
            gas = tx.gas,
            gasPrice = tx.gasPrice,
            maxPriorityFeePerGas = tx.maxPriorityFeePerGas,
            maxFeePerGas = tx.maxFeePerGas,
            hash = tx.hash,
            input = tx.input,
            nonce = tx.nonce,
            to = tx.to?.let { addresses[it] },
            value = tx.value,
            type = tx.type,
            transactionIndex = EvmUint32(index),
            accessList = tx.accessList?.map { access ->
                EthTransaction.AccessListItem(
                    address = addresses[access.address]!!,
                    storageKeys = access.storageKeys
                )
            }
        )
    },
    transactionsRoot = transactionsRoot,
    withdrawals = withdrawals?.map { withdrawal ->
        Withdrawal(
            index = withdrawal.index,
            validatorIndex = withdrawal.validatorIndex,
            address = addresses[withdrawal.address]!!,
            amount = withdrawal.amount
        )
    },
    withdrawalsRoot = withdrawalsRoot
)
