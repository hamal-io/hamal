package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
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

@DisplayName("toProxyArray()")
internal class ToProxyArrayTest {

    @Test
    fun `Empty array`() {
        val testArray = ArrayType()
        val result = testState.toProxyArray(testArray)
        assertThat(result.length, equalTo(0))
    }

    @Test
    fun `Array with boolean type`() {
        val testArray = ArrayType(mutableMapOf(1 to TrueValue, 2 to FalseValue))
        val result = testState.toProxyArray(testArray)
        assertThat(result.length, equalTo(2))
        assertThat(result.getBooleanType(1), equalTo(TrueValue))
        assertThat(result.getBooleanType(2), equalTo(FalseValue))
    }

    @Test
    fun `Array with number type`() {
        val testArray = ArrayType(mutableMapOf(1 to NumberType(23.42)))
        val result = testState.toProxyArray(testArray)
        assertThat(result.length, equalTo(1))
        assertThat(result.getNumberType(1), equalTo(NumberType(23.42)))
    }

    @Test
    fun `Array with string type`() {
        val testArray = ArrayType(mutableMapOf(1 to StringType("HamalRocks")))
        val result = testState.toProxyArray(testArray)
        assertThat(result.length, equalTo(1))
        assertThat(result.getStringType(1), equalTo(StringType("HamalRocks")))
    }

    @Test
    fun `Array with map type`() {
        val testArray = ArrayType(mutableMapOf(1 to MapType(mutableMapOf("number" to NumberType(13.37)))))
        val result = testState.toProxyArray(testArray)
        assertThat(result.length, equalTo(1))

        val nested = testArray.getMap(1)
        assertThat(nested.size, equalTo(1))
        assertThat(nested.getNumberValue("number"), equalTo(NumberType(13.37)))
    }

    @Test
    fun `Array with array type`() {
        val testArray = ArrayType(mutableMapOf(1 to ArrayType(mutableMapOf(1 to NumberType(22.33)))))
        val result = testState.toProxyArray(testArray)
        assertThat(result.length, equalTo(1))

        val nested = testArray.getArray(1)
        assertThat(nested.size, equalTo(1))
        assertThat(nested.getNumberType(1), equalTo(NumberType(22.33)))
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

@DisplayName("toProxyMap()")
internal class ToProxyMapTest {

    @Test
    fun `Empty map`() {
        val testMap = MapType()
        val result = testState.toProxyMap(testMap)
        assertThat(result.length, equalTo(0))
    }

    @Test
    fun `Map with boolean type`() {
        val testMap = MapType(mutableMapOf("true_value" to TrueValue, "false_value" to FalseValue))
        val result = testState.toProxyMap(testMap)
        assertThat(result.length, equalTo(2))
        assertThat(result.getBooleanType("true_value"), equalTo(TrueValue))
        assertThat(result.getBooleanType("false_value"), equalTo(FalseValue))
    }

    @Test
    fun `Map with number type`() {
        val testMap = MapType(mutableMapOf("number" to NumberType(1337)))
        val result = testState.toProxyMap(testMap)
        assertThat(result.length, equalTo(1))
        assertThat(result.getNumberType("number"), equalTo(NumberType(1337)))
    }

    @Test
    fun `Map with string type`() {
        val testMap = MapType(mutableMapOf("some_string" to StringType("HamalRocks")))
        val result = testState.toProxyMap(testMap)
        assertThat(result.length, equalTo(1))
        assertThat(result.getStringType("some_string"), equalTo(StringType("HamalRocks")))
    }

    @Test
    fun `Map with nested map`() {
        val testMap = MapType(mutableMapOf("nested" to MapType(mutableMapOf("value" to StringType("HamalRocks")))))
        val result = testState.toProxyMap(testMap)
        assertThat(result.length, equalTo(1))

        val nested = result.getTableMap("nested")
        assertThat(nested.length, equalTo(1))
        assertThat(nested.getStringType("value"), equalTo(StringType("HamalRocks")))
    }

    @Test
    fun `Map with nested array`() {
        val testMap = MapType(mutableMapOf("nested" to ArrayType(mutableMapOf(3 to NumberType(13)))))
        val result = testState.toProxyMap(testMap)
        assertThat(result.length, equalTo(1))

        val nested = result.getTableArray("nested")
        assertThat(nested.length, equalTo(1))
        assertThat(nested.getNumberType(1), equalTo(NumberType(13)))
    }

    private val testSandbox by lazy {
        NativeLoader.load(Resources)
        Sandbox(NopSandboxContext())
    }

    private val testState = testSandbox.state
}