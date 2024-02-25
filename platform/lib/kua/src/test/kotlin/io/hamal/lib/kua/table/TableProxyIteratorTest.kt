package io.hamal.lib.kua.table

import io.hamal.lib.kua.ClosableState
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.type.KuaNumber
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableEntryIteratorTest {

    @Test
    fun `Empty table`() {
        val emptyTableIndex = state.native.tableCreate(0, 0)
        val testInstance = TableEntryIterator(
            index = emptyTableIndex,
            state = state,
            keyExtractor = { state, index -> state.getStringType(index) },
            valueExtractor = { state, index -> state.getStringType(index) }
        )
        assertThat(testInstance.hasNext(), equalTo(false))

        val exception = assertThrows<NoSuchElementException> {
            testInstance.next()
        }
        assertThat(exception.message, equalTo("Iterator exhausted"))
    }

    @Test
    fun `Table with single element`() {
        val table = state.tableCreateMap(123)
        table.append(21)

        val testInstance = TableEntryIterator(
            index = table.index,
            state = state,
            keyExtractor = { state, index -> state.getNumberType(index) },
            valueExtractor = { state, index -> state.getNumberType(index) }
        )
        assertThat(testInstance.hasNext(), equalTo(true))

        val entry = testInstance.next()
        assertThat(entry.key, equalTo(KuaNumber(1)))
        assertThat(entry.value, equalTo(KuaNumber(21)))

        assertThat(testInstance.hasNext(), equalTo(false))

        val exception = assertThrows<NoSuchElementException> {
            testInstance.next()
        }
        assertThat(exception.message, equalTo("Iterator exhausted"))
    }


    @Test
    fun `Table with multiple elements`() {
        val table = state.tableCreateMap(0)
        repeat(10) { idx ->
            table.append(idx)
            println(idx)
        }

        val testInstance = TableEntryIterator(
            index = table.index,
            state = state,
            keyExtractor = { state, index -> state.getNumberType(index) },
            valueExtractor = { state, index -> state.getNumberType(index) }
        )

        repeat(10) { idx ->
            assertThat(testInstance.hasNext(), equalTo(true))
            val entry = testInstance.next()
            assertThat(entry.key, equalTo(KuaNumber(idx + 1)))
            assertThat(entry.value, equalTo(KuaNumber(idx)))
        }

        assertThat(testInstance.hasNext(), equalTo(false))

        val exception = assertThrows<NoSuchElementException> { testInstance.next() }
        assertThat(exception.message, equalTo("Iterator exhausted"))
    }


    private val state = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        ClosableState(Sandbox(NopSandboxContext()).native)
    }
}