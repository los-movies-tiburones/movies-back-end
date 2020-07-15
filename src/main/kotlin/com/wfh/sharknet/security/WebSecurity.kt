package com.wfh.sharknet.security

import com.wfh.sharknet.factory.TokenFactory
import com.wfh.sharknet.security.SecurityConstants.SIGN_UP_URL
import com.wfh.sharknet.service.UserDetailsServiceImpl
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@EnableWebSecurity
class WebSecurity(
    private val userDetailsService: UserDetailsServiceImpl,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val tokenFactory: TokenFactory
): WebSecurityConfigurerAdapter() {
    
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.cors()
            .and()
                .csrf()
                .disable()
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, SIGN_UP_URL).permitAll()
                    .antMatchers(HttpMethod.GET,
                        "/movies",
                        "movies/genres",
                        "movies/top-rating",
                        "/actuator/**",
                        "/swagger**",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v2/**"
                    ).permitAll()
                .anyRequest()
                .authenticated()
            .and()
                .addFilter(JWTAuthenticationFilter(authenticationManager(), tokenFactory))
                .addFilter(JWTAuthorizationFilter(authenticationManager())) // this disables session creation on Spring Security
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }
    
    @Throws(Exception::class)
    public override fun configure(auth: AuthenticationManagerBuilder) {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder)
    }
    
    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
        return source
    }
    
    @Bean
    @Throws(java.lang.Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager? {
        return super.authenticationManagerBean()
    }
}