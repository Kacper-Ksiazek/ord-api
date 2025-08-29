package com.ord.core.auth.security

import com.ord.core.auth.jwt.JwtService
import com.ord.core.user.model.UserEntity
import com.ord.exceptions.REST.InternalServerError
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class AuthenticatedUserArgumentResolver(
    private val jwtService: JwtService,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthenticatedUser::class.java) &&
                parameter.parameterType == UserEntity::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): UserEntity = getAuthenticatedUser(webRequest)

    fun getAuthenticatedUser(webRequest: NativeWebRequest): UserEntity {
        return jwtService.getAuthenticatedUserOrThrowForbidden(
            webRequest.nativeRequest as HttpServletRequest
        )
    }
}