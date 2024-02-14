package io.hamal.api.http.controller.workspace

import io.hamal.api.http.auth.AuthContextHolder
import io.hamal.core.adapter.GroupListPort
import io.hamal.lib.sdk.api.ApiGroupList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class WorkspaceListController(
    private val list: GroupListPort
) {
    @GetMapping("/v1/groups")
    fun listGroup(): ResponseEntity<ApiGroupList> {
        return list(AuthContextHolder.get().accountId) { groups ->
            ResponseEntity.ok(
                ApiGroupList(
                    groups = groups.map { group ->
                        ApiGroupList.Group(
                            id = group.id,
                            name = group.name
                        )
                    }
                )
            )
        }
    }
}