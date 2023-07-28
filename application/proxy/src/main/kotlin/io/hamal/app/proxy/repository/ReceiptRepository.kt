//package io.hamal.app.proxy.repository
//
//import io.hamal.lib.sqlite.BaseSqliteRepository
//import io.hamal.lib.sqlite.Connection
//import io.hamal.lib.web3.eth.abi.type.EthAddress
//import io.hamal.lib.web3.eth.abi.type.EthHash
//import io.hamal.lib.web3.eth.abi.type.EthPrefixedHexString
//import io.hamal.lib.web3.eth.domain.EthReceipt
//import io.hamal.lib.web3.util.ByteWindow
//import kotlinx.serialization.ExperimentalSerializationApi
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.decodeFromByteArray
//import kotlinx.serialization.encodeToByteArray
//import kotlinx.serialization.protobuf.ProtoBuf
//import java.math.BigInteger
//import java.nio.ByteBuffer
//import java.nio.file.Path
//
//interface ReceiptRepository {
//    fun find(hash: EthHash): EthReceipt?
//    fun store(receipt: EthReceipt)
//}
//
//@OptIn(ExperimentalSerializationApi::class)
//class SqliteReceiptRepository(
//    val path: Path,
//    private val protobuf: ProtoBuf
//) : BaseSqliteRepository(object : Config {
//    override val path = path
//    override val filename: String = "receipts.db"
//}), ReceiptRepository {
//
//    override fun setupConnection(connection: Connection) {
//        connection.execute("""PRAGMA journal_mode = wal;""")
//        connection.execute("""PRAGMA locking_mode = exclusive;""")
//        connection.execute("""PRAGMA temp_store = memory;""")
//        connection.execute("""PRAGMA synchronous = off;""")
//    }
//
//    override fun setupSchema(connection: Connection) {
//        connection.execute(
//            """
//            CREATE TABLE IF NOT EXISTS receipts (
//                 hash           BLOB NOT NULL,
//                 receipt        BLOB NOT NULL,
//                 PRIMARY KEY    (hash)
//            );
//        """.trimIndent()
//        )
//    }
//
//    override fun clear() {
//        connection.execute("DELETE FROM receipts")
//    }
//
//    override fun find(hash: EthHash): EthReceipt? {
//        return connection.executeQueryOne("SELECT receipt FROM receipts WHERE hash = :hash") {
//            query { set("hash", hash) }
//            map { rs ->
//                protobuf.decodeFromByteArray<PersistedEthReceipt>(rs.getBytes("receipt").zlibDecompress()).decode()
//            }
//        }
//    }
//
//    override fun store(receipt: EthReceipt) {
//        connection.tx {
//            execute("INSERT INTO receipts(hash, receipt) VALUES( :hash, :receipt )") {
//                set("hash", receipt.transactionHash)
//                set("receipt", protobuf.encodeToByteArray<PersistedEthReceipt>(receipt.encode()).zlibCompress())
//            }
//        }
//    }
//
//}
//
//internal fun EthReceipt.encode(): PersistedEthReceipt {
//    return PersistedEthReceipt(
//        hash = transactionHash.value.toByteArray(),
//        logs = logs.map { it.encode() }
//    )
//}
//
//internal fun EthReceipt.Log.encode(): PersistedEthReceipt.Log {
//    return PersistedEthReceipt.Log(
//        address = address.value.value.toByteArray(),
//        data = data.value.toByteArray(),
//        topics = concat(topics.map { it.value.toByteArray() })
//    )
//}
//
//internal fun PersistedEthReceipt.decode() = EthReceipt(
//    transactionHash = EthHash(hash),
//    logs = logs.map(PersistedEthReceipt.Log::decode)
//)
//
//internal fun PersistedEthReceipt.Log.decode() = EthReceipt.Log(
//    address = EthAddress(BigInteger(address)),
//    data = EthPrefixedHexString(data),
//    topics = ByteWindow.of(topics).map { bytes ->
//        EthHash(bytes)
//    }
//)
//
//
//@Serializable
//internal data class PersistedEthReceipt(
//    val hash: ByteArray,
//    val logs: List<Log>
//) {
//    @Serializable
//    data class Log(
//        val address: ByteArray,
//        val data: ByteArray,
//        val topics: ByteArray
//    )
//}
//
//
//fun concat(arrays: List<ByteArray>): ByteArray {
//    val n = arrays.sumOf { it.size }
//    val byteBuffer = ByteBuffer.allocate(n)
//    arrays.forEach { byteBuffer.put(it) }
//    return byteBuffer.array()
//}