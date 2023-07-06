package io.hamal.lib.script.api.value

internal class TestValue : Value {
    override val metaTable = TestMetaTable
}


internal object TestMetaTable : MetaTable<TestValue> {
    override val type = "test-type"
    override val operators = listOf(TestPrefixOperator, TestInfixOperator)
    override val props: Map<IdentValue, ValueProp<TestValue>> = mapOf()
}

internal object TestPrefixOperator : PrefixValueOperator {
    override val selfType = "test-type"
    override val operatorType = ValueOperator.Type.Add
    override fun invoke(self: Value) = TODO("Not yet implemented")
}

internal object TestInfixOperator : InfixValueOperator {
    override val selfType = "test-type"
    override val otherType = "test-type"
    override val operatorType = ValueOperator.Type.Add

    override fun invoke(self: Value, other: Value) = TODO("Not yet implemented")
}