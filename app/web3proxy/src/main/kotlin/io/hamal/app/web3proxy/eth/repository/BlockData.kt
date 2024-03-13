package io.hamal.app.web3proxy.eth.repository

import io.hamal.app.web3proxy.json
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.lib.web3.eth.EthBatchService
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthBlock
import io.hamal.lib.web3.eth.domain.EthGetBlockResponse
import java.nio.file.Path

interface EthBlockDataRepository {
    fun list(blockNumbers: List<EthUint64>): List<EthBlock?>
}

class EthBlockDataRepositoryImpl(
    path: Path,
    private val ethBatchService: EthBatchService<*>
) : SqliteBaseRepository(
    path = path,
    filename = "block.db"
), EthBlockDataRepository {

    override fun list(blockNumbers: List<EthUint64>): List<EthBlock?> {
        val foundBlocks = blockNumbers.mapNotNull { number -> findBlock(number) }

        val foundBlockNumbers = foundBlocks.map { it.number }.toSet()

        val blockNumbersToDownload = blockNumbers.filterNot { it in foundBlockNumbers }

        if (blockNumbersToDownload.isEmpty()) {
            return foundBlocks
        }

        log.info("Download blocks [${blockNumbersToDownload.map { it.toPrefixedHexString() }.joinToString(" ")}]")
        blockNumbersToDownload.forEach(ethBatchService::getBlock)

        val result = ethBatchService
            .execute()
            .filterIsInstance<EthGetBlockResponse>()
            .mapNotNull { it.result }
            .also { insertBlocks(it) }
            .plus(foundBlocks)
            .associateBy { it.number }

        return blockNumbers.map { number ->
            result[number]
        }
    }

    private fun findBlock(blockNumber: EthUint64): EthBlock? {
        return connection.tx {
            executeQueryOne("SELECT data FROM block_data WHERE number = :number") {
                query {
                    set("number", blockNumber.value)
                }
                map { rs ->
                    json.decompressAndDeserialize(EthBlock::class, rs.getBytes("data"))
                }
            }
        }
    }

    private fun insertBlocks(blocks: List<EthBlock>) {
        connection.tx {
            blocks.forEach { block ->
                execute("INSERT OR REPLACE INTO block_data(number, hash, data) VALUES( :number, :hash, :data )") {
                    set("number", block.number.value)
                    set("hash", block.hash!!.toPrefixedHexString().value)
                    set("data", json.serializeAndCompress(block))
                }
            }
        }
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
            CREATE TABLE IF NOT EXISTS block_data (
                 number      INTEGER PRIMARY KEY,
                 hash        VARCHAR(64) NOT NULL,
                 data        BLOB NOT NULL,
                 UNIQUE      (hash)
            );
        """.trimIndent()
        )
    }

    override fun clear() {
        connection.execute("DELETE FROM block_data")
    }
}