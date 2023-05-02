package io.hamal.backend.store.impl

import io.hamal.lib.Shard
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.sql.Connection

// test each function of each operation
class TxOperationsIT {
    @Test
    fun implementMe() {
        TODO()
    }
}

class RxOperationsIT {
    @Test
    fun implementMe() {
        TODO()
    }
}

class OperationsIT {
    @Test
    fun implementMe() {
        TODO()
    }
}

class BaseStoreIT {
    @Test
    fun implementMe() {
        TODO()
    }
}


class TestStore : BaseStore(
    object : Config {
        override val path = Files.createTempDirectory("test-store")
        override val name = "test-store"
        override val shard = Shard(42)
    }
) {
//    fun jdbcOperations() = jdbcOperations

    //    override fun setupConnection(operations: NamedParameterJdbcOperations) {
    override fun setupConnection(connection: Connection) {
        connection.createStatement().use {
            it.execute("""PRAGMA journal_mode = wal;""")
            it.execute("""PRAGMA locking_mode = exclusive;""")
            it.execute("""PRAGMA temp_store = memory;""")
            it.execute("""PRAGMA synchronous = off;""")
        }
//        withoutTx {
//            operations.execute("""PRAGMA journal_mode = wal;"""){}
//            operations.execute("""PRAGMA locking_mode = exclusive;"""){}
//            operations.execute("""PRAGMA temp_store = memory;"""){}
//            operations.execute("""PRAGMA synchronous = off;"""){}
        //            execute("""PRAGMA journal_mode = wal;""")
//            execute("""PRAGMA locking_mode = exclusive;""")
//            execute("""PRAGMA temp_store = memory;""")
//            execute("""PRAGMA synchronous = off;""")
//        }
    }

    //    override fun setupSchema(operations: NamedParameterJdbcOperations) {
    override fun setupSchema(connection: Connection) {
        connection.createStatement().use {
            it.execute("""CREATE TABLE boolean_table(value BOOLEAN NOT NULL, another_value BOOLEAN)""")
            it.execute("""CREATE TABLE int_table(value INT NOT NULL, another_value INT)""")
            it.execute("""CREATE TABLE long_table(value INT NOT NULL, another_value INT)""")
            it.execute("""CREATE TABLE string_table(value TEXT NOT NULL, another_value TEXT)""")
            it.execute("""CREATE TABLE instant_table(value INT NOT NULL, another_value INT)""")
            it.execute("""CREATE TABLE snowflake_id_table(value INT NOT NULL, another_value INT)""")
            it.execute("""CREATE TABLE domain_id_table(value INT NOT NULL, another_value INT)""")
            it.execute("""CREATE TABLE request_id_table(value INT NOT NULL, another_value INT)""")
        }
//         inTx {
//            execute("""CREATE TABLE boolean_table(value BOOLEAN NOT NULL, another_value BOOLEAN)""")
//            execute("""CREATE TABLE int_table(value INT NOT NULL, another_value INT)""")
//            execute("""CREATE TABLE long_table(value INT NOT NULL, another_value INT)""")
//            execute("""CREATE TABLE string_table(value TEXT NOT NULL, another_value TEXT)""")
//            execute("""CREATE TABLE instant_table(value INT NOT NULL, another_value INT)""")
//            execute("""CREATE TABLE snowflake_id_table(value INT NOT NULL, another_value INT)""")
//            execute("""CREATE TABLE domain_id_table(value INT NOT NULL, another_value INT)""")
//            execute("""CREATE TABLE request_id_table(value INT NOT NULL, another_value INT)""")
//        }
    }

    override fun drop() {
//        inTx {
//            execute("DROP TABLE IF EXISTS boolean_table")
//            execute("DROP TABLE IF EXISTS int_table")
//            execute("DROP TABLE IF EXISTS long_table")
//            execute("DROP TABLE IF EXISTS string_table")
//            execute("DROP TABLE IF EXISTS instant_table")
//            execute("DROP TABLE IF EXISTS snowflake_id_table")
//            execute("DROP TABLE IF EXISTS domain_id_table")
//            execute("DROP TABLE IF EXISTS request_id_table")
//        }
    }
}