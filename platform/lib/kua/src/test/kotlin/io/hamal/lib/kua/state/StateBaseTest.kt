package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.kua.function.FunctionContext
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest


internal abstract class StateBaseTest {

    init {
        NativeLoader.load(NativeLoader.Preference.Resources)
    }

    fun runTest(action: (State) -> Unit): List<DynamicTest> = listOf(
        "StateImpl" to ::closeableStateImpl,
        "FunctionContext" to ::functionContext,
        "Sandbox" to ::sandbox,
    ).map { (name, factory) ->
        dynamicTest(name) {
            Native().use { native ->
                CloseableStateImpl(native).use { state ->
                    action(factory(state))
                }
            }
        }
    }

    private fun closeableStateImpl(state: CloseableState) = state

    private fun functionContext(state: CloseableState): FunctionContext = FunctionContext(state)
    private fun sandbox(state: CloseableState): Sandbox = Sandbox(
        ctx = NopSandboxContext(),
        state = state
    )
}
