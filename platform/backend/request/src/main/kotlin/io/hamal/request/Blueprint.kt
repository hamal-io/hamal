package io.hamal.request

import io.hamal.lib.domain.vo.BlueprintInputs
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.CodeValue

interface BlueprintCreateReq {
    val name: BlueprintName
    val inputs: BlueprintInputs
    val value: CodeValue
}


interface BlueprintUpdateReq {
    val name: BlueprintName?
    val inputs: BlueprintInputs?
    val value: CodeValue?
}