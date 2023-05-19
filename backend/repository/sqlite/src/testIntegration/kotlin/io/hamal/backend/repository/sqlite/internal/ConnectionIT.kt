package io.hamal.backend.repository.sqlite.internal

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.ReqId
import io.hamal.lib.domain.vo.base.DomainId
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.nio.file.Files
import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger

class DefaultConnectionIT {

    @Nested
    @DisplayName("prepare()")
    inner class PrepareTest {
        @Test
        fun `Statement are not getting cached`() {
            val result = testInstance.prepare("INSERT INTO some_table(value) VALUES (:some_value)")
            result["some_value"] = 1234
            result.execute()

            val anotherResult = testInstance.prepare("INSERT INTO some_table(value) VALUES (:some_value)")
            anotherResult["some_value"] = 5432
            anotherResult.execute()

            assertNotSame(result, anotherResult)

            testInstance.prepare("SELECT COUNT(*) as count FROM some_table").executeQuery()
                .use {
                    assertThat(it.getInt("count"), equalTo(2))
                }
        }

        private val testInstance = DefaultConnection(
            PrepareTest::class,
            "jdbc:sqlite:${Files.createTempDirectory("execute")}/db.sqlite"
        )

        init {
            testInstance.execute("""PRAGMA journal_mode = wal;""")
            testInstance.execute("""PRAGMA locking_mode = exclusive;""")
            testInstance.execute("""PRAGMA temp_store = memory;""")
            testInstance.execute("""PRAGMA synchronous = off;""")

            testInstance.execute("""CREATE TABLE some_table(value INT NOT NULL)""")
        }
    }

    @Nested
    @DisplayName("execute()")
    inner class ExecuteTest {
        @Test
        fun `Named parameter is missing`() {
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.execute("INSERT INTO string_table(value) VALUES(:some_value)")
            }
            assertThat(
                exception.message,
                containsString("Expected all named parameters to be set, but [some_value] are missing")
            )
        }

        @Test
        fun `With named parameter of type boolean`() {
            testInstance.execute("INSERT INTO boolean_table(value) VALUES(:some_value)") {
                set("some_value", true)
            }
            verifyIsOne("SELECT COUNT(*) FROM boolean_table WHERE value = true")
        }

        @Test
        fun `With named parameter of type int`() {
            testInstance.execute("INSERT INTO int_table(value) VALUES(:some_value)") {
                set("some_value", 1337)
            }
            verifyIsOne("SELECT COUNT(*) FROM int_table WHERE value = 1337")
        }

        @Test
        fun `With named parameter of type long`() {
            testInstance.execute("INSERT INTO long_table(value) VALUES(:some_value)") {
                set("some_value", 12345678901234567L)
            }
            verifyIsOne("SELECT COUNT(*) FROM long_table WHERE value = 12345678901234567")
        }

        @Test
        fun `With named parameter of type instant`() {
            testInstance.execute("INSERT INTO instant_table(value) VALUES(:some_value)") {
                set("some_value", Instant.ofEpochMilli(12345678))
            }
            verifyIsOne("SELECT COUNT(*) FROM instant_table WHERE value = 12345678")
        }

        @Test
        fun `With named parameter of type snowflake_id`() {
            testInstance.execute("INSERT INTO snowflake_id_table(value) VALUES(:some_value)") {
                set("some_value", SnowflakeId(12345678))
            }
            verifyIsOne("SELECT COUNT(*) FROM snowflake_id_table WHERE value = 12345678")
        }

        @Test
        fun `With named parameter of type domain_id`() {
            class TestDomainId(override val value: SnowflakeId) : DomainId() {
                constructor(value: Int) : this(SnowflakeId(value.toLong()))
            }

            testInstance.execute("INSERT INTO domain_id_table(value) VALUES(:some_value)") {
                set("some_value", TestDomainId(12345678))
            }
            verifyIsOne("SELECT COUNT(*) FROM domain_id_table WHERE value = 12345678")
        }

        @Test
        fun `With named parameter of type request_id`() {
            testInstance.execute("INSERT INTO request_id_table(value) VALUES(:some_value)") {
                set("some_value", ReqId(12345678))
            }
            verifyIsOne("SELECT COUNT(*) FROM request_id_table WHERE value = 12345678")
        }

        @Test
        fun `With named parameter of type string`() {
            testInstance.execute("INSERT INTO string_table(value) VALUES(:some_value)") {
                set("some_value", "ThisHamalConnectionRockz")
            }
            verifyIsOne("SELECT COUNT(*) FROM string_table WHERE value = 'ThisHamalConnectionRockz'")
        }

