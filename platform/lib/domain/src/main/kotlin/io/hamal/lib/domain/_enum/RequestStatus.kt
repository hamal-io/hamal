package io.hamal.lib.domain._enum

enum class RequestStatuses(val value: Int) {
    Submitted(1),
    Processing(2),
    Completed(3),
    Failed(4);


    companion object {
        @JvmStatic
        fun of(value: Int): RequestStatuses {
            val result = entries.find { it.value == value }
            require(result != null) { "$value not mapped as a RequestStatus" }
            return result
        }
    }
}

