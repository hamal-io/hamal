package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.ValueObjectString
import org.springframework.scheduling.support.CronExpression

class CronPattern(override val value: String) : ValueObjectString() {
    init {
        if (!CronExpression.isValidExpression(value)) throw IllegalArgumentException("Invalid Cron Expression")
    }
}