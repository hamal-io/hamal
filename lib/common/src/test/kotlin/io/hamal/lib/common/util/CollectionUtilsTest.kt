package io.hamal.lib.common.util

import io.hamal.lib.common.util.CollectionUtils.cross
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CollectionUtilsTest {
    @Nested
    
    inner class CrossTest {
        @Test
        fun `Creates cartesian product`() {
            val lhs = sequenceOf("A", "B")
            val rhs = sequenceOf(1, 2)

            val result = lhs.cross(rhs).toList()
            assertThat(
                result, equalTo(
                    listOf(
                        Pair("A", 1),
                        Pair("A", 2),
                        Pair("B", 1),
                        Pair("B", 2)
                    )
                )
            )
        }

        @Test
        fun `Left hand side is empty`() {
            val lhs = emptySequence<String>()
            val rhs = sequenceOf(1, 2)

            val result = lhs.cross(rhs).toList()
            assertThat(result, empty())
        }

        @Test
        fun `Right hand side is empty`() {
            val lhs = sequenceOf("A", "B")
            val rhs = emptySequence<Int>()

            val result = lhs.cross(rhs).toList()
            assertThat(result, empty())
        }

        @Test
        fun `Left hand side has more elements than right hand side`() {
            val lhs = sequenceOf("A", "B")
            val rhs = sequenceOf(1)

            val result = lhs.cross(rhs).toList()
            assertThat(
                result, equalTo(
                    listOf(
                        Pair("A", 1),
                        Pair("B", 1)
                    )
                )
            )
        }

        @Test
        fun `Right hand side has more elements than left hand side`() {
            val lhs = sequenceOf("A")
            val rhs = sequenceOf(1, 2)

            val result = lhs.cross(rhs).toList()
            assertThat(
                result, equalTo(
                    listOf(
                        Pair("A", 1),
                        Pair("A", 2)
                    )
                )
            )
        }
    }


}