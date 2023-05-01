package io.hamal.backend.store.impl

import io.hamal.lib.RequestId
import io.hamal.lib.Shard
import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.vo.base.DomainId
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations
import java.nio.file.Files
import java.time.Instant


class NamedParametersIT {

    companion object {
        private val testStore = TestStore()
    }

    @Test
    fun `Sets named parameter of type boolean`() {
        testStore.withoutTx {
            execute("INSERT INTO boolean_table(value, another_value) VALUES(:true_value, :false_value)") {
                set("true_value", true)
                set("false_value", false)
            }
        }

        verifyIsOne("SELECT COUNT(*) FROM boolean_table WHERE value = true")
        verifyIsZero("SELECT COUNT(*) FROM boolean_table WHERE value = false")

        verifyIsOne("SELECT COUNT(*) FROM boolean_table WHERE another_value = false")
        verifyIsZero("SELECT COUNT(*) FROM boolean_table WHERE another_value = true")
    }

    @Test
    fun `Sets named parameter of type int`() {
        testStore.withoutTx {
            execute("INSERT INTO int_table(value, another_value) VALUES(:some_value, :another_value)") {
                set("some_value", 28)
                set("another_value", 10)
            }
        }
        verifyIsOne("SELECT COUNT(*) FROM int_table WHERE value = 28")
        verifyIsZero("SELECT COUNT(*) FROM int_table WHERE value = 10")

        verifyIsOne("SELECT COUNT(*) FROM int_table WHERE another_value = 10")
        verifyIsZero("SELECT COUNT(*) FROM int_table WHERE another_value = 28")
    }

    @Test
    fun `Sets named parameter of type long`() {
        testStore.inTx {
            execute("INSERT INTO long_table(value, another_value) VALUES(:some_value, :another_value)") {
                set("some_value", 42949672950L)
                set("another_value", 42949672950000L)
            }
        }
        verifyIsOne("SELECT COUNT(*) FROM long_table WHERE value = 42949672950")
        verifyIsZero("SELECT COUNT(*) FROM long_table WHERE value = 42949672950000")

        verifyIsOne("SELECT COUNT(*) FROM long_table WHERE another_value = 42949672950000")
        verifyIsZero("SELECT COUNT(*) FROM long_table WHERE another_value = 42949672950")
    }

    @Test
    fun `Sets named parameter of type string`() {
        testStore.inTx {
            execute("INSERT INTO string_table(value, another_value) VALUES(:some_value, :another_value)") {
                set("some_value", "HamalRocks")
                set("another_value", "HamalRRRRRROOOOOOCCCCKKKKKSSSSSS")
            }
        }
        verifyIsOne("SELECT COUNT(*) FROM string_table WHERE value = 'HamalRocks'")
        verifyIsZero("SELECT COUNT(*) FROM string_table WHERE value = 'HamalRRRRRROOOOOOCCCCKKKKKSSSSSS'")

        verifyIsOne("SELECT COUNT(*) FROM string_table WHERE another_value = 'HamalRRRRRROOOOOOCCCCKKKKKSSSSSS'")
        verifyIsZero("SELECT COUNT(*) FROM string_table WHERE another_value = 'HamalRocks'")
    }

    @Test
    fun `Sets named parameter of type instant`() {
        testStore.inTx {
            execute("INSERT INTO instant_table(value, another_value) VALUES(:some_value, :another_value)") {
                set("some_value", Instant.ofEpochMilli(0))
                set("another_value", Instant.ofEpochMilli(1682930120851))
            }
        }
        verifyIsOne("SELECT COUNT(*) FROM instant_table WHERE value = 0")
        verifyIsZero("SELECT COUNT(*) FROM instant_table WHERE value = 1682930120851")

        verifyIsOne("SELECT COUNT(*) FROM instant_table WHERE another_value = 1682930120851")
        verifyIsZero("SELECT COUNT(*) FROM instant_table WHERE another_value = 0")
    }

