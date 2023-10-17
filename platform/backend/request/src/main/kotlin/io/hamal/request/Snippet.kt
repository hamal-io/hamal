package io.hamal.request

import io.hamal.lib.domain.vo.*

interface CreateSnippetReq {
    val name: SnippetName
    val inputs: SnippetInputs
    val value: CodeValue
    val accountId: AccountId
}


interface UpdateSnippetReq {
    val name: SnippetName?
    val inputs: SnippetInputs?
    val value: CodeValue?
}