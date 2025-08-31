package com.ord.features.conversation.api.annotations.resolvers

//@Component
//class OwnedConversationArgumentResolver(
//    private val conversationService: ConversationService,
//    private val authenticatedUserArgumentResolver: AuthenticatedUserArgumentResolver
//) : HandlerMethodArgumentResolver {
//    override fun supportsParameter(parameter: MethodParameter): Boolean {
//        return parameter.hasParameterAnnotation(OwnedConversation::class.java) &&
//                parameter.parameterType == ConversationEntity::class.java
//    }
//
//    override fun resolveArgument(
//        parameter: MethodParameter,
//        mavContainer: ModelAndViewContainer?,
//        webRequest: NativeWebRequest,
//        binderFactory: WebDataBinderFactory?
//    ): Any {
//        val request = webRequest.nativeRequest as HttpServletRequest
//
//        val user = authenticatedUserArgumentResolver.getAuthenticatedUser(webRequest)
//        val conversation = conversationService.findByIdOrFail(
//            id = request.getConversationId(),
//            userId = user.id
//        )
//
//        return conversation
//    }
//
//    private fun HttpServletRequest.getConversationId(): UUID {
//        val conversationIdStr = this.getParameter("conversationId")
//            ?: throw BadRequestException("Missing conversationId")
//
//        return try {
//            UUID.fromString(conversationIdStr)
//        } catch (e: IllegalArgumentException) {
//            throw BadRequestException("Invalid UUID format for conversationId")
//        }
//    }
//}