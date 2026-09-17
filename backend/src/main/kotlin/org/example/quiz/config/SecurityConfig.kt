package org.example.quiz.config

import org.example.quiz.entity.User
import org.example.quiz.repository.UserRepository
import jakarta.servlet.http.HttpServletResponse
import org.example.quiz.entity.Role
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@ConditionalOnProperty(name = ["app.security.enabled"], havingValue = "true", matchIfMissing = true)
class SecurityConfig(
    private val userRepository: UserRepository,
    @Value("\${frontend.url}") private val frontendUrl: String,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/oauth2/**", "/login/**", "/error").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN") // adds ROLE_
                    .requestMatchers("/api/user/me").authenticated()
                    .requestMatchers("/api/**").authenticated()
                    .anyRequest().permitAll()
            }
            .exceptionHandling { ex ->
                ex.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .oauth2Login { oauth2 ->
                oauth2
                    .userInfoEndpoint { userInfo ->
                        userInfo.oidcUserService(customOidcUserService())
                    }
                    .successHandler(oauthSuccessHandler())
            }
            .logout { logout ->
                logout
                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessHandler { _, response, _ ->
                        response.status = HttpServletResponse.SC_OK
                    }
            }
        return http.build()
    }

    private fun oauthSuccessHandler() = AuthenticationSuccessHandler { _, response, authentication ->
        response.sendRedirect(frontendUrl)
    }

    @Bean
    fun customOidcUserService(): OAuth2UserService<OidcUserRequest, OidcUser> {
        val delegate = OidcUserService()

        return OAuth2UserService { userRequest ->
            val oidcUser = delegate.loadUser(userRequest)

            val email = oidcUser.email ?: throw IllegalStateException("Email missing")
            val user = userRepository.findByEmail(email)
                ?: userRepository.save(
                    User(
                        googleId = oidcUser.subject,
                        email = email,
                        roles = mutableSetOf(Role.USER)
                    )
                )

            val authorities = user.roles.map {
                SimpleGrantedAuthority("ROLE_${it.name}")
            }.toSet()

            DefaultOidcUser(authorities, oidcUser.idToken, oidcUser.userInfo)
        }
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf(frontendUrl)
        config.allowedOriginPatterns = listOf("*")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}
