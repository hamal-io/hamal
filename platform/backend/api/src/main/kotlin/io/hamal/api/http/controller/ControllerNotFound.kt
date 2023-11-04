package io.hamal.api.http.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ControllerNotFound {

    @RequestMapping("/v1/*")
    fun notfound(): Nothing = throw IllegalArgumentException("test")

}