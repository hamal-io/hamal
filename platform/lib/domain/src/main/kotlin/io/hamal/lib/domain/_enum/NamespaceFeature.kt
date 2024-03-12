package io.hamal.lib.domain._enum

enum class NamespaceFeature(val value: Int) {
    Schedules(0),
    Topics(1),
    Webhooks(2),
    Endpoints(3);

    companion object {
        @JvmStatic
        fun of(value: Int): NamespaceFeature {
            val result = NamespaceFeature.entries.find { it.value == value }
            require(result != null) { "$value not mapped as a namespace feature" }
            return result
        }
    }
}