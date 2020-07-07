package com.wfh.sharknet.service

import arrow.core.getOrElse
import com.wfh.sharknet.repository.ApplicationUserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.Collections.emptyList


@Service
class UserDetailsServiceImpl(
    private val applicationUserRepository: ApplicationUserRepository
) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val (_, username1, password) = applicationUserRepository.findByUsername(username)
            ?: throw UsernameNotFoundException(username)
        return User(username1, password, emptyList())
    }
    
}