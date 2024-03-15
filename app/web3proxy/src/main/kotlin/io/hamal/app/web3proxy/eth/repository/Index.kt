package io.hamal.app.web3proxy.eth.repository

import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.lib.sqlite.Transaction
import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthUint64
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
                hash            TEXT(64) PRIMARY KEY,
                block_number    INTEGER NOT NULL
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


private fun Transaction.insertTransactions(blockNumber: EthUint64, txHashes: List<EthHash>) {
    txHashes.forEach { txHash ->
        execute("INSERT OR IGNORE INTO index_transaction(hash, block_number) VALUES( :hash, :blockNumber )") {
            set("hash", txHash.toPrefixedHexString().value)
            set("blockNumber", blockNumber.value)
        }
    }
}

private fun extractTransactionHashes(block: BlockEntity): List<EthHash> {
    return block.transactions.map { it.hash }
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