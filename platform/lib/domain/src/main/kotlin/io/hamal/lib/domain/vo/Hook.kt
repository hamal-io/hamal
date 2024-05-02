package io.hamal.lib.domain.vo

import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.common.value.ValueVariableObject


class HookHeaders(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

class HookParameters(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()

class HookContent(override val value: ValueObject = ValueObject.empty) : ValueVariableObject()