package io.hamal.app.proxy.repository

import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.web3.util.Web3Encoding
import kotlinx.serialization.ExperimentalSerializationApi
import java.nio.file.Path


interface TransactionRepository {
    //    fun findTransaction(number: EthUint64): EthTransaction?
//    fun findTransaction(hash: EthHash): EthTransaction?
//    fun findTransactionHashOfTransaction(hash: EthHash): EthHash?
    fun store(transaction: PersistedEthTransaction)

    fun find(blockId: ULong, blockIndex: UShort): PersistedEthTransaction?
}

@OptIn(ExperimentalSerializationApi::class)
class SqliteTransactionRepository(
    val path: Path,
) : BaseSqliteRepository(object : Config {
    override val path = path

    // FIXME partitionise data
    override val filename: String = "new-transactions.db"
}), TransactionRepository {

    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }


    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS transactions (
                 type              TINYINT NOT NULL,
                 block_id          INTEGER NOT NULL,
                 block_index       INTEGER NOT NULL,
                 from_address_id   INTEGER NOT NULL,
                 to_address_id     INTEGER NOT NULL,
                 input             BLOB NOT NULL,
                 value             BLOB NOT NULL,
                 gas               INTEGER NOT NULL,
                 PRIMARY KEY       (block_id, block_index)
            );
        """.trimIndent()
        )
    }

    override fun clear() {
        connection.execute("DELETE FROM transactions")
        connection.execute("DELETE FROM transactions")
    }

//    override fun findTransaction(number: EthUint64): EthTransaction? {
//        TODO()
//    }
//
//    override fun findTransaction(hash: EthHash): EthTransaction? {
////        return connection.executeQueryOne("SELECT transaction FROM transactions WHERE transaction_hash = :transactionHash") {
////            query { set("transactionHash", hash) }
////            map { rs ->
////                protobuf.decodeFromByteArray<PersistedEthTransaction>(rs.getBytes("transaction").zlibDecompress()).decode()
////            }
////        }
//        TODO()
//    }
//
//    override fun findTransactionHashOfTransaction(hash: EthHash): EthHash? {
////        return connection.executeQueryOne("SELECT transaction_hash FROM transactions WHERE tx_hash = :txHash") {
////            query { set("txHash", hash) }
////            map { rs -> EthHash(EthPrefixedHexString(rs.getString("transaction_hash"))) }
////        }
//        TODO()
//    }

    override fun store(
        transaction: PersistedEthTransaction
    ) {

        connection.tx {
            execute(
                "INSERT OR IGNORE INTO transactions(type, block_id, block_index, from_address_id, to_address_id,value, input, gas) " +
                        "VALUES(:type, :blockId, :blockIndex, :fromAddressId, :toAddressId, :value, :input, :gas )"
            ) {
                set("type", transaction.type.toInt())
                set("blockId", transaction.blockId)
                set("blockIndex", transaction.blockIndex)
                set("fromAddressId", transaction.fromAddressId)
                set("toAddressId", transaction.toAddressId)
                set("value", transaction.value)
                set("input", transaction.input)
                set("gas", transaction.gas)
            }
        }
    }

    override fun find(blockId: ULong, blockIndex: UShort): PersistedEthTransaction? {
        return connection.tx {
            executeQueryOne("SELECT * FROM transactions WHERE block_id = :blockId and block_index = :blockIndex") {
                query {
                    set("blockId", blockId)
                    set("blockIndex", blockIndex)
                }
                map { rs ->
                    PersistedEthTransaction(
                        type = rs.getInt("type").toByte(),
                        blockId = rs.getLong("block_id").toULong(),
                        blockIndex = rs.getInt("block_index").toUShort(),
                        fromAddressId = rs.getLong("from_address_id").toULong(),
                        toAddressId = rs.getLong("to_address_id").toULong(),
                        input = Web3Encoding.decodeRunLength(rs.getBytes("input")),
                        value = rs.getBytes("value"),
                        gas = rs.getLong("gas").toULong()
                    )
                }
            }
        }
    }
}


data class PersistedEthTransaction(
    val type: Byte,
    val blockId: ULong,
    val blockIndex: UShort,
    val fromAddressId: ULong,
    val toAddressId: ULong,
    val input: ByteArray,
    val value: ByteArray,
    val gas: ULong,
)