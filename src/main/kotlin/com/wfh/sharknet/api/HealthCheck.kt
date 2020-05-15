package com.wfh.sharknet.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/healthcheck")
class HealthCheck {
    @GetMapping("")
    fun healthcheck(): String = "It's working"
}