        @Test
        fun `Execute returns result set`() {
            val result = testInstance.execute<Int>(
                "INSERT INTO string_table(id, value) VALUES(:some_id, :some_value) RETURNING id"
            ) {
                with {
                    set("some_id", 1337)
                    set("some_value", "ThisHamalConnectionRockz")
                }
                map {
                    it.getInt("id")
                }
            }
            assertThat(result, equalTo(1337))
            verifyIsOne("SELECT COUNT(*) FROM string_table WHERE value = 'ThisHamalConnectionRockz'")
        }

        @Test
        fun `Execute with result mapper but query does return result`() {
            `Execute returns result set`()

            val result = testInstance.execute<Int>(
                "INSERT OR IGNORE INTO string_table(id, value) VALUES(:some_id, :some_value) RETURNING id"
            ) {
                with {
                    set("some_id", 1337)
                    set("some_value", "ThisHamalConnectionRockz")
                }
                map {
                    it.getInt("id")
                }
            }
            assertThat(result, nullValue())
            verifyIsOne("SELECT COUNT(*) FROM string_table")
        }


        private val testInstance = DefaultConnection(
            ExecuteTest::class,
            "jdbc:sqlite:${Files.createTempDirectory("execute")}/db.sqlite"
        )

        init {
            testInstance.execute("""PRAGMA journal_mode = wal;""")
            testInstance.execute("""PRAGMA locking_mode = exclusive;""")
            testInstance.execute("""PRAGMA temp_store = memory;""")
            testInstance.execute("""PRAGMA synchronous = off;""")

            testInstance.execute("""CREATE TABLE boolean_table(value BOOLEAN NOT NULL)""")
            testInstance.execute("""CREATE TABLE int_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE long_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE string_table(id INT PRIMARY KEY, value TEXT NOT NULL)""")
            testInstance.execute("""CREATE TABLE instant_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE snowflake_id_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE domain_id_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE request_id_table(value INT NOT NULL)""")
        }

