package io.hamal.app.proxy.repository

import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.eth.domain.EthBlock
import io.hamal.lib.web3.util.Web3Formatter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.math.BigInteger
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
            map { rs -> protobuf.decodeFromByteArray<PersistedEthBlock>(rs.getBytes("block")).decode() }
        }
    }

    override fun findBlock(hash: EthHash): EthBlock? {
        return connection.executeQueryOne("SELECT block FROM blocks WHERE block_hash = :blockHash") {
            query { set("blockHash", hash) }
            map { rs -> protobuf.decodeFromByteArray<PersistedEthBlock>(rs.getBytes("block")).decode() }
        }
    }

    override fun store(block: EthBlock) {
        connection.tx {
            execute("INSERT INTO blocks(block_number, block_hash, block) VALUES( :blockNumber, :blockHash, :block )") {
                set("blockNumber", block.number)
                set("blockHash", block.hash)
                set("block", protobuf.encodeToByteArray<PersistedEthBlock>(block.encode()))
            }
        }
    }
}

internal fun EthBlock.encode(): PersistedEthBlock {
    return PersistedEthBlock(
        number = number.value.toLong(),
        hash = hash.value.toByteArray(),
        parentHash = parentHash.value.toByteArray(),
        miner = miner.toByteArray(),
        timestamp = timestamp.value.toLong(),
    )
}

internal fun PersistedEthBlock.decode() = EthBlock(
    number = EthUint64(BigInteger.valueOf(number)),
    hash = EthHash(EthBytes32(hash)),
    parentHash = EthHash(EthBytes32(parentHash)),
    miner = EthAddress(EthPrefixedHexString("0x${Web3Formatter.formatToHex(miner)}")),
    timestamp = EthUint64(BigInteger.valueOf(timestamp))
)

@Serializable
internal data class PersistedEthBlock(
    val number: Long,
    val hash: ByteArray,
    val parentHash: ByteArray,
    val miner: ByteArray,
    val timestamp: Long
)
