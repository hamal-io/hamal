package io.hamal.lib.kua.table

import io.hamal.lib.kua.ClosableState
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

internal class TableProxyArrayTest {

    @TestFactory
    fun append(): List<DynamicTest> {
        TODO()
    }
//        lateinit var testInstance: TableProxyArray
//        return listOf(
//            { testInstance.append(true) },
//            { testInstance.append(KuaFalse) },
//            { testInstance.append(23.23) },
//            { testInstance.append(23) },
//            { testInstance.append(23L) },
//            { testInstance.append(23.0f) },
//            { testInstance.append("Hamal") },
//            { testInstance.append(KuaString("Hamal")) }
//        ).mapIndexed { idx, testFn ->
//            dynamicTest("Test: ${(idx + 1)}") {
//                testInstance = state.tableCreateArray()
//
//                val result = testFn()
//                assertThat(result, equalTo(1))
//            }
//        }
//    }

    private val state = run {
        NativeLoader.load(Resources)
        ClosableState(Sandbox(NopSandboxContext()).native)
    }
}