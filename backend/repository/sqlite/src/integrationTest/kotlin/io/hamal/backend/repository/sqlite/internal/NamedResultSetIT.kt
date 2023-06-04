package io.hamal.backend.repository.sqlite.internal

import io.hamal.lib.common.SnowflakeId
import io.hamal.lib.domain.CmdId
import io.hamal.lib.domain.vo.base.DomainId
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.sql.Connection
import java.sql.DriverManager
import java.time.Instant

class DefaultNamedResultSetIT {
    companion object {
        val connection: Connection = DriverManager.getConnection(
            "jdbc:sqlite:${Files.createTempDirectory("named-resultset-test")}/test.db"
        )

        init {
            connection.createStatement().use {
                it.execute("""PRAGMA journal_mode = wal;""")
                it.execute("""PRAGMA locking_mode = exclusive;""")
                it.execute("""PRAGMA temp_store = memory;""")
                it.execute("""PRAGMA synchronous = off;""")
            }

            connection.createStatement().use {
                it.execute(
                    """CREATE TABLE some_table(
                    |boolean_value BOOLEAN,
                    |int_value INT,
                    |long_value INT,
                    |instant_value INT,
                    |snowflake_id_value INT,
                    |domain_id_value INT,
                    |compute_id_value INT,
                    |null_value INT,
                    |string_value TEXT,
                    |blob_value BLOB
                    |)""".trimMargin()
                )

            }
        }
    }

    @Test
    fun getBoolean() {
        connection.createStatement().use { it.execute("INSERT INTO some_table (boolean_value) VALUES (true)") }
        val testInstance = testInstance("SELECT boolean_value FROM some_table WHERE boolean_value is not null")
        assertThat(testInstance.getBoolean("boolean_value"), equalTo(true))
    }

    @Test
    fun getInt() {
        connection.createStatement().use { it.execute("INSERT INTO some_table (int_value) VALUES (1234)") }
        val testInstance = testInstance("SELECT int_value FROM some_table WHERE int_value is not null")
        assertThat(testInstance.getInt("int_value"), equalTo(1234))
    }

    @Test
    fun getLong() {
        connection.createStatement().use { it.execute("INSERT INTO some_table (long_value) VALUES (123456789)") }
        val testInstance = testInstance("SELECT long_value FROM some_table WHERE long_value is not null")
        assertThat(testInstance.getLong("long_value"), equalTo(123456789L))
    }

    @Test
    fun getString() {
        connection.createStatement().use { it.execute("INSERT INTO some_table (string_value) VALUES ('h4m4l')") }
        val testInstance = testInstance("SELECT string_value FROM some_table WHERE string_value is not null")
        assertThat(testInstance.getString("string_value"), equalTo("h4m4l"))
    }

    @Test
    fun getInstant() {
        connection.createStatement().use { it.execute("INSERT INTO some_table (instant_value) VALUES (987654)") }
        val testInstance = testInstance("SELECT instant_value FROM some_table WHERE instant_value is not null")
        assertThat(testInstance.getInstant("instant_value"), equalTo(Instant.ofEpochMilli(987654)))
    }

    @Test
    fun getSnowflakeId() {
        connection.createStatement().use { it.execute("INSERT INTO some_table (snowflake_id_value) VALUES (987654)") }
        val testInstance =
            testInstance("SELECT snowflake_id_value FROM some_table WHERE snowflake_id_value is not null")
        assertThat(testInstance.getSnowflakeId("snowflake_id_value"), equalTo(SnowflakeId(987654)))
    }

    @Test
    fun getCommandId() {
        connection.createStatement().use { it.execute("INSERT INTO some_table (compute_id_value) VALUES (1234567890)") }
        val testInstance = testInstance(
            "SELECT compute_id_value FROM some_table WHERE compute_id_value is not null"
        )
        assertThat(testInstance.getCommandId("compute_id_value"), equalTo(CmdId(1234567890)))
    }

    @Test
    fun getDomainId() {
        data class TestDomainId(override val value: SnowflakeId) : DomainId() {
            constructor(value: Int) : this(SnowflakeId(value.toLong()))
        }
        connection.createStatement().use { it.execute("INSERT INTO some_table (domain_id_value) VALUES (54321)") }
        val testInstance = testInstance(
            "SELECT domain_id_value FROM some_table WHERE domain_id_value is not null"
        )
        assertThat(testInstance.getDomainId("domain_id_value", ::TestDomainId), equalTo(TestDomainId(54321)))
    }

    @Test
    fun getBytes() {
        connection.createStatement().use { it.execute("INSERT INTO some_table (blob_value) VALUES ('some_blob')") }
        val testInstance = testInstance(
            "SELECT blob_value FROM some_table WHERE blob_value is not null"
        )
        assertThat(testInstance.getBytes("blob_value"), equalTo("some_blob".toByteArray()))
    }

    private fun testInstance(query: String): DefaultNamedResultSet {
        return DefaultNamedResultSet(connection.createStatement().executeQuery(query))
    }

}
