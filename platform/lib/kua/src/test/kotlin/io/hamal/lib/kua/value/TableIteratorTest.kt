import io.hamal.lib.kua.*
import io.hamal.lib.kua.value.KuaTableIterator
import io.hamal.lib.kua.value.getNumber
import io.hamal.lib.kua.value.set
import io.hamal.lib.common.value.ValueNumber
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class KuaTableEntryIteratorTest {

    @Test
    fun `Empty table`() {
        state.tableCreate(0, 0)
        val testInstance = KuaTableIterator(
            index = ValueNumber(1),
            state = state,
            keyExtractor = { state, index -> state.stringGet(index) },
            valueExtractor = { state, index -> state.stringGet(index) }
        )

        assertThat(testInstance.hasNext(), equalTo(false))

        assertThrows<NoSuchElementException> { testInstance.next() }.also { exception ->
            assertThat(exception.message, equalTo("Iterator exhausted"))
        }
    }

    @Test
    fun `Table with single element`() {
        state.tableCreate(listOf(ValueNumber(21)))

        val testInstance = KuaTableIterator(
            index = ValueNumber(1),
            state = state,
            keyExtractor = { state, index -> state.numberGet(index) },
            valueExtractor = { state, index -> state.numberGet(index) }
        )
        assertThat(testInstance.hasNext(), equalTo(true))

        val entry = testInstance.next()
        assertThat(entry.key, equalTo(ValueNumber(1)))
        assertThat(entry.value, equalTo(ValueNumber(21)))

        assertThat(testInstance.hasNext(), equalTo(false))

        val exception = assertThrows<NoSuchElementException> {
            testInstance.next()
        }
        assertThat(exception.message, equalTo("Iterator exhausted"))
    }


    @Test
    fun `Array table with multiple elements`() {
        val table = state.tableCreate(0, 0)
        repeat(10) { idx ->
            table.append(ValueNumber(idx))
        }

        val testInstance = KuaTableIterator(
            index = table.index,
            state = state,
            keyExtractor = { state, index -> state.numberGet(index) },
            valueExtractor = { state, index -> state.numberGet(index) }
        )

        repeat(10) { idx ->
            assertThat(testInstance.hasNext(), equalTo(true))

            val entry = testInstance.next()
            assertThat(entry.key, equalTo(ValueNumber(idx + 1)))
            assertThat(entry.value, equalTo(ValueNumber(idx)))
        }

        assertThat(testInstance.hasNext(), equalTo(false))

        assertThrows<NoSuchElementException> { testInstance.next() }.also { exception ->
            assertThat(exception.message, equalTo("Iterator exhausted"))
        }
    }

    @Test
    fun `Nested table`() {
        state.tableCreate(0, 0).also { table ->
            repeat(10) { idx ->
                table.append(state.tableCreate().also { inner ->
                    inner["value"] = ValueNumber(idx)
                })
            }

        }

        val testInstance = KuaTableIterator(
            index = ValueNumber(1),
            state = state,
            keyExtractor = { state, index -> state.numberGet(index) },
            valueExtractor = { state, index ->
                state.tableGet(index)
                state.referenceAcquire().also {
                    state.nilPush()
                }
            }
        )

        repeat(10) { idx ->
            assertThat(testInstance.hasNext(), equalTo(true))
            val entry = testInstance.next()
            assertThat(entry.key, equalTo(ValueNumber(idx + 1)))

            state.referencePush(entry.value)
            state.tableGet(-1).also { table ->
                assertThat(table.getNumber("value"), equalTo(ValueNumber(idx)))
                state.topPop(2)
            }
        }

        assertThat(testInstance.hasNext(), equalTo(false))

        val exception = assertThrows<NoSuchElementException> { testInstance.next() }
        assertThat(exception.message, equalTo("Iterator exhausted"))
    }


    private val state = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(SandboxContextNop)
    }
}