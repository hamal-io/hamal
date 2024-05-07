package io.hamal.lib.domain._enum


enum class Features(val value: Int) {
    Schedule(1),
    Topic(2),
    Webhook(3),
    Endpoint(4);

    companion object {
        @JvmStatic
        fun of(value: Int): Features {
            val result = Features.entries.find { it.value == value }
            require(result != null) { "$value not mapped as a feature" }
            return result
        }
    }
}



