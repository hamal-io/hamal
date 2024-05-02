package io.hamal.app.web3proxy.eth.repository

import io.hamal.lib.common.compress.BzipCompressor
import io.hamal.lib.common.hot.HotObjectModule
import io.hamal.lib.common.serialization.JsonFactoryBuilder
import io.hamal.lib.domain.Json
import io.hamal.lib.domain.vo.ValueVariableJsonModule
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.lib.web3.evm.EvmHotModule
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.chain.eth.domain.EthBlockData
import io.hamal.lib.web3.evm.chain.eth.domain.EthGetBlockResponse
import io.hamal.lib.web3.evm.chain.eth.http.EthBatchService
import java.nio.file.Path

internal interface EthBlockRepository {
    fun list(blockNumbers: List<EvmUint64>): List<BlockEntity?>

    fun clear()
}

internal class EthBlockRepositoryImpl(
    path: Path,
    private val batchService: EthBatchService<*>,
    private val addressRepository: EthAddressRepository,
    private val indexRepository: EthIndexRepository
) : SqliteBaseRepository(
    path = path,
    filename = "block.db"
), EthBlockRepository {

    override fun list(blockNumbers: List<EvmUint64>): List<BlockEntity?> {
        val foundBlocks = blockNumbers.mapNotNull { number -> findBlock(number) }

        val foundBlockNumbers = foundBlocks.map { it.number }.toSet()

        val blockNumbersToDownload = blockNumbers.filterNot { it in foundBlockNumbers }

        if (blockNumbersToDownload.isEmpty()) {
            return foundBlocks
        }

        log.info("Download blocks [${blockNumbersToDownload.map { it.toPrefixedHexString() }.joinToString(" ")}]")
        blockNumbersToDownload.forEach(batchService::getBlock)

        val blocks = batchService
            .execute()
            .filterIsInstance<EthGetBlockResponse>()
            .mapNotNull { it.result }


        val addresses = addressRepository.resolve(collectEthAddresses(blocks))
        val entities = blocks.map { it.toEntity(addresses) }.also { insertBlocks(it) }

        val result = entities.plus(foundBlocks).associateBy { it.number }

        return blockNumbers.map { number ->
            result[number]
        }
    }

    private fun findBlock(blockNumber: EvmUint64): BlockEntity? {
        return connection.tx {
            executeQueryOne("SELECT data FROM block WHERE number = :number") {
                query {
                    set("number", blockNumber.value)
                }
                map { rs -> json.decompressAndDeserialize(BlockEntity::class, rs.getBytes("data")) }
            }
        }
    }

    private fun insertBlocks(blocks: List<BlockEntity>) {
        connection.tx {
            blocks.forEach { block ->
                execute("INSERT OR REPLACE INTO block(number, hash, data) VALUES( :number, :hash, :data )") {
                    set("number", block.number.value)
                    set("hash", block.hash.toPrefixedHexString().value)
                    set("data", json.serializeAndCompress(block))
                }
            }
        }

        blocks.forEach { block -> indexRepository.index(block) }
    }

    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS block (
                 number      INTEGER PRIMARY KEY,
                 hash        VARCHAR(64) NOT NULL,
                 data        BLOB NOT NULL,
                 UNIQUE      (hash)
            );
        """.trimIndent()
        )
    }

    override fun clear() {
        connection.execute("DELETE FROM block")
    }

    private val json = Json(
        factory = JsonFactoryBuilder()
            .register(EvmHotModule)
            .register(HotObjectModule)
            .register(ValueVariableJsonModule),
        compressor = BzipCompressor
    )
}

private fun collectEthAddresses(blocks: List<EthBlockData>): Set<EvmAddress> {
    return blocks.flatMap { block ->
        listOf(block.miner)
            .plus(block.transactions.flatMap { transaction ->
                listOfNotNull(transaction.from, transaction.to).plus(transaction.accessList?.map { it.address }
                    ?: listOf())
            }
            ).plus(block.withdrawals?.map { it.address } ?: listOf())
    }.toSet()
}


private fun EthBlockData.toEntity(addresses: Map<EvmAddress, EthAddressId>) = BlockEntity(
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
    transactions = transactions.map { tx ->
        TransactionEntity(
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
            accessList = tx.accessList?.map { access ->
                TransactionEntity.AccessListItem(
                    address = addresses[access.address]!!,
                    storageKeys = access.storageKeys
                )
            }
        )
    },
    transactionsRoot = transactionsRoot,
    withdrawals = withdrawals?.map { withdrawal ->
        WithdrawalEntity(
            index = withdrawal.index,
            validatorIndex = withdrawal.validatorIndex,
            address = addresses[withdrawal.address]!!,
            amount = withdrawal.amount
        )
    },
    withdrawalsRoot = withdrawalsRoot
)