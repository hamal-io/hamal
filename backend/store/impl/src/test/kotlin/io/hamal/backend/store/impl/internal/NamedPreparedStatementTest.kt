package io.hamal.backend.store.impl.internal

import io.hamal.backend.store.impl.internal.DefaultNamedPreparedStatement.Companion.prepare
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.sql.Connection
import java.sql.DriverManager

class ParseTest {

    @Nested
    @DisplayName("parse()")
    inner class ParseTest {
        @Test
        fun `Empty string`() {
            val result = Parser().parse("")
            assertThat(result.sql, equalTo(""))
            assertThat(result.orderedParameters, empty())
        }

        @Test
        fun `Single whitespace`() {
            val result = Parser().parse(" ")
            assertThat(result.sql, equalTo(""))
            assertThat(result.orderedParameters, empty())
        }

        @Test
        fun `Statement does not contain parameter`() {
            val result = Parser().parse(
                "SELECT COUNT(*) from table WHERE id = 10"
            )
            assertThat(result.sql, equalTo("SELECT COUNT(*) from table WHERE id = 10"))
            assertThat(result.orderedParameters, empty())
        }

        @Test
        fun `Single parameter`() {
            val result = Parser().parse(
                "SELECT COUNT(*) from table WHERE id = :id"
            )
            assertThat(result.sql, equalTo("SELECT COUNT(*) from table WHERE id = ?"))
            assertThat(result.orderedParameters, equalTo(listOf("id")))
        }

        @Test
        fun `Multiple unique parameters`() {
            val result = Parser().parse(
                "SELECT COUNT(*) from table WHERE id = :id and x = :some_value and y >= :another_value"
            )
            assertThat(result.sql, equalTo("SELECT COUNT(*) from table WHERE id = ? and x = ? and y >= ?"))
            assertThat(result.orderedParameters, equalTo(listOf("id", "some_value", "another_value")))
        }

        @Test
        fun `Query contains double colon`() {
            val result = Parser().parse(
                "SELECT '{1,2,3,4,5}'::int[] WHERE some_field = :some_field_value"
            )
            assertThat(result.sql, equalTo("SELECT '{1,2,3,4,5}'::int[] WHERE some_field = ?"))
            assertThat(result.orderedParameters, equalTo(listOf("some_field_value")))
        }

        @Test
        fun `Similar named parameters`() {
            val result = Parser().parse(
                """
                    SELECT col1, col2 
                    FROM table
                    WHERE id = :named_parameter1
                    AND name = :named_parameter2;
                """.trimIndent()
            )
            assertThat(result.sql, equalTo("SELECT col1, col2 FROM table WHERE id = ? AND name = ? ;"))
            assertThat(result.orderedParameters, equalTo(listOf("named_parameter1", "named_parameter2")))
        }

        @Test
        fun `Parsing single quote`() {
            val result = Parser().parse(
                """ 'execute :me and :me2' """
            )
            assertThat(result.sql, equalTo("'execute :me and :me2'"))
            assertThat(result.orderedParameters, empty())
        }

        @Test
        fun `Parsing double quote`() {
            val result = Parser().parse(
                """ "execute :me and :me2" """
            )
            assertThat(
                result.sql, equalTo(
                    """
                "execute :me and :me2"
            """.trimIndent()
                )
            )
            assertThat(result.orderedParameters, empty())
        }

        @Test
        fun `Parsing of a more complex query`() {
            val result = Parser().parse(
                """
                    SELECT * FROM table WHERE afield = ':not me' AND bfield = :param1 AND cfield = :param2 and dfield = :param2;
                """.trimIndent()
            )
            assertThat(
                result.sql,
                equalTo("SELECT * FROM table WHERE afield = ':not me' AND bfield = ? AND cfield = ? and dfield = ? ;")
            )
            assertThat(result.orderedParameters, equalTo(listOf("param1", "param2", "param2")))
        }

        @Test
        fun `Insert query`() {
            val result = Parser().parse(
                """
                    INSERT INTO string_table(value, another_value) VALUES(:some_value, :another_value)
                """.trimIndent()
            )
            assertThat(
                result.sql,
                equalTo("INSERT INTO string_table(value, another_value) VALUES( ? , ? )")
            )
            assertThat(result.orderedParameters, equalTo(listOf("some_value", "another_value")))
        }

        @Test
        fun `Single line comment with named parameter - which should be ignored`() {
            val result = Parser().parse("""-- :named_parameters in comments should be ignored""")
            assertThat(result.sql, equalTo(""))
            assertThat(result.orderedParameters, empty())
        }

        @Test
        fun `Multiline comment with named parameter - which should be ignored`() {
            val result = Parser().parse(
                """
                    /*
                    Test SQL file to test correct parsing
                    :named_parameters in multiline comments should be ignored
                    */
                """.trimIndent()
            )
            assertThat(result.sql, equalTo(""))
            assertThat(result.orderedParameters, empty())
        }
    }
}

@DisplayName("DefaultNamedPreparedStatement")
class DefaultNamedPreparedStatementTest {

    companion object {
        val connection: Connection = DriverManager.getConnection("jdbc:sqlite::memory:")

        init {
            connection.createStatement().use {
                it.execute("""CREATE TABLE fantasy_table(id INT, value_1 INT, value_2 INT)""")
            }
        }
    }

    @Nested
    @DisplayName("ensureAllParametersSet()")
    inner class EnsureAllParametersSetTest {
        @Test
        fun `All parameters set`() {
            val testInstance = connection.prepare(
                "INSERT INTO fantasy_table(id, value_1,value_2) VALUES(:paramOne, :paramTwo, :paramThree)"
            ) as DefaultNamedPreparedStatement

            testInstance["paramOne"] = 1
            testInstance["paramTwo"] = 2
            testInstance["paramThree"] = 3

            testInstance.ensureAllParametersSet()
        }

        @Test
        fun `Parameters are set partially`() {
            val testInstance = connection.prepare(
                "INSERT INTO fantasy_table(id, value_1,value_2) VALUES(:paramOne, :paramTwo, :paramThree)"
            ) as DefaultNamedPreparedStatement

            testInstance["paramOne"] = false
            testInstance["paramThree"] = true

            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ensureAllParametersSet()
            }

            assertThat(
                exception.message,
                containsString("Expected all named parameters to be set, but [paramTwo] are missing")
            )
        }

        @Test
        fun `None of the parameters set`() {
            val testInstance = connection.prepare(
                "INSERT INTO fantasy_table(id, value_1,value_2) VALUES(:paramOne, :paramTwo, :paramThree)"
            ) as DefaultNamedPreparedStatement
            val exception = assertThrows<IllegalArgumentException> {
                testInstance.ensureAllParametersSet()
            }
            assertThat(
                exception.message,
                containsString("Expected all named parameters to be set, but [paramOne, paramTwo, paramThree] are missing")
            )
        }
    }
}