package io.hamal.lib.typesystem


data class Field(
    val kind: Kind,
    val identifier: String,
    // for container
    val valueType: Type? = null
) {

    val isContainer get() = kind.isContainer

    enum class Kind(val isContainer: kotlin.Boolean) {
        Any(true),
        Boolean(false),
        Date(false),
        Decimal(false),
        List(true),
        Nil(false),
        Number(false),
        Object(true),
        OneOf(true),
        String(false),
        Time(false)
    }

    init {
        if (isContainer && valueType == null) {
            throw IllegalArgumentException("Container type requires valueType")
        }

        if(!isContainer && valueType != null){
            throw IllegalArgumentException("Not a container type has valueType")
        }
    }
}
