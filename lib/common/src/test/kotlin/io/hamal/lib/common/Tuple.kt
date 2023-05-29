package io.hamal.lib.common

import io.hamal.lib.domain.Tuple0
import io.hamal.lib.domain.Tuple1
import io.hamal.lib.domain.Tuple2
import io.hamal.lib.domain.Tuple3
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Nested
class TupleTest {
    @Nested
    inner class Tuple0Test {
        @Nested
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
        inner class ToStringTest {
            @Test
            fun ok() {
                val testInstance = Tuple0()
                val result = testInstance.toString()
                assertThat(result, equalTo("()"))
            }
        }

        @Nested
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
    inner class Tuple1Test {
        @Nested
        inner class EqualsTest {
            @Test
            fun `Equal if underlying values are equal`() {
                assertEquals(
                    Tuple1(true),
                    Tuple1(true)
                )
            }

            @Test
            fun `Not Equal if underlying values are different`() {
                assertNotEquals(
                    Tuple1(true),
                    Tuple1(false)
                )
            }
        }

        @Nested
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
        inner class ToStringTest {
            @Test
            fun ok() {
                val testInstance = Tuple1("ABC")
                val result = testInstance.toString()
                assertThat(result, equalTo("(ABC)"))
            }
        }

        @Nested
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
    inner class Tuple2Test {
        @Nested
        inner class EqualsTest {
            @Test
            fun `Equal if underlying values are equal`() {
                assertEquals(
                    Tuple2(true, "2810"),
                    Tuple2(true, "2810")
                )
            }

            @Test
            fun `Not Equal if underlying values are different`() {
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
        inner class ToStringTest {
            @Test
            fun ok() {
                val testInstance = Tuple2("ABC", 42)
                val result = testInstance.toString()
                assertThat(result, equalTo("(ABC,42)"))
            }
        }

        @Nested
        inner class SizeTest {
            @Test
            fun ok() {
                val testInstance = Tuple2(1, 4)
                val result = testInstance.size
                assertThat(result, equalTo(2))
            }
        }

    }

    @Nested
    inner class Tuple3Test {
        @Nested
        inner class EqualsTest {
            @Test
            fun `Equal if underlying values are equal`() {
                assertEquals(
                    Tuple3(true, "2810", 1212),
                    Tuple3(true, "2810", 1212)
                )
            }

            @Test
            fun `Not Equal if underlying values are different`() {
                assertNotEquals(
                    Tuple3(true, "2810", 1212),
                    Tuple3(false, "2810", 1212)
                )
                assertNotEquals(
                    Tuple3(true, "2810", 1212),
                    Tuple3(true, "1038", 1212)
                )
                assertNotEquals(
                    Tuple3(true, "2810", 1212),
                    Tuple3(true, "2810", 1506)
                )
            }
        }

        @Nested
        inner class HashCodeTest {
            @Test
            fun `Same hashcode if values are equal`() {
                assertEquals(
                    Tuple3(true, "2810", 1212).hashCode(),
                    Tuple3(true, "2810", 1212).hashCode()
                )
            }

            @Test
            fun `Different hashcode if values are different`() {
                assertNotEquals(
                    Tuple3(true, "2810", 1212).hashCode(),
                    Tuple3(false, "2810", 1212).hashCode()
                )
                assertNotEquals(
                    Tuple3(true, "2810", 1212).hashCode(),
                    Tuple3(true, "1038", 1212).hashCode()
                )
                assertNotEquals(
                    Tuple3(true, "2810", 1212).hashCode(),
                    Tuple3(true, "2810", 1506).hashCode()
                )
            }
        }

        @Nested
        inner class ToStringTest {
            @Test
            fun ok() {
                val testInstance = Tuple3("ABC", 43, true)
                val result = testInstance.toString()
                assertThat(result, equalTo("(ABC,43,true)"))
            }
        }

        @Nested
        inner class SizeTest {
            @Test
            fun ok() {
                val testInstance = Tuple3(1, 4, 100)
                val result = testInstance.size
                assertThat(result, equalTo(3))
            }
        }

    }
}