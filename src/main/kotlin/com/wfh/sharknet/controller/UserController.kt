package com.wfh.sharknet.controller

import com.wfh.sharknet.model.ApplicationUser
import com.wfh.sharknet.security.SecurityConstants
import com.wfh.sharknet.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    
    @PostMapping("/sign-up")
    fun signUp(
        @Valid @RequestBody user: ApplicationUser,
        res: HttpServletResponse
    ) {
        res.addHeader(SecurityConstants.HEADER_STRING, userService.signUp(user))
    }
}