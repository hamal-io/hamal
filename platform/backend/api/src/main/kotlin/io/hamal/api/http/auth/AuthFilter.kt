package io.hamal.api.http.auth

import io.hamal.core.security.SecurityContext
import io.hamal.lib.domain.GenerateCmdId
import io.hamal.lib.domain.vo.AuthToken
import io.hamal.lib.domain.vo.ExecToken
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
    private val generateCmdId: GenerateCmdId
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

        if (path == "/v1/login" && request.method == "POST") {
            return filterChain.doFilter(request, response)
        }

        if (path.startsWith("/v1/endpoints")) {
            return filterChain.doFilter(request, response)
        }

        if (path == "/v1/metamask/challenge" && request.method == "POST") {
            return filterChain.doFilter(request, response)
        }

        if (path == "/v1/metamask/token" && request.method == "POST") {
            return filterChain.doFilter(request, response)
        }

        if (path == "/v1/anonymous-accounts" && request.method == "POST") {
            return filterChain.doFilter(request, response)
        }

        // FIXME user uses bearer
        // FIXME runner uses runner
        // FIXME x-exec-code as one time password

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


        request.getHeader("x-exec-token")?.let(::ExecToken)?.also { execToken ->
            val auth = authRepository.find(execToken) ?: run {
                log.warn("Unauthorized request on $path")

                response.status = 403
                response.contentType = "application/json"
                response.writer.write("""{"message":"That's an error"}""")
                return
            }

            return SecurityContext.with(auth) {
                filterChain.doFilter(request, response)
            }
        }

        // FIXME token must contain creation timestamp is creation timestamp < 1s ago retry a couple of times - due to its async nature the token might not be in the database yet
        var counter = 0
        while (true) {
            val auth = authRepository.find(token)
            if (auth != null) {
                return SecurityContext.with(auth) {
                    filterChain.doFilter(request, response)
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