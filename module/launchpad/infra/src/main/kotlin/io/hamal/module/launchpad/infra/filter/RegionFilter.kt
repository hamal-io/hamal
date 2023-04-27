package io.hamal.module.launchpad.infra.filter

import io.hamal.lib.domain.vo.RegionId
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RegionFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        request.setAttribute("regionId", RegionId(1))
        filterChain.doFilter(request, response)
    }

}