        private fun verifyIsOne(query: String) {
            testInstance.delegate.createStatement().use { stmt ->
                stmt.executeQuery(query).use {
                    assertThat(it.getInt(1), equalTo(1))
                }
            }
        }
    }

    @Nested
    @DisplayName("executeUpdate()")
    inner class ExecuteUpdateTest {
        @Test
        fun `Named parameter is missing`() {
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.executeUpdate("INSERT INTO string_table(value) VALUES(:some_value)")
            }
            assertThat(
                exception.message,
                containsString("Expected all named parameters to be set, but [some_value] are missing")
            )
        }

        @Test
        fun `With named parameter of type boolean`() {
            val result = testInstance.executeUpdate("INSERT INTO boolean_table(value) VALUES(:some_value)") {
                set("some_value", true)
            }
            assertThat(result, equalTo(1))
            verifyIsOne("SELECT COUNT(*) FROM boolean_table WHERE value = true")
        }

        @Test
        fun `With named parameter of type int`() {
            val result = testInstance.executeUpdate("INSERT INTO int_table(value) VALUES(:some_value)") {
                set("some_value", 1337)
            }
            assertThat(result, equalTo(1))
            verifyIsOne("SELECT COUNT(*) FROM int_table WHERE value = 1337")
        }

        @Test
        fun `With named parameter of type long`() {
            val result = testInstance.executeUpdate("INSERT INTO long_table(value) VALUES(:some_value)") {
                set("some_value", 12345678901234567L)
            }
            assertThat(result, equalTo(1))
            verifyIsOne("SELECT COUNT(*) FROM long_table WHERE value = 12345678901234567")
        }

        @Test
        fun `With named parameter of type instant`() {
            val result = testInstance.executeUpdate("INSERT INTO instant_table(value) VALUES(:some_value)") {
                set("some_value", Instant.ofEpochMilli(12345678))
            }
            assertThat(result, equalTo(1))
            verifyIsOne("SELECT COUNT(*) FROM instant_table WHERE value = 12345678")
        }

        @Test
        fun `With named parameter of type snowflake_id`() {
            val result = testInstance.executeUpdate("INSERT INTO snowflake_id_table(value) VALUES(:some_value)") {
                set("some_value", SnowflakeId(12345678))
            }
            assertThat(result, equalTo(1))
            verifyIsOne("SELECT COUNT(*) FROM snowflake_id_table WHERE value = 12345678")
        }

        @Test
        fun `With named parameter of type domain_id`() {
            class TestDomainId(override val value: SnowflakeId) : DomainId() {
                constructor(value: Int) : this(SnowflakeId(value.toLong()))
            }

            val result = testInstance.executeUpdate("INSERT INTO domain_id_table(value) VALUES(:some_value)") {
                set("some_value", TestDomainId(12345678))
            }
            assertThat(result, equalTo(1))
            verifyIsOne("SELECT COUNT(*) FROM domain_id_table WHERE value = 12345678")
        }

        @Test
        fun `With named parameter of type request_id`() {
            val result = testInstance.executeUpdate("INSERT INTO request_id_table(value) VALUES(:some_value)") {
                set("some_value", ReqId(12345678))
            }
            assertThat(result, equalTo(1))
            verifyIsOne("SELECT COUNT(*) FROM request_id_table WHERE value = 12345678")
        }

        @Test
        fun `With named parameter of type string`() {
            val result = testInstance.executeUpdate("INSERT INTO string_table(value) VALUES(:some_value)") {
                set("some_value", "ThisHamalConnectionRockz")
            }
            assertThat(result, equalTo(1))
            verifyIsOne("SELECT COUNT(*) FROM string_table WHERE value = 'ThisHamalConnectionRockz'")
        }

        private val testInstance =
            DefaultConnection(
                ExecuteUpdateTest::class,
                "jdbc:sqlite:${Files.createTempDirectory("execute-update")}/db.sqlite"
            )

        init {
            testInstance.execute("""PRAGMA journal_mode = wal;""")
            testInstance.execute("""PRAGMA locking_mode = exclusive;""")
            testInstance.execute("""PRAGMA temp_store = memory;""")
            testInstance.execute("""PRAGMA synchronous = off;""")

            testInstance.execute("""CREATE TABLE boolean_table(value BOOLEAN NOT NULL)""")
            testInstance.execute("""CREATE TABLE int_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE long_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE string_table(id INT PRIMARY KEY, value TEXT NOT NULL)""")
            testInstance.execute("""CREATE TABLE instant_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE snowflake_id_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE domain_id_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE request_id_table(value INT NOT NULL)""")
        }

        private fun verifyIsOne(query: String) {
            testInstance.delegate.createStatement().use { stmt ->
                stmt.executeQuery(query).use {
                    assertThat(it.getInt(1), equalTo(1))
                }
            }
        }
    }

    @Nested
    @DisplayName("executeQuery")
    inner class ExecuteQueryTest {

        @Test
        fun `Named parameter does not exists in query`() {
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.executeQuery<Boolean>("SELECT value FROM boolean_table WHERE value = :some_value") {
                    with {
                        set("does_not_exists", false)
                    }
                    map { it.getBoolean("value") }
                }
            }
            assertThat(exception.message, equalTo("Statement does not contain parameter does_not_exists"))
        }

        @Test
        fun `Named parameter does not in result set`() {
            testInstance.execute("INSERT INTO boolean_table(value)VALUES(true)")

            val exception = assertThrows<IllegalArgumentException> {
                testInstance.executeQuery<Boolean>("SELECT value FROM boolean_table WHERE value = :some_value") {
                    with {
                        set("some_value", true)
                    }
                    map { it.getBoolean("does_not_exists") }
                }
            }
            assertThat(exception.message, equalTo("Parameter 'does_not_exists' does not exist in result set"))
        }

        @Test
        fun `ExecuteQuery provided with consumer`() {
            testInstance.execute("INSERT INTO boolean_table(value)VALUES(true)")

            testInstance.executeQuery("SELECT value FROM boolean_table WHERE value = true") {
                assertThat(it.getBoolean("value"), equalTo(true))
            }
        }

        @Test
        fun `With single named parameter`() {
            testInstance.execute("INSERT INTO boolean_table(value)VALUES(true)")
            data class BooleanResult(val value: Boolean)

            val result =
                testInstance.executeQuery<BooleanResult>("SELECT value FROM boolean_table WHERE value = :some_value") {
                    with {
                        set("some_value", true)
                    }
                    map {
                        BooleanResult(
                            value = it.getBoolean("value")
                        )
                    }
                }

            assertThat(result, equalTo(listOf(BooleanResult(true))))
        }

        private val testInstance = DefaultConnection(
            ExecuteQueryTest::class,
            "jdbc:sqlite:${Files.createTempDirectory("execute-query")}/db.sqlite"
        )

        init {
            testInstance.execute("""PRAGMA journal_mode = wal;""")
            testInstance.execute("""PRAGMA locking_mode = exclusive;""")
            testInstance.execute("""PRAGMA temp_store = memory;""")
            testInstance.execute("""PRAGMA synchronous = off;""")

            testInstance.execute("""CREATE TABLE boolean_table(value BOOLEAN NOT NULL)""")
            testInstance.execute("""CREATE TABLE int_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE long_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE string_table(value TEXT NOT NULL)""")
            testInstance.execute("""CREATE TABLE instant_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE snowflake_id_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE domain_id_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE request_id_table(value INT NOT NULL)""")
        }
    }

    @Nested
    @DisplayName("tx()")
    inner class TxTest {
        @Test
        fun `Successful transaction automatically commits and returns result`() {
            val result = testInstance.tx {
                execute("INSERT INTO some_table(value) VALUES(1234)")
                execute("INSERT INTO another_table(value) VALUES (321)")
                executeQuery("SELECT * FROM another_table") {
                    map { it.getInt("value") }
                }
            }
            assertThat(result, equalTo(listOf(321)))

            verifyIsOne("SELECT COUNT(*) as count FROM some_table")
            verifyIsOne("SELECT COUNT(*) as count FROM another_table")
        }

        @Test
        fun `Exception rolls transaction back`() {
            val exception = assertThrows<RuntimeException> {
                testInstance.tx {
                    execute("INSERT INTO some_table(value) VALUES( 123)")
                    execute("INSERT INTO another_table(value) VALUES (321)")
                    throw RuntimeException("Oops")
                }
            }
            assertThat(exception.message, equalTo("Oops"))

            verifyIsZero("SELECT COUNT(*) as count FROM some_table")
            verifyIsZero("SELECT COUNT(*) as count FROM another_table")
        }

        @Test
        fun `abort() stops execution and rolls transaction back`() {
            val counter = AtomicInteger(0)
            val result = testInstance.tx {
                counter.incrementAndGet()
                execute("INSERT INTO some_table(value) VALUES( 123)")
                counter.incrementAndGet()


                abort()
                execute("INSERT INTO another_table(value) VALUES (321)")
                counter.incrementAndGet()

                throw RuntimeException("Oops")
            }
            assertThat(result, nullValue())
            assertThat(counter.get(), equalTo(2))

            verifyIsZero("SELECT COUNT(*) as count FROM some_table")
            verifyIsZero("SELECT COUNT(*) as count FROM another_table")
        }

        private fun verifyIsZero(query: String) {
            val count = testInstance.executeQuery<Int>(query) {
                map { it.getInt("count") }
            }
            assertThat(count, equalTo(listOf(0)))
        }

        private fun verifyIsOne(query: String) {
            val count = testInstance.executeQuery<Int>(query) {
                map { it.getInt("count") }
            }
            assertThat(count, equalTo(listOf(1)))
        }

        private val testInstance = DefaultConnection(
            TxTest::class,
            "jdbc:sqlite:${Files.createTempDirectory("tx")}/db.sqlite"
        )

        init {
            testInstance.execute("""PRAGMA journal_mode = wal;""")
            testInstance.execute("""PRAGMA locking_mode = exclusive;""")
            testInstance.execute("""PRAGMA temp_store = memory;""")
            testInstance.execute("""PRAGMA synchronous = off;""")

            testInstance.execute("""CREATE TABLE some_table(value INT NOT NULL)""")
            testInstance.execute("""CREATE TABLE another_table(value INT NOT NULL)""")
        }

    }


    @Nested
    @DisplayName("close()")
    inner class CloseTest {

        @Test
        fun `Closes an open connection`() {
            assertTrue(testInstance.isOpen)
            assertFalse(testInstance.isClosed)

            testInstance.close()

            assertFalse(testInstance.isOpen)
            assertTrue(testInstance.isClosed)
        }

        @Test
        fun `Tries to close a closed connection`() {
            testInstance.close()
            assertFalse(testInstance.isOpen)
            assertTrue(testInstance.isClosed)

            testInstance.close()
            testInstance.close()
            testInstance.close()
            testInstance.close()
            assertFalse(testInstance.isOpen)
            assertTrue(testInstance.isClosed)
        }

        private val testInstance = DefaultConnection(
            CloseTest::class,
            "jdbc:sqlite:${Files.createTempDirectory("connection")}/db.sqlite"
        )
    }

}