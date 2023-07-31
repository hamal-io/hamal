package io.hamal.lib.kua.table

import io.hamal.lib.kua.ClosableState
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.value.NumberValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableEntryIteratorTest {

    @Test
    fun `Empty table`() {
        val emptyTableIndex = state.bridge.tableCreate(0, 0)
        val testInstance = TableEntryIterator(
            index = emptyTableIndex,
            state = state,
            keyExtractor = { state, index -> state.getStringValue(index) },
            valueExtractor = { state, index -> state.getStringValue(index) }
        )
        assertThat(testInstance.hasNext(), equalTo(false))

        val exception = assertThrows<NoSuchElementException> {
            testInstance.next()
        }
        assertThat(exception.message, equalTo("Iterator exhausted"))
    }

    @Test
    fun `Table with single element`() {
        val table = state.tableCreateArray(123)
        table.append(21)

        val testInstance = TableEntryIterator(
            index = table.index,
            state = state,
            keyExtractor = { state, index -> state.getNumberValue(index) },
            valueExtractor = { state, index -> state.getNumberValue(index) }
        )
        assertThat(testInstance.hasNext(), equalTo(true))

        val entry = testInstance.next()
        assertThat(entry.key, equalTo(NumberValue(1)))
        assertThat(entry.value, equalTo(NumberValue(21)))

        assertThat(testInstance.hasNext(), equalTo(false))

        val exception = assertThrows<NoSuchElementException> {
            testInstance.next()
        }
        assertThat(exception.message, equalTo("Iterator exhausted"))
    }


    @Test
    fun `Table with multiple elements`() {
        val table = state.tableCreateArray(0)
        repeat(10) { idx ->
            table.append(idx)
            println(idx)
        }

        val testInstance = TableEntryIterator(
            index = table.index,
            state = state,
            keyExtractor = { state, index -> state.getNumberValue(index) },
            valueExtractor = { state, index -> state.getNumberValue(index) }
        )

        repeat(10) { idx ->
            assertThat(testInstance.hasNext(), equalTo(true))
            val entry = testInstance.next()
            assertThat(entry.key, equalTo(NumberValue(idx + 1)))
            assertThat(entry.value, equalTo(NumberValue(idx)))
        }

        assertThat(testInstance.hasNext(), equalTo(false))

        val exception = assertThrows<NoSuchElementException> { testInstance.next() }
        assertThat(exception.message, equalTo("Iterator exhausted"))
    }


    private val state = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        ClosableState(Sandbox().bridge)
    }
}