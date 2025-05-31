package com.backend.ord.core.auth.security

import com.backend.ord.core.auth.jwt.JwtService
import com.backend.ord.core.user.model.UserEntity
import com.backend.ord.exceptions.REST.InternalServerError
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
    ): Any? {
        val httpServletRequest =
            webRequest.getNativeRequest(HttpServletRequest::class.java)
                ?: throw InternalServerError("Request is not found")

        return jwtService.getAuthenticatedUserOrThrowForbidden(httpServletRequest)
    }
}