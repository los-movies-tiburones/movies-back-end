package com.wfh.sharknet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class SharknetApplication

fun main(args: Array<String>) {
	runApplication<SharknetApplication>(*args)
}
