package com.ord.features.conversation.models.conversation

import com.ord.features.conversation.models.conversation.enums.RecencyBucket
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

fun computeRecencyBucket(activity: Instant, now: Instant): RecencyBucket {
    val activityDate = activity.atZone(ZoneOffset.UTC).toLocalDate()
    val today = now.atZone(ZoneOffset.UTC).toLocalDate()
    val yesterday = today.minusDays(1)
    val weekMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    return when {
        activityDate == today -> RecencyBucket.TODAY
        activityDate == yesterday -> RecencyBucket.YESTERDAY
        activityDate >= weekMonday && activityDate < today -> RecencyBucket.THIS_WEEK
        activityDate.year == today.year && activityDate.month == today.month -> RecencyBucket.THIS_MONTH
        else -> RecencyBucket.LATER
    }
}
