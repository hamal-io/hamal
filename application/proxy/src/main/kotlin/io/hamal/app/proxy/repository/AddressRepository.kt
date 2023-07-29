package io.hamal.app.proxy.repository

import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import java.nio.file.Path

// FIXME have interface
// FIXME renmae to sqlite address repo
class AddressRepository(
    path: Path
) : BaseSqliteRepository(object : Config {
    override val path = path
    override val filename: String = "addresses.db"
}) {


    override fun setupConnection(connection: Connection) {
        connection.execute("""PRAGMA journal_mode = wal;""")
        connection.execute("""PRAGMA locking_mode = exclusive;""")
        connection.execute("""PRAGMA temp_store = memory;""")
        connection.execute("""PRAGMA synchronous = off;""")
    }

    override fun setupSchema(connection: Connection) {
        connection.execute(
            """
            CREATE TABLE IF NOT EXISTS addresses (
                 id             INTEGER PRIMARY KEY,
                 address        BLOB NOT NULL,
                 UNIQUE         (address)
            );
        """.trimIndent()
        )
    }

    fun resolve(address: EthAddress): ULong {
        return resolve(listOf(address)).entries.first().value
    }

    fun resolve(addresses: List<EthAddress>): Map<EthAddress, ULong> {
        val strings = addresses.map { it.toPrefixedHexString().value }
        val inClause = "(${strings.map { "'$it'" }.joinToString(",")})"

        return connection.tx {
            strings.forEach { address ->
                execute("INSERT OR IGNORE INTO addresses(address) VALUES( :address )") {
                    set("address", address)
                }
            }

            executeQuery("""SELECT * FROM addresses WHERE address in $inClause""") {
//                query {
//                    set("addresses", inClause)
//                }
                map { rs ->
                    EthAddress(EthPrefixedHexString(rs.getString("address"))) to rs.getLong("id").toULong()
                }
            }
        }.toMap()

    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}
