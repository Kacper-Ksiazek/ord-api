package com.ord.features.conversation.models.enums

enum class ConversationType(
    val contextForAI: String,
    val examplesForAI: List<String>
) {
    SMALL_TALK(
        contextForAI = "Light and casual conversation",
        examplesForAI = listOf(
            "for A2: How are you today? What did you do this morning?",
            "for B2: What does your typical weekend look like, and do you prefer relaxing or being active?",
            "for C1: Can you describe a memorable conversation you had recently and why it stood out to you?"
        )
    ),

    SCENARIO_ROLEPLAY(
        contextForAI = "Engage in interactive role-playing scenarios between two parties. Always define both roles clearly — label one as (A) and the other as (B). The user must choose one of the roles to play.",
        examplesForAI = listOf(
            "for A2: You're visiting a local café to grab something to eat and drink. The barista greets you and asks for your order. Roles: (A) the barista, (B) the customer.",
            "for B2: You’re attending a job interview for an administrative assistant role. The interviewer asks you about your background, skills, and availability. Roles: (A) the interviewer, (B) the applicant.",
            "for C1: A client is reaching out to a freelancer to negotiate a complex web development contract, discussing project scope, pricing, and deadlines. Roles: (A) the client, (B) the freelancer."
        )
    ),

    EXAM_PRACTICE(
        contextForAI = "Prepare for language exams with practice questions and answers",
        examplesForAI = listOf(
            "for A2: Describe your last holiday — where you went, what you did, and who you went with.",
            "for B2: Discuss the advantages and disadvantages of studying online compared to attending classes in person.",
            "for C1: Present a well-reasoned argument either in favor of or against banning advertisements aimed at children, including at least two supporting points."
        )
    ),

    TOPIC_EXPLORATION(
        contextForAI = "Discuss specific topics of interest, e.g., travel, technology, culture",
        examplesForAI = listOf(
            "for A2: Tell me about a place you would like to visit and explain why you find it interesting.",
            "for B2: How has technology changed the way people communicate over the last ten years?",
            "for C1: Discuss the cultural impact of globalization on local traditions, and give an example from your country or experience."
        )
    ),

    OXFORD_DEBATE(
        contextForAI = "Engage in structured debates on various topics, following the Oxford debate format. You must choose to argue either for or against the given statement.",
        examplesForAI = listOf(
            "for A2: This house believes that school uniforms should be worn. Do you agree or disagree? Explain your choice.",
            "for B2: This house believes that social media does more harm than good. Choose a side and support your opinion.",
            "for C1: This house believes that AI-generated content should be banned in academic settings. Take a stance and present a structured argument, addressing possible objections."
        )
    )
}

