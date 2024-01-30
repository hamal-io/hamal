package io.hamal.repository.api.new_log

import io.hamal.lib.common.domain.ValueObjectId
import io.hamal.lib.common.snowflake.SnowflakeId

class LogTopicConsumerId(override val value: SnowflakeId) : ValueObjectId()
