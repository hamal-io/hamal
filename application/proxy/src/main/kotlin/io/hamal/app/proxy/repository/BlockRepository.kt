package io.hamal.app.proxy.repository

import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthBlock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.protobuf.ProtoBuf
import java.nio.file.Path

interface BlockRepository {
    fun findBlock(number: EthUint64): EthBlock?
    fun findBlock(hash: EthHash): EthBlock?
    fun store(block: EthBlock)
}

@OptIn(ExperimentalSerializationApi::class)
class SqliteBlockRepository(
    val path: Path,
    private val protobuf: ProtoBuf
) : BaseSqliteRepository(object : Config {
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
                 block_number   NUMERIC NOT NULL,
                 block_hash     BLOB NOT NULL,
                 block          BLOB NOT NULL,
                 PRIMARY KEY    (block_hash),
                 UNIQUE         (block_number)
            );
        """.trimIndent()
        )
    }

    override fun clear() {
        connection.execute("DELETE FROM blocks")
    }


    override fun findBlock(number: EthUint64): EthBlock? {
        return connection.executeQueryOne("SELECT block FROM blocks WHERE block_number = :blockNumber") {
            query { set("blockNumber", number) }
            map { rs -> protobuf.decodeFromByteArray(EthBlock.serializer(), rs.getBytes("block")) }
        }
    }

    override fun findBlock(hash: EthHash): EthBlock? {
        return connection.executeQueryOne("SELECT block FROM blocks WHERE block_hash = :blockHash") {
            query { set("blockHash", hash) }
            map { rs -> protobuf.decodeFromByteArray(EthBlock.serializer(), rs.getBytes("block")) }
        }
    }

    override fun store(block: EthBlock) {
        connection.tx {
            execute("INSERT INTO blocks(block_number, block_hash, block) VALUES( :blockNumber, :blockHash, :block )") {
                set("blockNumber", block.number)
                set("blockHash", block.hash)
                set("block", protobuf.encodeToByteArray(EthBlock.serializer(), block))
            }
        }
    }
}