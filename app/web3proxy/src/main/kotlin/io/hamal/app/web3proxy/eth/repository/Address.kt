package io.hamal.app.web3proxy.eth.repository

import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import java.nio.file.Path

interface AddressRepository {

    fun resolve(address: EthAddress): Long

    fun resolve(addresses: List<EthAddress>): Map<EthAddress, Long>

    fun find(addressIds: Iterable<Long>): Map<Long, EthAddress>
}

class AddressRepositoryImpl(
    path: Path
) : SqliteBaseRepository(
    path = path,
    filename = "address.db"
), AddressRepository {

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS address (
                 id             INTEGER PRIMARY KEY,
                 address        TEXT(42) NOT NULL,
                 UNIQUE         (address)
            );
        """.trimIndent()
        )
    }

    override fun resolve(address: EthAddress): Long {
        return resolve(listOf(address)).entries.first().value
    }

    override fun resolve(addresses: List<EthAddress>): Map<EthAddress, Long> {
        val strings = addresses.map { it.toPrefixedHexString().value }
        val inClause = "(${strings.joinToString(",") { "'$it'" }})"

        return connection.tx {
            strings.forEach { address ->
                execute("INSERT OR IGNORE INTO addresses(address) VALUES( :address )") {
                    set("address", address)
                }
            }
            executeQuery("""SELECT * FROM addresses WHERE address in $inClause""") {
                map { rs ->
                    EthAddress(EthPrefixedHexString(rs.getString("address"))) to rs.getLong("id")
                }
            }
        }.toMap()

    }

    override fun find(addressIds: Iterable<Long>): Map<Long, EthAddress> {
        val inClause = "(${addressIds.joinToString(",") { "$it" }})"

        return connection.tx {
            executeQuery("""SELECT * FROM addresses WHERE id in $inClause""") {
                map { rs ->
                    rs.getLong("id") to EthAddress(EthPrefixedHexString(rs.getString("address")))
                }
            }
        }.toMap()
    }

    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }

    override fun clear() {
        connection.execute("DELETE FROM address")
    }
}
