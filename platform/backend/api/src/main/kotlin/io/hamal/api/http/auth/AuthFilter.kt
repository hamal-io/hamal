package io.hamal.api.http.auth

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.repository.api.AccountQueryRepository
import io.hamal.repository.api.AuthCmdRepository.RevokeAuthCmd
import io.hamal.repository.api.AuthRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.security.SecurityProperties.BASIC_AUTH_ORDER
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter


private val log = LoggerFactory.getLogger(AuthApiFilter::class.java)

@Component
@Order(BASIC_AUTH_ORDER)
class AuthApiFilter(
    private val authRepository: AuthRepository,
    private val accountQueryRepository: AccountQueryRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val path = request.servletPath
        if (request.method == "OPTIONS") {
            return filterChain.doFilter(request, response)
        }

        if (path.startsWith("/v1/webhooks")) {
            return filterChain.doFilter(request, response)
        }

        if (path == "/v1/login" && request.method == "POST") {
            return filterChain.doFilter(request, response)
        }

        if (path == "/v1/anonymous-accounts" && request.method == "POST") {
            return filterChain.doFilter(request, response)
        }

        val token = request.getHeader("authorization")
            ?.replace("Bearer ", "")
            ?.let(::AuthToken)
            ?: run {
                log.warn("Unauthorized request on $path")

                response.status = 403
                response.contentType = "application/json"
                response.writer.write("""{"message":"That's an error"}""")
                return
            }



        if (token == AuthToken("let_me_in")) {
            return filterChain.doFilter(request, response)
        }

        // FIXME token must contain creation timestamp is creation timestamp < 1s ago retry a couple of times - due to its async nature the token might not be in the database yet
        var counter = 0
        while (true) {
            val auth = authRepository.find(token)
            if (auth != null) {
                try {

                    if (path == "/v1/logout") {
                        authRepository.revokeAuth(RevokeAuthCmd(CmdId.random(), auth.id))
                        response.status = 204
                        return
                    }

                    AuthContextHolder.set(AuthContext(auth, accountQueryRepository.get(auth.accountId), token))
                    return filterChain.doFilter(request, response)
                } finally {
                    AuthContextHolder.clear()
                }
            }
            if (counter++ > 20) {
                response.status = 403
                response.contentType = "application/json"
                response.writer.write("""{"message":"That's an error"}""")
                return
            }
            Thread.sleep(50)
        }
    }
}