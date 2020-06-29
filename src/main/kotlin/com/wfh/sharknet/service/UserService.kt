package com.wfh.sharknet.service

import com.wfh.sharknet.factory.TokenFactory
import com.wfh.sharknet.model.ApplicationUser
import com.wfh.sharknet.repository.ApplicationUserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.*


interface UserService {
    fun signUp(user: ApplicationUser): String
}

@Service
class UserServiceImp constructor(
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val authenticationManagerUser: AuthenticationManager,
    private val applicationUserRepository: ApplicationUserRepository,
    private val tokenFactory: TokenFactory
): UserService {
    
    override fun signUp(user: ApplicationUser): String {
        val newUser = ApplicationUser(
            username = user.username,
            password = bCryptPasswordEncoder.encode(user.password)
        )
        val userSaved = applicationUserRepository.save(newUser)
        val auth = authenticationManagerUser.authenticate(
            UsernamePasswordAuthenticationToken(
                userSaved.username,
                user.password,
                ArrayList())
        )
        return tokenFactory.create((auth.principal as User).username)
    }
}