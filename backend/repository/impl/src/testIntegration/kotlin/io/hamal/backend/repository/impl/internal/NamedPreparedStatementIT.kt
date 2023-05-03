package io.hamal.backend.repository.impl.internal

import io.hamal.backend.repository.impl.internal.DefaultNamedPreparedStatement.Companion.prepare
import io.hamal.lib.RequestId
import io.hamal.lib.util.SnowflakeId
import io.hamal.lib.vo.base.DomainId
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager
import java.time.Instant


class NamedPreparedStatementIT {
    companion object {
        val connection: Connection = DriverManager.getConnection(
            "jdbc:sqlite:${Files.createTempDirectory("named-parameter-test")}/test.db"
        )

        init {
            connection.createStatement().use {
                it.execute("""PRAGMA journal_mode = wal;""")
                it.execute("""PRAGMA locking_mode = exclusive;""")
                it.execute("""PRAGMA temp_store = memory;""")
                it.execute("""PRAGMA synchronous = off;""")
            }

            connection.createStatement().use {
                it.execute("""CREATE TABLE boolean_table(value BOOLEAN NOT NULL, another_value BOOLEAN)""")
                it.execute("""CREATE TABLE int_table(value INT NOT NULL, another_value INT)""")
                it.execute("""CREATE TABLE long_table(value INT NOT NULL, another_value INT)""")
                it.execute("""CREATE TABLE string_table(value TEXT NOT NULL, another_value TEXT)""")
                it.execute("""CREATE TABLE instant_table(value INT NOT NULL, another_value INT)""")
                it.execute("""CREATE TABLE snowflake_id_table(value INT NOT NULL, another_value INT)""")
                it.execute("""CREATE TABLE domain_id_table(value INT NOT NULL, another_value INT)""")
                it.execute("""CREATE TABLE request_id_table(value INT NOT NULL, another_value INT)""")
                it.execute("""CREATE TABLE nullable_table(value INT , another_value INT)""")
                it.execute("""CREATE TABLE unique_number(value INT PRIMARY KEY )""")
            }
        }
    }

    @Nested
    @DisplayName("execute()")
    inner class ExecuteTest {
        @Test
        fun `Statement yields result set`() {
            connection.prepare("INSERT INTO nullable_table(value, another_value) VALUES(:some_value,:another_value) RETURNING *")
                .use {
                    it["some_value"] = 23
                    it["another_value"] = 42
                    assertTrue(it.execute())
                }
        }

        @Test
        fun `Statement does not yield result set`() {
            connection.prepare("INSERT INTO nullable_table(value, another_value) VALUES(:some_value,:another_value)")
                .use {
                    it["some_value"] = 23
                    it["another_value"] = 42
                    assertFalse(it.execute())
                }
        }

        @Test
        fun `None of the named parameters were set`() {
            val exception = assertThrows<IllegalArgumentException> {
                connection.prepare("INSERT INTO nullable_table(value, another_value) VALUES(:some_value,:another_value)")
                    .use {
                        it.execute()
                    }
            }
            assertThat(
                exception.message,
                containsString("Expected all named parameters to be set, but [some_value, another_value] are missing")
            )
        }
    }

    @Nested
    @DisplayName("executeUpdate()")
    inner class ExecuteUpdate {
        @Test
        fun `Statement inserts one row`() {
            connection.prepare("INSERT INTO unique_number(value) VALUES(:some_value)")
                .use {
                    it["some_value"] = 23
                    assertThat(it.executeUpdate(), equalTo(1))
                }
        }

        @Test
        fun `Statement does not insert row, because it already exists`() {
            connection.prepare("INSERT OR IGNORE  INTO unique_number(value) VALUES(:some_value)")
                .use {
                    it["some_value"] = 42
                    assertThat(it.executeUpdate(), equalTo(1))
                }
            connection.prepare("INSERT OR IGNORE  INTO unique_number(value) VALUES(:some_value)")
                .use {
                    it["some_value"] = 42
                    assertThat(it.executeUpdate(), equalTo(0))
                }
        }

        @Test
        fun `None of the named parameters were set`() {
            val exception = assertThrows<IllegalArgumentException> {
                connection.prepare("INSERT INTO nullable_table(value, another_value) VALUES(:some_value,:another_value)")
                    .use {
                        it.executeUpdate()
                    }
            }
            assertThat(
                exception.message,
                containsString("Expected all named parameters to be set, but [some_value, another_value] are missing")
            )
        }
    }

    @Nested
    @DisplayName("executeQuery()")
    inner class ExecuteQuery {
        @Test
        fun `Query does not return any value`() {
            connection.prepare("SELECT * FROM unique_number WHERE value = :some_value")
                .use {
                    it["some_value"] = 4411
                    it.executeQuery()
                }
        }

        @Test
        fun `Query returns value`() {
            connection.prepare("INSERT OR IGNORE  INTO unique_number(value) VALUES(:some_value)")
                .use { it["some_value"] = 1133; it.execute() }

            connection.prepare("SELECT * FROM unique_number WHERE value = :some_value")
                .use {
                    it["some_value"] = 1133
                    val result = it.executeQuery()
                    assertThat(result.getInt("value"), equalTo(1133))
                }
        }

        @Test
        fun `None of the named parameters were set`() {
            val exception = assertThrows<IllegalArgumentException> {
                connection.prepare("SELECT * FROM unique_number WHERE value = :some_value")
                    .use {
                        it.executeQuery()
                    }
            }
            assertThat(
                exception.message,
                containsString("Expected all named parameters to be set, but [some_value] are missing")
            )
        }
    }

