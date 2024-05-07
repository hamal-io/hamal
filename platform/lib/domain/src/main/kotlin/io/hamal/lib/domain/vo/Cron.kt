package io.hamal.lib.domain.vo

import io.hamal.lib.common.value.ValueString
import io.hamal.lib.common.value.ValueVariableString
import org.springframework.scheduling.support.CronExpression

class CronPattern(override val value: ValueString) : ValueVariableString() {
    init {
        if (!CronExpression.isValidExpression(value.stringValue)) throw IllegalArgumentException("Invalid Cron Expression")
    }

    companion object {
        fun CronPattern(value: String) = CronPattern(ValueString(value))
    }
}