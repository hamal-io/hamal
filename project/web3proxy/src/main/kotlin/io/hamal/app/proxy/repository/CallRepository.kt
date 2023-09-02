package io.hamal.app.proxy.repository

import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.web3.util.Web3Encoding
import java.nio.file.Path


interface CallRepository {
    fun store(call: PersistedEthCall)

    fun find(blockId: ULong, toAddressId: ULong, data: ByteArray): PersistedEthCall?
}

class SqliteCallRepository(
    path: Path
) : CallRepository {

    private val partitions = (0..9)
        .map { partition -> SqliteCallPartitionRepository(path, partition) }
        .associateBy { it.partition }

    override fun store(call: PersistedEthCall) {
        partitions[resolvePartition(call.toAddressId)]!!.store(call)
    }

    override fun find(blockId: ULong, toAddressId: ULong, data: ByteArray): PersistedEthCall? {
        return partitions[resolvePartition(toAddressId)]!!.find(blockId, toAddressId, data)
    }

    private fun resolvePartition(toAddressId: ULong) = (toAddressId % 10UL).toInt()
}

class SqliteCallPartitionRepository(
    val path: Path,
    val partition: Int
) : BaseSqliteRepository(object : Config {
    override val path = path
    override val filename: String = "calls-${partition}.db"
}), CallRepository {

    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }


    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS calls (
                 block_id           INTEGER NOT NULL,
                 to_address_id      INTEGER NOT NULL,
                 data               BLOB NOT NULL,
                 data_hash          INTEGER NOT NULL,
                 result             BLOB NOT NULL,
                 PRIMARY KEY        (block_id, to_address_id, data_hash)
            );
        """.trimIndent()
        )
    }

    override fun clear() {
        connection.execute("DELETE FROM calls")
    }

    override fun store(
        call: PersistedEthCall
    ) {

        connection.tx {
            execute(
                "INSERT OR IGNORE INTO calls(block_id, to_address_id, data, data_hash, result) " +
                        "VALUES(:blockId, :toAddressId, :data, :dataHash, :result )"
            ) {
                set("blockId", call.blockId)
                set("toAddressId", call.toAddressId)
                set("data", Web3Encoding.encodeRunLength(call.data))
                set("dataHash", call.dataHash)
                set("result", Web3Encoding.encodeRunLength(call.result))
            }
        }
    }

    override fun find(blockId: ULong, toAddressId: ULong, data: ByteArray): PersistedEthCall? {
        return connection.tx {
            executeQueryOne("SELECT * FROM calls WHERE block_id = :blockId and to_address_id = :toAddressId and data_hash = :dataHash") {
                query {
                    set("blockId", blockId)
                    set("toAddressId", toAddressId)
                    set("dataHash", data.contentHashCode())
                }
                map { rs ->
                    PersistedEthCall(
                        blockId = rs.getLong("block_id").toULong(),
                        toAddressId = rs.getLong("to_address_id").toULong(),
                        data = Web3Encoding.decodeRunLength(rs.getBytes("data")),
                        result = Web3Encoding.decodeRunLength(rs.getBytes("result")),
                    )
                }
            }
        }
    }
}


data class PersistedEthCall(
    val blockId: ULong,
    val toAddressId: ULong,
    val data: ByteArray,
    val result: ByteArray,
    val dataHash: Int = data.contentHashCode()
)