    @Nested
    @DisplayName("set()")
    inner class SetTest {

        @Test
        fun `Sets named parameter of type boolean`() {
            connection.prepare("INSERT INTO boolean_table(value, another_value) VALUES(:some_value,:another_value)")
                .use {
                    it["some_value"] = true
                    it["another_value"] = false
                    it.execute()
                }

            verifyIsOne("SELECT COUNT(*) FROM boolean_table WHERE value = true")
            verifyIsZero("SELECT COUNT(*) FROM boolean_table WHERE value = false")

            verifyIsOne("SELECT COUNT(*) FROM boolean_table WHERE another_value = false")
            verifyIsZero("SELECT COUNT(*) FROM boolean_table WHERE value = false")
        }

        @Test
        fun `Sets named parameter of type int`() {
            connection.prepare("INSERT INTO int_table(value, another_value) VALUES(:some_value, :another_value)")
                .use {
                    it["some_value"] = 28
                    it["another_value"] = 10
                    it.execute()
                }

            verifyIsOne("SELECT COUNT(*) FROM int_table WHERE value = 28")
            verifyIsZero("SELECT COUNT(*) FROM int_table WHERE value = 10")

            verifyIsOne("SELECT COUNT(*) FROM int_table WHERE another_value = 10")
            verifyIsZero("SELECT COUNT(*) FROM int_table WHERE another_value = 28")
        }

        @Test
        fun `Sets named parameter of type long`() {
            connection.prepare("INSERT INTO long_table(value, another_value) VALUES(:some_value, :another_value)")
                .use {
                    it["some_value"] = 42949672950L
                    it["another_value"] = 42949672950000L
                    it.execute()
                }
            verifyIsOne("SELECT COUNT(*) FROM long_table WHERE value = 42949672950")
            verifyIsZero("SELECT COUNT(*) FROM long_table WHERE value = 42949672950000")

            verifyIsOne("SELECT COUNT(*) FROM long_table WHERE another_value = 42949672950000")
            verifyIsZero("SELECT COUNT(*) FROM long_table WHERE another_value = 42949672950")
        }

        @Test
        fun `Sets named parameter of type string`() {
            connection.prepare("INSERT INTO string_table(value, another_value) VALUES(:some_value, :another_value)")
                .use {
                    it["some_value"] = "HamalRocks"
                    it["another_value"] = "HamalRRRRRROOOOOOCCCCKKKKKSSSSSS"
                    it.execute()
                }

            verifyIsOne("SELECT COUNT(*) FROM string_table WHERE value = 'HamalRocks'")
            verifyIsZero("SELECT COUNT(*) FROM string_table WHERE value = 'HamalRRRRRROOOOOOCCCCKKKKKSSSSSS'")

            verifyIsOne("SELECT COUNT(*) FROM string_table WHERE another_value = 'HamalRRRRRROOOOOOCCCCKKKKKSSSSSS'")
            verifyIsZero("SELECT COUNT(*) FROM string_table WHERE another_value = 'HamalRocks'")
        }

        @Test
        fun `Sets named parameter of type instant`() {
            connection.prepare("INSERT INTO instant_table(value, another_value) VALUES(:some_value, :another_value)")
                .use {
                    it["some_value"] = Instant.ofEpochMilli(0)
                    it["another_value"] = Instant.ofEpochMilli(1682930120851)
                    it.execute()
                }
            verifyIsOne("SELECT COUNT(*) FROM instant_table WHERE value = 0")
            verifyIsZero("SELECT COUNT(*) FROM instant_table WHERE value = 1682930120851")

            verifyIsOne("SELECT COUNT(*) FROM instant_table WHERE another_value = 1682930120851")
            verifyIsZero("SELECT COUNT(*) FROM instant_table WHERE another_value = 0")
        }

        @Test
        fun `Sets named parameter of type snowflake id`() {
            connection.prepare("INSERT INTO snowflake_id_table(value, another_value) VALUES(:some_value, :another_value)")
                .use {
                    it["some_value"] = SnowflakeId(2810)
                    it["another_value"] = SnowflakeId(0)
                    it.execute()
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

            connection.prepare("INSERT INTO domain_id_table(value, another_value) VALUES(:some_value, :another_value)")
                .use {
                    it["some_value"] = TestDomainId(2810)
                    it["another_value"] = TestDomainId(0)
                    it.execute()
                }
            verifyIsOne("SELECT COUNT(*) FROM domain_id_table WHERE value = 2810")
            verifyIsZero("SELECT COUNT(*) FROM domain_id_table WHERE value = 0")

            verifyIsOne("SELECT COUNT(*) FROM domain_id_table WHERE another_value = 0")
            verifyIsZero("SELECT COUNT(*) FROM domain_id_table WHERE another_value = 2810")
        }

        @Test
        fun `Sets named parameter of type request id`() {
            connection.prepare("INSERT INTO request_id_table(value, another_value) VALUES(:some_value, :another_value)")
                .use {
                    it["some_value"] = RequestId(2810)
                    it["another_value"] = RequestId(0)
                    it.execute()
                }
            verifyIsOne("SELECT COUNT(*) FROM request_id_table WHERE value = 2810")
            verifyIsZero("SELECT COUNT(*) FROM request_id_table WHERE value = 0")

            verifyIsOne("SELECT COUNT(*) FROM request_id_table WHERE another_value = 0")
            verifyIsZero("SELECT COUNT(*) FROM request_id_table WHERE another_value = 2810")
        }

        private fun verifyIsOne(query: String) {
            connection.createStatement().use { stmt ->
                stmt.executeQuery(query).use {
                    assertThat(it.getInt(1), equalTo(1))
                }
            }
        }

        private fun verifyIsZero(query: String) {
            connection.createStatement().use { stmt ->
                stmt.executeQuery(query).use {
                    assertThat(it.getInt(1), equalTo(0))
                }
            }
        }
    }
}