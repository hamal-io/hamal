package io.hamal.lib.domain._enum

enum class FeedbackMoods(val value: Int) {
    Happy(0),
    Excited(1),
    Normal(2),
    Disappointed(3),
    Angry(4);

    companion object {
        @JvmStatic
        fun of(value: Int): FeedbackMoods {
            val result = FeedbackMoods.values().find { it.value == value }
            require(result != null) { "$value not mapped as a feedback mood" }
            return result
        }
    }

}