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
                 id               INTEGER PRIMARY KEY,
                 hash             BLOB NOT NULL,
                 miner_address_id INTEGER NOT NULL,
                 gas_used         INTEGER NOT NULL,
                 timestamp        INTEGER NOT NULL,
                 UNIQUE           (hash)
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
                "INSERT OR IGNORE INTO blocks(id, hash, miner_address_id,  gas_used, timestamp) " +
                        "VALUES( :id, :hash, :minerAddressId, :gasUsed, :timestamp )"
            ) {
                set("id", block.id)
                set("hash", block.hash)
                set("minerAddressId", block.minerAddressId)
                set("gasUsed", block.gasUsed)
                set("timestamp", block.timestamp)
            }
        }
    }

    override fun find(blockId: ULong): PersistedEthBlock? {
        return connection.tx {
            executeQueryOne("SELECT * FROM blocks WHERE id = :blockId") {
                query {
                    set("blockId", blockId)
                }
                map { rs ->
                    PersistedEthBlock(
                        id = rs.getLong("id").toULong(),
                        hash = rs.getBytes("hash"),
                        minerAddressId = rs.getLong("miner_address_id").toULong(),
                        gasUsed = rs.getLong("gas_used").toULong(),
                        timestamp = rs.getLong("timestamp").toULong()
                    )
                }
            }
        }
    }
}


data class PersistedEthBlock(
    val id: ULong,
    val hash: ByteArray,
    val minerAddressId: ULong,
    val gasUsed: ULong,
    val timestamp: ULong
)