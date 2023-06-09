package io.hamal.lib.common.value


data class DepPrototypeValue(
    val identifier: IdentValue,
    val parameters: List<StringValue>,
//    val block: Statement
) : Value {
    override val metaTable = TODO()

}