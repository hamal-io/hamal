//package io.hamal.lib.kua.type
//
//import io.hamal.lib.kua.NativeLoader
//import io.hamal.lib.kua.Sandbox
//import io.hamal.lib.kua.SandboxContextNop
//import io.hamal.lib.kua.tableCreate
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.equalTo
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//
//internal class KuaTableIteratorTest {
//
//    @Test
//    fun `Empty table`() {
//        state.tableCreate(0, 0)
//
//        val testInstance = KuaTableIterator(
//            index = 1,
//            state = state,
//            keyExtractor = { state, index -> state.stringGet(index) },
//            valueExtractor = { state, index -> state.stringGet(index) }
//        )
//        assertThat(testInstance.hasNext(), equalTo(false))
//        assertThrows<NoSuchElementException> {
//            testInstance.next()
//        }.also { exception -> assertThat(exception.message, equalTo("Iterator exhausted")) }
//    }
//
//    @Test
//    fun `Array table with single element`() {
//        val table = state.tableCreate(arrayCount = 123, recordCount = 0)
//        table.append(KuaNumber(21))
//
//        val testInstance = KuaTableIterator(
//            index = table.index,
//            state = state,
//            keyExtractor = { state, index -> state.numberGet(index) },
//            valueExtractor = { state, index -> state.numberGet(index) }
//        )
//        assertThat(testInstance.hasNext(), equalTo(true))
//
//        val entry = testInstance.next()
//        assertThat(entry.key, equalTo(KuaNumber(1)))
//        assertThat(entry.value, equalTo(KuaNumber(21)))
//
//        assertThat(testInstance.hasNext(), equalTo(false))
////
////        val exception = assertThrows<NoSuchElementException> {
////            testInstance.next()
////        }
////        assertThat(exception.message, equalTo("Iterator exhausted"))
//    }
//
//
//    @Test
//    fun `Table with multiple elements`() {
//        val table = state.tableCreate(0, 0)
//        repeat(10) { idx ->
//            table.append(KuaNumber(idx))
//        }
//
//        val testInstance = KuaTableIterator(
//            index = table.index,
//            state = state,
//            keyExtractor = { state, index -> state.numberGet(index) },
//            valueExtractor = { state, index -> state.numberGet(index) }
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
//    fun `List of tables`() {
//        state.tableCreate(0, 0).also { table ->
//            repeat(10) { idx ->
//                table.append(state.tableCreate().also { inner ->
//                    inner["value"] = idx
//                })
//            }
//        }
//
////        KuaTableForEach(1, state).forEach { key, value ->
////            println(key)
////            println(value)
////        }
//
//        val result = KuaTableForEach(1, state).map { key, value ->
//            require(value is KuaTable)
//            state.tablePush(value)
//            value
//
//        }
//
//
//        println(result)
//
//
////        while(state.tableHasNext(1) == KuaTrue){
////            println("inside")
////        }
//
////        state.nilPush()
////        val seq = generateSequence {
////
////            val moreValues = state.tableHasNext(1) == KuaTrue
////            if (!moreValues) {
////                null
////            } else {
////                state.tableNext(1)
////                val key = state.anyGet(-2).value
////                val value = state.anyGet(-1).value
////
////                key to value
////            }
////        }
//////
////        seq.forEach { (key, table) ->
////            require(key is KuaNumber)
////            require(table is KuaTable)
////            assertThat(table.getNumber("value").intValue, equalTo(key.intValue - 1))
////            state.topPop(1)
////        }
//
////        state.nilPush()
////        while (state.tableHasNext(1) == KuaTrue) {
////            state.tableNext(1)
////            val idx = state.numberGet(-2)
////            val table = state.tableGet(-1)
////            assertThat(table.getNumber("value").intValue, equalTo(idx.intValue - 1))
////            state.topPop(1)
////        }
//
//
////        val testInstance = KuaTableIterator(
////            index = 1,
////            state = state,
////            keyExtractor = { state, index -> state.numberGet(index) },
////            valueExtractor = { state, index -> state.tableGet(index) }
////        )
//
////        while (testInstance.hasNext()) {
////            val (key, value) = testInstance.next()
////            println(key)
////            println(value)
//////            state.topPop(1)
////        }
//
////        repeat(10) { idx ->
////            assertThat(testInstance.hasNext(), equalTo(true))
////            val (key, table) = testInstance.next()
////            assertThat(key, equalTo(KuaNumber(idx + 1)))
////
////            println(state.topGet())
////            println(state.type(1))
////            println(state.type(2))
////            println(state.numberGet(2))
////            println(table.index)
////
////            assertThat(table.getNumber("value"), equalTo(KuaNumber(idx)))
////
//////            state.topPop(1)
////        }
//
////        assertThat(testInstance.hasNext(), equalTo(false))
////
////        assertThrows<NoSuchElementException> { testInstance.next() }.also { exception ->
////            assertThat(exception.message, equalTo("Iterator exhausted"))
////        }
//    }
//
//
//    private val state = run {
//        NativeLoader.load(NativeLoader.Preference.Resources)
//        Sandbox(SandboxContextNop)
//    }
//}