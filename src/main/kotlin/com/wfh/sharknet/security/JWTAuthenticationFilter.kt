package com.wfh.sharknet.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wfh.sharknet.factory.TokenFactory
import com.wfh.sharknet.model.ApplicationUser
import com.wfh.sharknet.security.SecurityConstants.HEADER_STRING
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.io.IOException
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(
    private val authenticationManagerUser: AuthenticationManager,
    private val tokenFactory: TokenFactory
) : UsernamePasswordAuthenticationFilter() {
    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(req: HttpServletRequest,
                                       res: HttpServletResponse): Authentication {
        return try {
            val (_, username, password) = jacksonObjectMapper()
                .readValue(req.inputStream, ApplicationUser::class.java)
            authenticationManagerUser.authenticate(
                UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    ArrayList())
            )
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
    
    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(req: HttpServletRequest?,
                                          res: HttpServletResponse,
                                          chain: FilterChain?,
                                          auth: Authentication) {
        val username = (auth.principal as User).username
        res.addHeader(HEADER_STRING, tokenFactory.create(username))
    }
}
