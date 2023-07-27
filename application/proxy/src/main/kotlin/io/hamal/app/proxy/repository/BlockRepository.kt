package io.hamal.app.proxy.repository

import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import kotlinx.serialization.ExperimentalSerializationApi
import java.nio.file.Path

interface BlockRepository {
    //    fun findBlock(number: EthUint64): EthBlock?
//    fun findBlock(hash: EthHash): EthBlock?
//    fun findBlockHashOfTransaction(hash: EthHash): EthHash?
    fun store(block: PersistedEthBlock)
}

@OptIn(ExperimentalSerializationApi::class)
class SqliteBlockRepository(
    val path: Path,
) : BaseSqliteRepository(object : Config {
    override val path = path
    override val filename: String = "new-blocks.db"
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
                 hash_id          INTEGER NOT NULL,
                 parent_hash_id   INTEGER NOT NULL,
                 miner_address_id INTEGER NOT NULL,
                 gas_used         INTEGER NOT NULL,
                 timestamp        INTEGER NOT NULL,
                 UNIQUE             (hash_id)
            );
        """.trimIndent()
        )

//        connection.execute(
//            """
//            CREATE TABLE IF NOT EXISTS transactions (
//                 tx_hash        BLOB NOT NULL,
//                 block_hash     BLOB NOT NULL,
//                 PRIMARY KEY    (tx_hash)
//            );
//        """.trimIndent()
//        )
    }

    override fun clear() {
        connection.execute("DELETE FROM blocks")
        connection.execute("DELETE FROM transactions")
    }

//    override fun findBlock(number: EthUint64): EthBlock? {
//        TODO()
//    }
//
//    override fun findBlock(hash: EthHash): EthBlock? {
////        return connection.executeQueryOne("SELECT block FROM blocks WHERE block_hash = :blockHash") {
////            query { set("blockHash", hash) }
////            map { rs ->
////                protobuf.decodeFromByteArray<PersistedEthBlock>(rs.getBytes("block").zlibDecompress()).decode()
////            }
////        }
//        TODO()
//    }
//
//    override fun findBlockHashOfTransaction(hash: EthHash): EthHash? {
////        return connection.executeQueryOne("SELECT block_hash FROM transactions WHERE tx_hash = :txHash") {
////            query { set("txHash", hash) }
////            map { rs -> EthHash(EthPrefixedHexString(rs.getString("block_hash"))) }
////        }
//        TODO()
//    }

    override fun store(
        block: PersistedEthBlock
    ) {

        connection.tx {
            execute(
                "INSERT OR IGNORE INTO blocks(id, hash_id, parent_hash_id, miner_address_id,  gas_used, timestamp) " +
                        "VALUES( :id, :hashId, :parentHashId, :minerAddressId, :gasUsed, :timestamp )"
            ) {
                set("id", block.id)
                set("hashId", block.hashId)
                set("parentHashId", block.parentHashId)
                set("minerAddressId", block.minerAddressId)
                set("gasUsed", block.gasUsed)
                set("timestamp", block.timestamp)
            }
        }
    }
}


data class PersistedEthBlock(
    val id: ULong,
    val hashId: ULong,
    val parentHashId: ULong,
    val minerAddressId: ULong,
    val gasUsed: ULong,
    val timestamp: ULong
)