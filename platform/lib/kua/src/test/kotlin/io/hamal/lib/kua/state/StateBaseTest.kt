package io.hamal.lib.kua.state

import io.hamal.lib.kua.ClosableState
import io.hamal.lib.kua.Native
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.State
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest


internal abstract class StateBaseTest {

    init {
        NativeLoader.load(NativeLoader.Preference.Resources)
    }

    fun runTest(action: (State) -> Unit): List<DynamicTest> = listOf(
        "CloseableState" to ::ClosableState,
        "Sandbox" to ::ClosableState,
        "FunctionContext" to ::ClosableState,
    ).map { (name, factory) ->
        dynamicTest(name) {
            Native().use {
                factory(it).use(action)
            }
        }
    }


}
