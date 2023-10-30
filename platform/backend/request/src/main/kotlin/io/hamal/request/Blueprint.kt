package io.hamal.request

import io.hamal.lib.domain.vo.BlueprintId
import io.hamal.lib.domain.vo.BlueprintInputs
import io.hamal.lib.domain.vo.BlueprintName
import io.hamal.lib.domain.vo.CodeValue

interface CreateBlueprintReq {
    val name: BlueprintName
    val inputs: BlueprintInputs
    val value: CodeValue
}


interface UpdateBlueprintReq {
    val id: BlueprintId
    val name: BlueprintName?
    val inputs: BlueprintInputs?
    val value: CodeValue?
}