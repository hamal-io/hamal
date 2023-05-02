package io.hamal.backend.store.impl

import io.hamal.lib.Shard
import io.hamal.lib.util.Files
import java.nio.file.Path
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlin.io.path.Path

class ResultSet(result: ResultSet) {

}

class TxOperations(
//    private val jdbcOperations: NamedParameterJdbcOperations,
//    private val transactionStatus: TransactionStatus
    private val connection: Connection
) {

//    fun execute(query: String) {
//        execute(query) { NamedParameters() }
//    }

//    fun execute(query: String, block: NamedParameters.() -> NamedParameters) {
////        val parameters = block(NamedParameters())
////        jdbcOperations.update(query, parameters.mapping)
//    }

    fun abort() {
        println("Aborting by throwing an expceted exception")
    }
}


class Operations(
//    private val jdbcOperations: NamedParameterJdbcOperations
    private val connection: Connection
) {

//    fun execute(query: String, block: NamedParameters.() -> NamedParameters) {
//        val parameters = block(NamedParameters())
//        connection.prepare(query).use {
//            parameters.mapping.forEach { parameter, value ->
//
//            }
//            it.set("true_value", parameters.mapping["true_value"] as Boolean)
//            it.set("false_value", parameters.mapping["false_value"] as Boolean)
//            it.execute()
//        }
////        jdbcOperations.update(query, parameters.mapping)
//    }
}


abstract class BaseStore(config: Config) : AutoCloseable {
    //    protected val dataSource: DataSource
//    protected val txOperations: TransactionOperations
//    protected val jdbcOperations: NamedParameterJdbcOperations
    val connection: Connection

    init {
//        dataSource = DriverManagerDataSource()
//        dataSource.setDriverClassName("org.sqlite.JDBC")
//        dataSource.url = "jdbc:sqlite:${ensureFilePath(config)}"
        connection = DriverManager.getConnection("jdbc:sqlite:${ensureFilePath(config)}")
//
//        txOperations = TransactionTemplate(DataSourceTransactionManager(dataSource))
//        txOperations.propagationBehavior = TransactionDefinition.PROPAGATION_REQUIRED
//
//        jdbcOperations = NamedParameterJdbcTemplate(dataSource)
//
//        setupConnection(jdbcOperations)
        setupConnection(connection)
//        setupSchema(jdbcOperations)
        setupConnection(connection)
    }


    interface Config {
        val path: Path
        val name: String
        val shard: Shard
    }

    abstract fun setupConnection(connection: Connection)
    abstract fun setupSchema(connection: Connection)
    abstract fun drop()
    fun <T> inTx(block: TxOperations.() -> T): T? {
//        return txOperations.execute { status ->
//            block(TxOperations(jdbcOperations, status))
//        }
        return block(TxOperations(connection))
    }

    fun <T> withoutTx(block: Operations.() -> T): T? {
//        return block(Operations(jdbcOperations))
        return block(Operations(connection))
    }

//    fun <T> inRx(fn: Operations.() -> T): T? {
//        return txOperations.execute { status ->
//            fn(Operations(jdbcOperations, status))
//        }
//    }

    // operations without any tx


    override fun close() {
//        dataSource.connection.close()
        connection.close()
    }
}

private fun ensureFilePath(config: BaseStore.Config): Path {
    return Files.createDirectories(config.path)
        .resolve(Path(String.format("${config.name}-%04d.db", config.shard.value.toLong())))
}