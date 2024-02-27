package io.hamal.lib.kua.type

import io.hamal.lib.kua.CloseableStateImpl
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class KuaTableArrayTest {

    @TestFactory
    fun append(): List<DynamicTest> {
        lateinit var testInstance: KuaTable
        return listOf(
            { testInstance.append(true) },
            { testInstance.append(KuaFalse) },
            { testInstance.append(23.23) },
            { testInstance.append(23) },
            { testInstance.append(23L) },
            { testInstance.append(23.0f) },
            { testInstance.append("Hamal") },
            { testInstance.append(KuaString("Hamal")) }
        ).mapIndexed { idx, testFn ->
            dynamicTest("Test: ${(idx + 1)}") {
                testInstance = state.tableCreateArray()

                val result = testFn()
                assertThat(result, equalTo(1))
            }
        }
    }

    private val state = run {
        NativeLoader.load(Resources)
        CloseableStateImpl(Sandbox(NopSandboxContext()).native)
    }

}