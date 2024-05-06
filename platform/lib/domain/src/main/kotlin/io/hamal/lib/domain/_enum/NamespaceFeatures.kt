package io.hamal.lib.domain._enum


enum class NamespaceFeatures(val value: Int) {
    schedule(0),
    topic(1),
    webhook(2),
    endpoint(3);

    companion object {
        @JvmStatic
        fun of(value: Int): FeedbackMood {
            val result = FeedbackMood.entries.find { it.value == value }
            require(result != null) { "$value not mapped as a feedback mood" }
            return result
        }
    }
}



