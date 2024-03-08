package io.hamal.bridge.http.filter

import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AccountId
import io.hamal.lib.domain.vo.AuthId
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.ExecToken
import io.hamal.repository.api.Auth
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.boot.autoconfigure.security.SecurityProperties.BASIC_AUTH_ORDER
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(BASIC_AUTH_ORDER)
class AuthBridgeFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
//        val path = request.servletPath
//
//        val runnerToken = request.getHeader("x-runner-token")
//        if (runnerToken == "i_am_runner_let_me_in") {
//            return filterChain.doFilter(request, response)
//        }
//
//        val runnerExecToken = request.getHeader("x-runner-exec-token")
//        if (runnerExecToken == "let_me_in") {
//            return filterChain.doFilter(request, response)
//        }
//
//        if (path == "/v1/sign-in" || path == "/v1/accounts" && request.method == "POST") {
//            return filterChain.doFilter(request, response)
//        }
//
//        val token = request.getHeader("authorization")
//            ?.let(::AuthToken) ?: throw NoSuchElementException("Account not found")
//
//        val auth = authRepository.get(token)
////        // FIXME not expired

        // FIXME make sure runner
        return SecurityContext.with(
            Auth.Runner(
                id = AuthId.root,
                cmdId = CmdId(1),
                accountId = AccountId.root,
                token = AuthToken("let_me_in"),
                execToken = ExecToken("ExecToken")
            )
        ) { filterChain.doFilter(request, response) }
    }

}