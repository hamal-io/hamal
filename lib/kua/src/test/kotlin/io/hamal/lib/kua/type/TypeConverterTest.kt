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
    fun `Table with nested map`() {
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
    fun `Table with nested array`() {
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

@DisplayName("toArrayType()")
internal class ToArrayTypeTest {

    @Test
    fun `Empty table`() {
        val table = testState.tableCreateArray(0)
        val result = testState.toArrayType(table)
        assertThat(result.size, equalTo(0))
    }

    @Test
    fun `Table with boolean`() {
        val table = testState.tableCreateArray(1).also { it.append(true); it.append(false) }
        val result = testState.toArrayType(table)
        assertThat(result.size, equalTo(2))
        assertThat(result[1], equalTo(TrueValue))
        assertThat(result[2], equalTo(FalseValue))
    }

    @Test
    fun `Table with number`() {
        val table = testState.tableCreateArray(1).also { it.append(1337) }
        val result = testState.toArrayType(table)
        assertThat(result.size, equalTo(1))
        assertThat(result[1], equalTo(NumberType(1337)))
    }

    @Test
    fun `Table with string`() {
        val table = testState.tableCreateArray(1).also { it.append("HamalRocks") }
        val result = testState.toArrayType(table)
        assertThat(result.size, equalTo(1))
        assertThat(result[1], equalTo(StringType("HamalRocks")))
    }

    @Test
    fun `Table with nested map`() {
        val innerTable = testState.tableCreateMap(1).also { it["inner-key"] = "inner-value" }
        val table = testState.tableCreateArray(1).also { it.append(innerTable) }
        val result = testState.toArrayType(table)
        assertThat(result.size, equalTo(1))

        val inner = result[1] as MapType
        assertThat(inner.size, equalTo(1))
        assertThat(inner["inner-key"], equalTo(StringType("inner-value")))
    }

    @Test
    @Disabled
    fun `Table with nested array`() {
        val innerTable = testState.tableCreateArray(1).also { it.append(1337) }
        val table = testState.tableCreateArray(1).also { it.append(innerTable) }
        val result = testState.toArrayType(table)
        assertThat(result.size, equalTo(1))

        val inner = result[1] as ArrayType
        assertThat(inner.size, equalTo(1))
        assertThat(inner[1], equalTo(NumberType(1337)))
    }

    private val testSandbox by lazy {
        NativeLoader.load(Resources)
        Sandbox(NopSandboxContext())
    }

    private val testState = testSandbox.state
}