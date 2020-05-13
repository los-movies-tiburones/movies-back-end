package com.WFH.sharknet.api
import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/healthcheck")
class HealthCheck {
    @GetMapping ("")
    fun healthcheck ():String = "It's working bitch"
}