    @Test
    fun `Sets named parameter of type snowflake id`() {
        testStore.inTx {
            execute("INSERT INTO snowflake_id_table(value, another_value) VALUES(:some_value, :another_value)") {
                set("some_value", SnowflakeId(2810))
                set("another_value", SnowflakeId(0))
            }
        }
        verifyIsOne("SELECT COUNT(*) FROM snowflake_id_table WHERE value = 2810")
        verifyIsZero("SELECT COUNT(*) FROM snowflake_id_table WHERE value = 0")

        verifyIsOne("SELECT COUNT(*) FROM snowflake_id_table WHERE another_value = 0")
        verifyIsZero("SELECT COUNT(*) FROM snowflake_id_table WHERE another_value = 2810")
    }

    @Test
    fun `Sets named parameter of type domain id`() {
        class TestDomainId(override val value: SnowflakeId) : DomainId() {
            constructor(value: Int) : this(SnowflakeId(value.toLong()))
        }

        testStore.inTx {
            execute("INSERT INTO domain_id_table(value, another_value) VALUES(:some_value, :another_value)") {
                set("some_value", TestDomainId(2810))
                set("another_value", TestDomainId(0))
            }
        }
        verifyIsOne("SELECT COUNT(*) FROM domain_id_table WHERE value = 2810")
        verifyIsZero("SELECT COUNT(*) FROM domain_id_table WHERE value = 0")

        verifyIsOne("SELECT COUNT(*) FROM domain_id_table WHERE another_value = 0")
        verifyIsZero("SELECT COUNT(*) FROM domain_id_table WHERE another_value = 2810")
    }

    @Test
    fun `Sets named parameter of type request id`() {
        testStore.inTx {
            execute("INSERT INTO request_id_table(value, another_value) VALUES(:some_value, :another_value)") {
                set("some_value", RequestId(2810))
                set("another_value", RequestId(0))
            }
        }
        verifyIsOne("SELECT COUNT(*) FROM request_id_table WHERE value = 2810")
        verifyIsZero("SELECT COUNT(*) FROM request_id_table WHERE value = 0")

        verifyIsOne("SELECT COUNT(*) FROM request_id_table WHERE another_value = 0")
        verifyIsZero("SELECT COUNT(*) FROM request_id_table WHERE another_value = 2810")
    }




    private fun verifyIsOne(query: String) {
        testStore.jdbcOperations().query(query) {
            assertThat(it.getInt(1), equalTo(1))
        }
    }

    private fun verifyIsZero(query: String) {
        testStore.jdbcOperations().query(query) {
            assertThat(it.getInt(1), equalTo(0))
        }
    }
}

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
    fun jdbcOperations() = jdbcOperations

    override fun setupConnection(operations: NamedParameterJdbcOperations) {
        withoutTx {
//            execute("""PRAGMA journal_mode = wal;""")
//            execute("""PRAGMA locking_mode = exclusive;""")
//            execute("""PRAGMA temp_store = memory;""")
//            execute("""PRAGMA synchronous = off;""")
        }
    }

    override fun setupSchema(operations: NamedParameterJdbcOperations) {
         inTx {
            execute("""CREATE TABLE boolean_table(value BOOLEAN NOT NULL, another_value BOOLEAN)""")
            execute("""CREATE TABLE int_table(value INT NOT NULL, another_value INT)""")
            execute("""CREATE TABLE long_table(value INT NOT NULL, another_value INT)""")
            execute("""CREATE TABLE string_table(value TEXT NOT NULL, another_value TEXT)""")
            execute("""CREATE TABLE instant_table(value INT NOT NULL, another_value INT)""")
            execute("""CREATE TABLE snowflake_id_table(value INT NOT NULL, another_value INT)""")
            execute("""CREATE TABLE domain_id_table(value INT NOT NULL, another_value INT)""")
            execute("""CREATE TABLE request_id_table(value INT NOT NULL, another_value INT)""")
        }
    }

    override fun drop() {
        inTx {
            execute("DROP TABLE IF EXISTS boolean_table")
            execute("DROP TABLE IF EXISTS int_table")
            execute("DROP TABLE IF EXISTS long_table")
            execute("DROP TABLE IF EXISTS string_table")
            execute("DROP TABLE IF EXISTS instant_table")
            execute("DROP TABLE IF EXISTS snowflake_id_table")
            execute("DROP TABLE IF EXISTS domain_id_table")
            execute("DROP TABLE IF EXISTS request_id_table")
        }
    }
}