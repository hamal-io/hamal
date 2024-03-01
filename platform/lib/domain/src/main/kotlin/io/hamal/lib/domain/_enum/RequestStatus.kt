package io.hamal.lib.domain._enum

enum class RequestStatus(val value: Int) {
    Submitted(1),
    Processing(2),
    Completed(3),
    Failed(4);

    companion object {
        fun fromInt(value: Int) = when (value) {
            1 -> Submitted
            2 -> Processing
            3 -> Completed
            4 -> Failed
            else -> throw IllegalArgumentException("Invalid RequestStatus")
        }
    }
}

