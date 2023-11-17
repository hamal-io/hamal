package io.hamal.lib.domain.vo

import io.hamal.lib.common.domain.StringValueObject
import io.hamal.lib.common.domain.StringValueObjectSerializer
import kotlinx.serialization.Serializable
import org.springframework.scheduling.support.CronExpression

@Serializable(with = CronPattern.Serializer::class)
class CronPattern(override val value: String) : StringValueObject() {
    init {
        if (!CronExpression.isValidExpression(value)) throw IllegalArgumentException("Invalid Cron Expression")
    }

    internal object Serializer : StringValueObjectSerializer<CronPattern>(::CronPattern)
}