package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.StringValueObjectSerializer
import io.hamal.lib.common.domain.ValueObjectString
import kotlinx.serialization.Serializable
import org.springframework.scheduling.support.CronExpression

@Serializable(with = CronPattern.Serializer::class)
class CronPattern(override val value: String) : ValueObjectString() {
    init {
        if (!CronExpression.isValidExpression(value)) throw IllegalArgumentException("Invalid Cron Expression")
    }

    internal object Serializer : StringValueObjectSerializer<CronPattern>(::CronPattern)
}