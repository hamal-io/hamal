package io.hamal.lib.common

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.nullValue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TreeTest {

    @Nested
    inner class FindTest {

        @Test
        fun `Finds value in root node`() {
            val result = testInstance.find { it.value.id == 10L }
            assertThat(result?.value?.value, equalTo("1"))
        }

        @Test
        fun `Finds value in direct descendant of root node`() {
            val result = testInstance.find { it.value.id == 23L }
            assertThat(result?.value?.value, equalTo("3"))
        }

        @Test
        fun `Finds tail node`() {
            val result = testInstance.find { it.value.id == 34L }
            assertThat(result?.value?.value, equalTo("4"))
        }

        @Test
        fun `Tries to find node which does not exists`() {
            val result = testInstance.find { it.value.id == 99L }
            assertThat(result, nullValue())
        }

        private val testInstance = TreeNode.Builder(NodeValue(10L, "1"))
            .descendant(NodeValue(12L, "2"))
            .fork(NodeValue(23L, "3")) {
                it.descendant(NodeValue(34, "4"))
            }
            .build()

    }

    private data class NodeValue(val id: Long, val value: String)
}

