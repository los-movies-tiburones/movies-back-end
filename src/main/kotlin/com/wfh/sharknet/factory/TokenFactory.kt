package com.wfh.sharknet.factory

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.wfh.sharknet.security.SecurityConstants
import org.springframework.stereotype.Component
import java.util.*

interface TokenFactory {
    fun create(subject: String): String
}

@Component
class TokenFactoryImp: TokenFactory {
    override fun create(subject: String): String {
        val token = JWT.create()
            .withSubject(subject)
            .withExpiresAt(Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
            .sign(Algorithm.HMAC512(SecurityConstants.SECRET.toByteArray()))
        return SecurityConstants.TOKEN_PREFIX + token
    }
}