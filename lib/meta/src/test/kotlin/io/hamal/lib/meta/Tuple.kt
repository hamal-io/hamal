package io.hamal.lib.meta

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

@Nested
class TupleTest {

    @Nested
    @DisplayName("Tuple0")
    inner class Tuple0Test {

        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Always equals`() {
                assertEquals(
                    Tuple0(),
                    Tuple0()
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Always same hashcode`() {
                assertEquals(
                    Tuple0().hashCode(),
                    Tuple0().hashCode()
                )
            }
        }

        @Nested
        @DisplayName("toString()")
        inner class ToStringTest {
            @Test
            fun ok() {
                val testInstance = Tuple0()
                val result = testInstance.toString()
                assertThat(result, equalTo("()"))
            }
        }

        @Nested
        @DisplayName("size()")
        inner class SizeTest {
            @Test
            fun ok() {
                val testInstance = Tuple0()
                val result = testInstance.size
                assertThat(result, equalTo(0))
            }
        }
    }

    @Nested
    @DisplayName("Tuple1")
    inner class Tuple1Test {

        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    Tuple1(true),
                    Tuple1(true)
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    Tuple1(true),
                    Tuple1(false)
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    Tuple1(true).hashCode(),
                    Tuple1(true).hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    Tuple1(true).hashCode(),
                    Tuple1(false).hashCode()
                )
            }
        }

        @Nested
        @DisplayName("toString()")
        inner class ToStringTest {
            @Test
            fun ok() {
                val testInstance = Tuple1("ABC")
                val result = testInstance.toString()
                assertThat(result, equalTo("(ABC)"))
            }
        }

        @Nested
        @DisplayName("size()")
        inner class SizeTest {
            @Test
            fun ok() {
                val testInstance = Tuple1(5)
                val result = testInstance.size
                assertThat(result, equalTo(1))
            }
        }

    }

    @Nested
    @DisplayName("Tuple2")
    inner class Tuple2Test {

        @Nested
        @DisplayName("equals()")
        inner class EqualsTest {
            @Test
            fun `Equals if underlying values are equal`() {
                assertEquals(
                    Tuple2(true, "2810"),
                    Tuple2(true, "2810")
                )
            }

            @Test
            fun `Not equals if underlying values are different`() {
                assertNotEquals(
                    Tuple2(true, "2810"),
                    Tuple2(false, "2810")
                )
                assertNotEquals(
                    Tuple2(true, "2810"),
                    Tuple2(true, "1028")
                )
            }
        }

        @Nested
        @DisplayName("hashCode()")
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    Tuple2(true, "2810").hashCode(),
                    Tuple2(true, "2810").hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    Tuple2(true, "2810").hashCode(),
                    Tuple2(false, "2810").hashCode()
                )
                assertNotEquals(
                    Tuple2(true, "2810").hashCode(),
                    Tuple2(true, "1028").hashCode()
                )
            }
        }

        @Nested
        @DisplayName("toString()")
        inner class ToStringTest {
            @Test
            fun ok() {
                val testInstance = Tuple2("ABC", 42)
                val result = testInstance.toString()
                assertThat(result, equalTo("(ABC,42)"))
            }
        }

        @Nested
        @DisplayName("size()")
        inner class SizeTest {
            @Test
            fun ok() {
                val testInstance = Tuple2(1, 4)
                val result = testInstance.size
                assertThat(result, equalTo(2))
            }
        }

    }

}