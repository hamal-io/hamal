package io.hamal.app.web3proxy.eth.repository

import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.lib.sqlite.Transaction
import io.hamal.lib.web3.eth.abi.type.EthUint64
import java.math.BigInteger
import java.nio.file.Path

internal interface EthIndexRepository {
    fun index(block: BlockEntity)
}

internal class EthIndexRepositoryImpl(
    path: Path
) : SqliteBaseRepository(
    path = path,
    filename = "index.db"
), EthIndexRepository {

    override fun index(block: BlockEntity) {
        val txHashes = extractTransactionHashes(block)
        val addressesWithMode = extractAddresses(block)
        connection.tx {
            insertTransactions(block.number, txHashes)
            insertAddresses(block.number, addressesWithMode)
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
            CREATE TABLE IF NOT EXISTS index_transaction (
                hash_1          INTEGER NOT NULL,
                hash_2          INTEGER NOT NULL,
                hash_3          INTEGER NOT NULL,
                hash_4          INTEGER NOT NULL,
                block_number    INTEGER NOT NULL,
                PRIMARY KEY(hash_1,hash_2,hash_3,hash_4)
            );
        """.trimIndent()
        )
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS index_address (
                address_id      INTEGER NOT NULL,
                mode            INTEGER NOT NULL,
                block_number    INTEGER NOT NULL,
                PRIMARY KEY(address_id, block_number)
            );
        """.trimIndent()
        )
    }

    override fun clear() {
        connection.execute("DELETE FROM index_transaction")
    }
}


private fun Transaction.insertTransactions(blockNumber: EthUint64, txHashes: List<Hash>) {
    txHashes.forEach { txHash ->
        execute("INSERT OR IGNORE INTO index_transaction(hash_1, hash_2, hash_3, hash_4 , block_number) VALUES( :hash1, :hash2, :hash3, :hash4 , :blockNumber )") {
            set("hash1", txHash.p1)
            set("hash2", txHash.p2)
            set("hash3", txHash.p3)
            set("hash4", txHash.p4)
            set("blockNumber", blockNumber.value)
        }
    }
}

private fun extractTransactionHashes(block: BlockEntity): List<Hash> {
    return block.transactions.map { it.hash }.map {
        val arr = it.value.value.toList()

        val p1 = arr.subList(0, 7)
        val p2 = arr.subList(8, 15)
        val p3 = arr.subList(16, 23)
        val p4 = arr.subList(24, 31)

        Hash(
            p1 = BigInteger(p1.toByteArray()).longValueExact(),
            p2 = BigInteger(p2.toByteArray()).longValueExact(),
            p3 = BigInteger(p3.toByteArray()).longValueExact(),
            p4 = BigInteger(p4.toByteArray()).longValueExact()
        )
    }

}

private fun extractAddresses(block: BlockEntity): Map<EthAddressId, Int> {
    val result = mutableMapOf<EthAddressId, Int>()
    result[block.miner] = 1

    block.transactions.forEach { tx ->
        result[tx.from] = result[tx.from]?.let { it or 2 } ?: 2
        tx.to?.also { to -> result[to] = result[to]?.let { it or 4 } ?: 4 }
        tx.accessList?.forEach { accessListItem ->
            result[accessListItem.address] = result[accessListItem.address]?.let { it or 8 } ?: 8
        }

        block.withdrawals?.forEach { withdrawal ->
            result[withdrawal.address] = result[withdrawal.address]?.let { it or 16 } ?: 16
        }
    }

    return result
}


private fun Transaction.insertAddresses(blockNumber: EthUint64, addressesWithMode: Map<EthAddressId, Int>) {
    addressesWithMode.forEach { (addressId, mode) ->
        execute(
            "INSERT INTO index_address(address_id, mode, block_number) VALUES( :addressId, :mode, :blockNumber ) " +
                    "ON CONFLICT DO UPDATE SET mode = mode + excluded.mode"
        ) {
            set("addressId", addressId.value)
            set("mode", mode)
            set("blockNumber", blockNumber.value)
        }
    }
}

private data class Hash(
    val p1: Long,
    val p2: Long,
    val p3: Long,
    val p4: Long
)