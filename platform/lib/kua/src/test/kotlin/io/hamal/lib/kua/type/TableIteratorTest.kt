//package io.hamal.lib.kua.type
//
//import io.hamal.lib.kua.*
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.equalTo
//import org.junit.jupiter.api.Disabled
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//
//internal class KuaTableEntryIteratorTest {
//
//    @Test
//    fun `Empty table`() {
//        val emptyTableIndex = state.native.tableCreate(0, 0)
//        val testInstance = KuaTableEntryIterator(
//            index = emptyTableIndex,
//            state = state,
//            keyExtractor = { state, index -> state.getStringType(index) },
//            valueExtractor = { state, index -> state.getStringType(index) }
//        )
//        assertThat(testInstance.hasNext(), equalTo(false))
//
//        val exception = assertThrows<NoSuchElementException> {
//            testInstance.next()
//        }
//        assertThat(exception.message, equalTo("Iterator exhausted"))
//    }
//
//    @Test
//    fun `Array table with single element`() {
//        val table = state.tableCreateArray(123)
//        table.append(21)
//
//        val testInstance = KuaTableEntryIterator(
//            index = table.index,
//            state = state,
//            keyExtractor = { state, index -> state.getNumberType(index) },
//            valueExtractor = { state, index -> state.getNumberType(index) }
//        )
//        assertThat(testInstance.hasNext(), equalTo(true))
//
//        val entry = testInstance.next()
//        assertThat(entry.key, equalTo(KuaNumber(1)))
//        assertThat(entry.value, equalTo(KuaNumber(21)))
//
//        assertThat(testInstance.hasNext(), equalTo(false))
//
//        val exception = assertThrows<NoSuchElementException> {
//            testInstance.next()
//        }
//        assertThat(exception.message, equalTo("Iterator exhausted"))
//    }
//
//
//    @Test
//    fun `Array table with multiple elements`() {
//        val table = state.tableCreateArray(0)
//        repeat(10) { idx ->
//            table.append(idx)
//        }
//
//        val testInstance = KuaTableEntryIterator(
//            index = table.index,
//            state = state,
//            keyExtractor = { state, index -> state.getNumberType(index) },
//            valueExtractor = { state, index -> state.getNumberType(index) }
//        )
//
//        repeat(10) { idx ->
//            assertThat(testInstance.hasNext(), equalTo(true))
//            val entry = testInstance.next()
//            assertThat(entry.key, equalTo(KuaNumber(idx + 1)))
//            assertThat(entry.value, equalTo(KuaNumber(idx)))
//        }
//
//        assertThat(testInstance.hasNext(), equalTo(false))
//
//        val exception = assertThrows<NoSuchElementException> { testInstance.next() }
//        assertThat(exception.message, equalTo("Iterator exhausted"))
//    }
//
//    @Test
//    @Disabled
//    fun `Array table with map table`() {
////        val table = state.tableCreateArray(0)
////        repeat(10) { idx ->
////            table.append(state.tableCreateMap(1).also { inner ->
////                inner["value"] = idx
////            })
////        }
////
////        val testInstance = KuaTableEntryIterator(
////            index = table.index,
////            state = state,
////            keyExtractor = { state, index -> state.getNumberType(index) },
////            valueExtractor = { state, index -> state.getTableMap(index) }
////        )
////
////        repeat(10) { idx ->
////            assertThat(testInstance.hasNext(), equalTo(true))
////            val entry = testInstance.next()
////            assertThat(entry.key, equalTo(KuaNumber(idx + 1)))
////            assertThat(entry.value, equalTo(KuaNumber(idx)))
////
////            println(entry.value.index)
////            println(state.type(entry.value.index - 2))
////            println(entry.value.getInt("value"))
////
////        }
////
////        assertThat(testInstance.hasNext(), equalTo(false))
////
////        val exception = assertThrows<NoSuchElementException> { testInstance.next() }
////        assertThat(exception.message, equalTo("Iterator exhausted"))
//    }
//
//
//    private val state = run {
//        NativeLoader.load(NativeLoader.Preference.Resources)
//        Sandbox(NopSandboxContext())
//    }
//}