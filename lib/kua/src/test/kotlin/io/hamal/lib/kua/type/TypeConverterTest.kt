package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("toMapType()")
internal class ToMapTypeTest {

    @Test
    fun `Empty table`() {
        val table = testState.tableCreateMap(0)
        val result = testState.toMapType(table)
        assertThat(result.size, equalTo(0))
    }

    @Test
    fun `Table with boolean`() {
        val table = testState.tableCreateMap(1).also {
            it["true_value"] = booleanOf(true)
            it["false_value"] = booleanOf(false)
        }

        val result = testState.toMapType(table)
        assertThat(result.size, equalTo(2))
        assertThat(result["true_value"], equalTo(booleanOf(true)))
        assertThat(result["false_value"], equalTo(booleanOf(false)))
    }

    @Test
    fun `Table with number`() {
        val table = testState.tableCreateMap(1).also { it["number"] = 41 }
        val result = testState.toMapType(table)
        assertThat(result.size, equalTo(1))
        assertThat(result["number"], equalTo(NumberType(41)))
    }

    @Test
    fun `Table with string`() {
        val table = testState.tableCreateMap(1).also { it["string"] = "HamalRocks" }
        val result = testState.toMapType(table)
        assertThat(result.size, equalTo(1))
        assertThat(result["string"], equalTo(StringType("HamalRocks")))
    }

    @Test
    fun `Table with nested table (map)`() {
        val innerTable = testState.tableCreateMap(1).also { it["inner-key"] = "inner-value" }
        val table = testState.tableCreateMap(1).also { it["inner"] = innerTable }
        val result = testState.toMapType(table)
        assertThat(result.size, equalTo(1))

        val inner = result["inner"] as MapType
        assertThat(inner.size, equalTo(1))
        assertThat(inner["inner-key"], equalTo(StringType("inner-value")))
    }

    @Test
    @Disabled
    fun `Table with nested table (array)`() {
        val innerTable = testState.tableCreateArray(1).also { it.append(1337) }
        val table = testState.tableCreateMap(1).also { it["inner"] = innerTable }
        val result = testState.toMapType(table)
        assertThat(result.size, equalTo(1))

        val inner = result["inner"] as ArrayType
        assertThat(inner.size, equalTo(1))
        assertThat(inner.getNumberType(1), equalTo(NumberType(1337)))
    }

    private val testSandbox by lazy {
        NativeLoader.load(Resources)
        Sandbox(NopSandboxContext())
    }

    private val testState = testSandbox.state
}