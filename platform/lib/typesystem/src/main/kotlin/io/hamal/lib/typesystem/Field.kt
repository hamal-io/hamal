package io.hamal.lib.typesystem


data class Field(
    val kind: Kind,
    val identifier: String,
    // for container
    val valueType: Type? = null
    // nullable?
    // defaultValue?
) {

    val isContainer get() = kind.isContainer

    enum class Kind(val isContainer: kotlin.Boolean) {
        Boolean(false),
        Date(false),
        DateTime(false),
        Decimal(false),
        List(true),
        Nil(false),
        Number(false),
        Object(true),
        String(false),
        Time(false)
    }

    init {
        if (isContainer && valueType == null) {
            throw IllegalArgumentException("Container type requires valueType")
        }

        if (!isContainer && valueType != null) {
            throw IllegalArgumentException("Not a container type has valueType")
        }
    }
}
