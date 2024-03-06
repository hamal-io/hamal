import io.hamal.lib.kua.*
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaTableIterator
import io.hamal.lib.kua.type.getNumber
import io.hamal.lib.kua.type.set
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class KuaTableEntryIteratorTest {

    @Test
    fun `Empty table`() {
        state.tableCreate(0, 0)
        val testInstance = KuaTableIterator(
            index = KuaNumber(1),
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
        state.tableCreate(listOf(KuaNumber(21)))

        val testInstance = KuaTableIterator(
            index = KuaNumber(1),
            state = state,
            keyExtractor = { state, index -> state.numberGet(index) },
            valueExtractor = { state, index -> state.numberGet(index) }
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
    fun `Array table with multiple elements`() {
        val table = state.tableCreate(0, 0)
        repeat(10) { idx ->
            table.append(KuaNumber(idx))
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
            assertThat(entry.key, equalTo(KuaNumber(idx + 1)))
            assertThat(entry.value, equalTo(KuaNumber(idx)))
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
                    inner["value"] = KuaNumber(idx)
                })
            }

        }

        val testInstance = KuaTableIterator(
            index = KuaNumber(1),
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
            assertThat(entry.key, equalTo(KuaNumber(idx + 1)))

            state.referencePush(entry.value)
            state.tableGet(-1).also { table ->
                assertThat(table.getNumber("value"), equalTo(KuaNumber(idx)))
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