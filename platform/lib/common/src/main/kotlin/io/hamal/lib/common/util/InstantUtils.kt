package io.hamal.lib.common.util

import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object InstantUtils {

    fun parse(value: String) = Instant.from(formatter.parse(value))

    fun format(value: Instant): String = formatter.format(value)

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC)
}