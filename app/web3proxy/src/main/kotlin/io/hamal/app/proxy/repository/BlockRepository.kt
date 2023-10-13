package io.hamal.app.proxy.repository

import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import java.nio.file.Path

interface BlockRepository {
    fun store(block: PersistedEthBlock)
    fun find(blockId: ULong): PersistedEthBlock?
}

class SqliteBlockRepository(
    val path: Path,
) : SqliteBaseRepository(object : Config {
    override val path = path
    override val filename: String = "blocks.db"
}), BlockRepository {

    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS blocks (
                 number             INTEGER PRIMARY KEY,
                 hash               BLOB NOT NULL,
                 parent_hash        BLOB NOT NULL,
                 sha3_uncles        BLOB NOT NULL,
                 miner_address_id   INTEGER NOT NULL,
                 state_root         BLOB NOT NULL,
                 transactions_root  BLOB NOT NULL,
                 receipts_root      BLOB NOT NULL,
                 gas_limit          INTEGER NOT NULL,
                 gas_used           INTEGER NOT NULL,
                 timestamp          INTEGER NOT NULL,
                 extra_data         BLOB NOT NULL,
                 UNIQUE             (hash)
            );
        """.trimIndent()
        )

    }

    override fun clear() {
        connection.execute("DELETE FROM blocks")
    }

    override fun store(
        block: PersistedEthBlock
    ) {

        connection.tx {
            execute(
                "INSERT OR IGNORE INTO blocks(number, hash, parent_hash, sha3_uncles,  miner_address_id,  " +
                        "state_root, transactions_root, receipts_root, gas_limit, gas_used, timestamp, extra_data) " +
                        "VALUES( :number, :hash,:parentHash, :sha3Uncles, :minerAddressId," +
                        ":stateRoot, :transactionsRoot, :receiptsRoot, :gasLimit, :gasUsed, :timestamp, :extraData )"
            ) {
                set("number", block.number)
                set("hash", block.hash)
                set("parentHash", block.parentHash)
                set("sha3Uncles", block.sha3Uncles)
                set("minerAddressId", block.minerAddressId)
                set("stateRoot", block.stateRoot)
                set("transactionsRoot", block.transactionsRoot)
                set("receiptsRoot", block.receiptsRoot)
                set("gasLimit", block.gasLimit)
                set("gasUsed", block.gasUsed)
                set("timestamp", block.timestamp)
                set("extraData", block.extraData)
            }
        }
    }

    override fun find(blockId: ULong): PersistedEthBlock? {
        return connection.tx {
            executeQueryOne("SELECT * FROM blocks WHERE number = :blockId") {
                query {
                    set("blockId", blockId)
                }
                map { rs ->
                    PersistedEthBlock(
                        number = rs.getLong("number").toULong(),
                        hash = rs.getBytes("hash"),
                        parentHash = rs.getBytes("parent_hash"),
                        sha3Uncles = rs.getBytes("sha3_uncles"),
                        minerAddressId = rs.getLong("miner_address_id").toULong(),
                        stateRoot = rs.getBytes("state_root"),
                        transactionsRoot = rs.getBytes("transactions_root"),
                        receiptsRoot = rs.getBytes("receipts_root"),
                        gasLimit = rs.getLong("gas_limit").toULong(),
                        gasUsed = rs.getLong("gas_used").toULong(),
                        timestamp = rs.getLong("timestamp").toULong(),
                        extraData = rs.getBytes("extra_data")
                    )
                }
            }
        }
    }
}


data class PersistedEthBlock(
    val number: ULong,
    val hash: ByteArray,
    val parentHash: ByteArray,
    val sha3Uncles: ByteArray,
    val minerAddressId: ULong,
    val stateRoot: ByteArray,
    val transactionsRoot: ByteArray,
    val receiptsRoot: ByteArray,
    val gasLimit: ULong,
    val gasUsed: ULong,
    val timestamp: ULong,
    val extraData: ByteArray,
)