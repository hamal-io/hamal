package io.hamal.lib.domain.value

internal class TestValue : Value {
    override val metaTable = TestMetaTable
}


internal object TestMetaTable : MetaTable {
    override val type = "test-type"
    override val operations = listOf(TestPrefixOperation, TestInfixOperation)
}

internal object TestPrefixOperation : PrefixValueOperation {
    override val selfType = "test-type"
    override val operationType = ValueOperation.Type.Add
    override fun invoke(self: Value) = TODO("Not yet implemented")
}

internal object TestInfixOperation : InfixValueOperation {
    override val selfType = "test-type"
    override val otherType = "test-type"
    override val operationType = ValueOperation.Type.Add

    override fun invoke(self: Value, other: Value) = TODO("Not yet implemented")
}