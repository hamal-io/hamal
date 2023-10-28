package io.hamal.lib.common.domain

import io.hamal.lib.common.Partition
import io.hamal.lib.common.snowflake.Elapsed
import io.hamal.lib.common.snowflake.Sequence
import io.hamal.lib.common.snowflake.SnowflakeId

abstract class DomainId : ValueObject.ComparableImpl<SnowflakeId>() {
    abstract fun partition(): Partition
    abstract fun sequence(): Sequence
    abstract fun elapsed(): Elapsed
}
