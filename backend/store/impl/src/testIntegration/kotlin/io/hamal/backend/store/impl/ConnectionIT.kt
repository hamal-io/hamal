package io.hamal.backend.store.impl

import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.nio.file.Files

class DefaultConnectionIT {

    @Nested
    @DisplayName("prepare()")
    inner class PrepareTest {
        @Test
        fun implementMe() {
            TODO()
        }
    }

    @Nested
    @DisplayName("execute()")
    inner class ExecuteTest {
        //FIXME missing parameter
        //FIXME wrong parameter name

        @Test
        fun `Execute with named parameter of type string`() {
            testInstance.execute("INSERT INTO string_table(value) VALUES(:some_value)") {
                set("some_value", "ThisHamalConnectionRockz")
            }

            verifyIsOne("SELECT COUNT(*) FROM string_table WHERE value = 'ThisHamalConnectionRockz'")
        }

        private val testInstance = DefaultConnection("jdbc:sqlite:${Files.createTempDirectory("execute")}/db.sqlite")

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

        private fun verifyIsOne(query: String) {
            testInstance.delegate.createStatement().use { stmt ->
                stmt.executeQuery(query).use {
                    assertThat(it.getInt(1), equalTo(1))
                }
            }
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

        private val testInstance = DefaultConnection("jdbc:sqlite:${Files.createTempDirectory("connection")}/db.sqlite")
    }

}