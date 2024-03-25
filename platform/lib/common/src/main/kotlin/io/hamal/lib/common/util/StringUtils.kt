package io.hamal.lib.common.util

object StringUtils {

    fun snakeCase(input: String): String {
        val builder = StringBuilder()
        input.windowed(2, 1, true) {
            val current = it[0]

            if (builder.isEmpty()) {
                builder.append(current.lowercase())
            } else if (it.length == 1) {
                builder.append(current.lowercase())
            } else {

                val next = it[1]
                if (current.isUpperCase() && next.isLowerCase()) {
                    builder.append("_")
                }
                builder.append(current.lowercase())
            }
        }
        return builder.toString()
    }

}