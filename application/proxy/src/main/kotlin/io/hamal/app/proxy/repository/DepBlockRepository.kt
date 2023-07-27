package io.hamal.app.proxy.repository

import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.web3.eth.abi.type.*
import io.hamal.lib.web3.eth.domain.EthBlock
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.protobuf.ProtoBuf
import java.math.BigInteger
import java.nio.file.Path

interface DepBlockRepository {
    fun findBlock(number: EthUint64): EthBlock?
    fun findBlock(hash: EthHash): EthBlock?
    fun findBlockHashOfTransaction(hash: EthHash): EthHash?
    fun store(block: EthBlock)
}

@OptIn(ExperimentalSerializationApi::class)
class DepSqliteBlockRepository(
    val path: Path,
    private val protobuf: ProtoBuf
) : BaseSqliteRepository(object : Config {
    override val path = path
    override val filename: String = "blocks.db"
}), DepBlockRepository {

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

        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS transactions (
                 tx_hash        BLOB NOT NULL,
                 block_hash     BLOB NOT NULL,
                 PRIMARY KEY    (tx_hash)
            );
        """.trimIndent()
        )
    }

    override fun clear() {
        connection.execute("DELETE FROM blocks")
        connection.execute("DELETE FROM transactions")
    }

    override fun findBlock(number: EthUint64): EthBlock? {
        return connection.executeQueryOne("SELECT block FROM blocks WHERE block_number = :blockNumber") {
            query { set("blockNumber", number) }
            map { rs ->
                protobuf.decodeFromByteArray<DepPersistedEthBlock>(rs.getBytes("block").zlibDecompress()).decode()
            }
        }
    }

    override fun findBlock(hash: EthHash): EthBlock? {
        return connection.executeQueryOne("SELECT block FROM blocks WHERE block_hash = :blockHash") {
            query { set("blockHash", hash) }
            map { rs ->
                protobuf.decodeFromByteArray<DepPersistedEthBlock>(rs.getBytes("block").zlibDecompress()).decode()
            }
        }
    }

    override fun findBlockHashOfTransaction(hash: EthHash): EthHash? {
        return connection.executeQueryOne("SELECT block_hash FROM transactions WHERE tx_hash = :txHash") {
            query { set("txHash", hash) }
            map { rs -> EthHash(EthPrefixedHexString(rs.getString("block_hash"))) }
        }
    }

    override fun store(block: EthBlock) {
        connection.tx {
            execute("INSERT INTO blocks(block_number, block_hash, block) VALUES( :blockNumber, :blockHash, :block )") {
                set("blockNumber", block.number)
                set("blockHash", block.hash)
                set("block", protobuf.encodeToByteArray<DepPersistedEthBlock>(block.encode()).zlibCompress())
            }

            block.transactions.forEach { transaction ->
                execute("INSERT INTO transactions(tx_hash, block_hash) VALUES( :txHash, :blockHash)") {
                    set("txHash", transaction.hash)
                    set("blockHash", block.hash)
                }
            }
        }
    }
}

internal fun EthBlock.encode(): DepPersistedEthBlock {
    return DepPersistedEthBlock(
        number = number.value.toLong(),
        hash = hash.value.toByteArray(),
        parentHash = parentHash.value.toByteArray(),
        sha3Uncles = sha3Uncles.toByteArray(),
        miner = miner.value.value.toByteArray(),
        stateRoot = stateRoot.toByteArray(),
        transactionsRoot = transactionsRoot.toByteArray(),
        receiptsRoot = receiptsRoot.toByteArray(),
        gasLimit = gasLimit.value.toLong(),
        gasUsed = gasUsed.value.toLong(),
        timestamp = timestamp.value.toLong(),
        extraData = extraData.toByteArray(),
        transactions = transactions.map(EthBlock.Transaction::encode)
    )
}

internal fun EthBlock.Transaction.encode(): DepPersistedEthBlock.DepTransaction {
    return DepPersistedEthBlock.DepTransaction(
        type = type.value.toByte(),
        hash = hash.value.toByteArray(),
        from = from.value.value.toByteArray(),
        to = to?.value?.value?.toByteArray() ?: ByteArray(0),
//        input = input.value.toByteArray(),
        value = value.toByteArray(),
        gas = gas.value.toLong(),
        gasPrice = gasPrice.value.toLong(),
    )
}

internal fun DepPersistedEthBlock.decode() = EthBlock(
    number = EthUint64(number),
    hash = EthHash(hash),
    parentHash = EthHash(parentHash),
    sha3Uncles = EthHash(sha3Uncles),
    miner = EthAddress(BigInteger(miner)),
    stateRoot = EthHash(stateRoot),
    transactionsRoot = EthHash(transactionsRoot),
    receiptsRoot = EthHash(receiptsRoot),
    gasLimit = EthUint64(gasLimit),
    gasUsed = EthUint64(gasUsed),
    timestamp = EthUint64(timestamp),
    extraData = EthBytes32(extraData),
    transactions = transactions.map(DepPersistedEthBlock.DepTransaction::decode)
)

internal fun DepPersistedEthBlock.DepTransaction.decode() = EthBlock.Transaction(
    type = EthUint8(type),
    hash = EthHash(hash),
    from = EthAddress(BigInteger(from)),
    to = if (to.isEmpty()) null else EthAddress(BigInteger(to)),
    input = EthPrefixedHexString(ByteArray(32)),
    value = EthUint256(BigInteger(value)),
    gas = EthUint64(gas),
    gasPrice = EthUint64(gasPrice),
)

@Serializable
internal data class DepPersistedEthBlock(
    val number: Long,
    val hash: ByteArray,
    val parentHash: ByteArray,
    val sha3Uncles: ByteArray,
    val miner: ByteArray,
    val stateRoot: ByteArray,
    val transactionsRoot: ByteArray,
    val receiptsRoot: ByteArray,
    val gasLimit: Long,
    val gasUsed: Long,
    val timestamp: Long,
    val extraData: ByteArray,
    val transactions: List<DepTransaction>
) {
    @Serializable
    data class DepTransaction(
        val type: Byte,
        val hash: ByteArray,
        val from: ByteArray,
        val to: ByteArray,
//        val input: ByteArray,
        val value: ByteArray,
        val gas: Long,
        val gasPrice: Long,
    )
}



