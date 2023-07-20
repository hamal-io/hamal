//package io.hamal.lib.kua.value
//
//import io.hamal.lib.kua.ResourceLoader
//import io.hamal.lib.kua.Bridge
//import org.hamcrest.MatcherAssert.assertThat
//import org.hamcrest.Matchers.equalTo
//import org.junit.jupiter.api.Test
//
//internal class FuncValueTest {
//
//    @Test
//    fun `Receives 2 parameters and returns 2 parameters`() {
//        testState.pushFunctionValue(Swap())
//        testState.pushNumber(1.0)
//        testState.pushNumber(5.0)
//        testState.call(2, 2)
//
//        assertThat(testState.toNumber(1), equalTo(5.0))
//        assertThat(testState.toNumber(2), equalTo(1.0))
//
//        testState.pop(2)
//        verifyStackIsEmpty()
//    }
//
//    private class Swap : FuncValue() {
//        override fun invokedByLua(bridge: Bridge): Int {
//            val a = bridge.toNumber(1)
//            val b = bridge.toNumber(2)
//            bridge.pushNumber(b)
//            bridge.pushNumber(a)
//            return 2
//        }
//    }
//
//
//    private val testState: Bridge = run {
//        ResourceLoader.load()
//        Bridge()
//    }
//
//    private fun verifyStackIsEmpty() {
//        assertThat("Stack is empty", testState.top(), equalTo(0))
//    }
//}
//
//
