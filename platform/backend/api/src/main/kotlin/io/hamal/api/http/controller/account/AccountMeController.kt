package io.hamal.api.http.controller.account

import io.hamal.core.adapter.account.AccountGetPort
import io.hamal.core.adapter.workspace.WorkspaceListPort
import io.hamal.core.component.Retry
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.Limit
import io.hamal.lib.sdk.api.ApiAccountMe
import io.hamal.repository.api.WorkspaceQueryRepository.WorkspaceQuery
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
internal class AccountMeController(
    private val retry: Retry,
    private val accountGet: AccountGetPort,
    private val workspaceList: WorkspaceListPort
) {

    // FIXME add tests and to sdk / sys
    @GetMapping("/v1/accounts/me")
    fun get(
    ): ResponseEntity<ApiAccountMe> {
        return retry {
            accountGet(SecurityContext.currentAccountId).let { account ->
                ResponseEntity.ok(
                    ApiAccountMe(
                        id = account.id,
                        workspaces = workspaceList(
                            WorkspaceQuery(
                                limit = Limit.all,
                                accountIds = listOf(account.id)
                            )
                        ).map { workspace ->
                            ApiAccountMe.Workspace(workspace.id)
                        }
                    )
                )
            }
        }
    }
}