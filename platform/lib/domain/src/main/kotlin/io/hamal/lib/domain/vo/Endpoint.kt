package io.hamal.lib.domain.vo

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.common.value.ValueVariableObject


class EndpointHeaders(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

class EndpointParameters(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

class EndpointContent(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()
