package io.hamal.app.web3proxy.arbitrum.repository

import io.hamal.lib.common.serialization.Serde
import io.hamal.lib.common.value.SerdeModuleJsonValue
import io.hamal.lib.domain.vo.SerdeModuleJsonValueVariable
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.lib.web3.evm.SerdeModuleJsonEvm
import io.hamal.lib.web3.evm.abi.type.EvmAddress
import io.hamal.lib.web3.evm.abi.type.EvmUint64
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumBlockData
import io.hamal.lib.web3.evm.chain.arbitrum.domain.ArbitrumGetBlockResponse
import io.hamal.lib.web3.evm.chain.arbitrum.http.ArbitrumBatchService
import java.nio.file.Path

internal interface ArbitrumBlockRepository {
    fun list(blockNumbers: List<EvmUint64>): List<BlockEntity?>

    fun clear()
}

internal class ArbitrumBlockRepositoryImpl(
    path: Path,
    private val batchService: ArbitrumBatchService<*>,
    private val addressRepository: ArbitrumAddressRepository,
    private val indexRepository: ArbitrumIndexRepository
) : SqliteBaseRepository(
    path = path,
    filename = "block.db"
), ArbitrumBlockRepository {

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
            .filterIsInstance<ArbitrumGetBlockResponse>()
            .mapNotNull { it.result }


        val addresses = addressRepository.resolve(collectArbitrumAddresses(blocks))
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
                map { rs -> serde.decompressAndRead(BlockEntity::class, rs.getBytes("data")) }
            }
        }
    }

    private fun insertBlocks(blocks: List<BlockEntity>) {
        connection.tx {
            blocks.forEach { block ->
                execute("INSERT OR REPLACE INTO block(number, hash, data) VALUES( :number, :hash, :data )") {
                    set("number", block.number.value)
                    set("hash", block.hash.toPrefixedHexString().value)
                    set("data", serde.writeAndCompress(block))
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

    private val serde = Serde.json()
        .register(SerdeModuleJsonEvm)
        .register(SerdeModuleJsonValue)
        .register(SerdeModuleJsonValueVariable)
}

private fun collectArbitrumAddresses(blocks: List<ArbitrumBlockData>): Set<EvmAddress> {
    return blocks.flatMap { block ->
        listOf(block.miner)
            .plus(block.transactions.flatMap { transaction ->
                listOfNotNull(transaction.from, transaction.to)
            })
    }.toSet()
}


private fun ArbitrumBlockData.toEntity(addresses: Map<EvmAddress, ArbitrumAddressId>) = BlockEntity(
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
    transactions = transactions.map { tx ->
        TransactionEntity(
            from = tx.from.let { addresses[it]!! },
            gas = tx.gas,
            gasPrice = tx.gasPrice,
            hash = tx.hash,
            input = tx.input,
            nonce = tx.nonce,
            to = tx.to?.let { addresses[it] },
            value = tx.value,
            type = tx.type,
            requestId = tx.requestId
        )
    },
    transactionsRoot = transactionsRoot,
)