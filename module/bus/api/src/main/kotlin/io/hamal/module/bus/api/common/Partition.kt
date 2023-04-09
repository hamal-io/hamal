package io.hamal.module.bus.api.common

import io.hamal.lib.ddd.base.ValueObject

class Partition(value: Int) : ValueObject.ComparableImpl<Int>(value)
class PartitionCount(value: Int) : ValueObject.ComparableImpl<Int>(value)