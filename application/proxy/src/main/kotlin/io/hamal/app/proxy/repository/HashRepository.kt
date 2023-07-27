package io.hamal.app.proxy.repository

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sqlite.BaseSqliteRepository
import io.hamal.lib.sqlite.Connection
import io.hamal.lib.web3.eth.abi.type.EthAddress
import io.hamal.lib.web3.eth.abi.type.EthHash
import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
import io.hamal.lib.web3.eth.abi.type.EthUint64
import io.hamal.lib.web3.eth.domain.EthGetBlockResp
import io.hamal.lib.web3.eth.http.EthHttpBatchService
import kotlin.io.path.Path

class HashRepository : BaseSqliteRepository(object : Config {
    override val path = Path("/tmp/hamal/db")
    override val filename: String = "hashes.db"
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
            CREATE TABLE IF NOT EXISTS hashes (
                 id             INTEGER PRIMARY KEY,
                 hash           BLOB NOT NULL,
                 UNIQUE         (hash)
            );
        """.trimIndent()
        )
    }

    fun resolve(hashes: List<EthHash>): Map<EthHash, ULong> {
        val strings = hashes.map { it.toPrefixedHexString().value }
        val inClause = "(${strings.map { "'$it'" }.joinToString(",")})"


        return connection.tx {
            strings.forEach { hash ->
                execute("INSERT OR IGNORE INTO hashes(hash) VALUES( :hash )") {
                    set("hash", hash)
                }
            }

            executeQuery("""SELECT * FROM hashes WHERE hash in $inClause""") {
//                query {
//                    set("hashes", inClause)
//                }
                map { rs ->
                    EthHash(EthPrefixedHexString(rs.getString("hash"))) to rs.getLong("id").toULong()
                }
            }
        }.toMap()

    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}


class AddressRepository : BaseSqliteRepository(object : Config {
    override val path = Path("/tmp/hamal/db")
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

fun main() {
    // FIXME have separate address repo
    val hRepo = HashRepository()
    val aRepo = AddressRepository()


    val srv = EthHttpBatchService(HttpTemplate("http://localhost:8081"))
    for (x in 17771431..17781431) {
        val result = srv.getBlock(EthUint64(x.toLong())).execute().first() as EthGetBlockResp
        val resolvedHashes = hRepo.resolve(
            listOf(
                result.result.hash,
                result.result.parentHash,
            ).plus(
                result.result.transactions.map { it.hash }
            )
        )
        println(resolvedHashes)

        val resolvedAddresses = aRepo.resolve(
            listOf(
                result.result.miner,
            )
                .plus(result.result.transactions.map { it.from })
                .plus(result.result.transactions.mapNotNull { it.to })
        )

        println(resolvedHashes)
        println(resolvedAddresses)
    }

}