package io.hamal.app.web3proxy.eth.repository

import io.hamal.lib.sqlite.Connection
import io.hamal.lib.sqlite.SqliteBaseRepository
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import java.nio.file.Path

interface EthAddressRepository {

    fun resolve(address: EthAddress): EthAddressId

    fun resolve(addresses: Set<EthAddress>): Map<EthAddress, EthAddressId>

    fun list(addressIds: Iterable<EthAddressId>): Map<EthAddressId, EthAddress>

    fun clear()
}

class EthAddressRepositoryImpl(
    path: Path
) : SqliteBaseRepository(
    path = path,
    filename = "address.db"
), EthAddressRepository {


    override fun resolve(address: EthAddress): EthAddressId {
        return resolve(setOf(address)).entries.first().value
    }

    override fun resolve(addresses: Set<EthAddress>): Map<EthAddress, EthAddressId> {
        val strings = addresses.map { it.toPrefixedHexString().value }
        val inClause = "(${strings.joinToString(",") { "'$it'" }})"

        return connection.tx {
            strings.forEach { address ->
                execute("INSERT OR IGNORE INTO address(address) VALUES( :address )") {
                    set("address", address)
                }
            }
            executeQuery("""SELECT * FROM address WHERE address in $inClause""") {
                map { rs ->
                    EthAddress(EthPrefixedHexString(rs.getString("address"))) to EthAddressId(rs.getLong("id"))
                }
            }
        }.toMap()

    }

    override fun list(addressIds: Iterable<EthAddressId>): Map<EthAddressId, EthAddress> {
        val inClause = "(${addressIds.joinToString(",") { "${it.value}" }})"

        return connection.tx {
            executeQuery("""SELECT * FROM address WHERE id in $inClause""") {
                map { rs ->
                    EthAddressId(rs.getLong("id")) to EthAddress(EthPrefixedHexString(rs.getString("address")))
                }
            }
        }.toMap()
    }


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
