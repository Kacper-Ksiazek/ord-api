package com.ord.features.conversation.models.conversation

import com.ord.features.conversation.models.conversation.enums.RecencyBucket
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

data class InstantRange(val from: Instant?, val until: Instant?)

fun recencyBucketToInstantRange(bucket: RecencyBucket, now: Instant): InstantRange {
    val zone = ZoneOffset.UTC
    val today = now.atZone(zone).toLocalDate()
    val yesterday = today.minusDays(1)
    val weekMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val monthStart = today.withDayOfMonth(1)

    fun LocalDate.toInstant() = atStartOfDay(zone).toInstant()

    return when (bucket) {
        RecencyBucket.TODAY -> InstantRange(today.toInstant(), today.plusDays(1).toInstant())
        RecencyBucket.YESTERDAY -> InstantRange(yesterday.toInstant(), today.toInstant())
        RecencyBucket.THIS_WEEK -> InstantRange(weekMonday.toInstant(), yesterday.toInstant())
        RecencyBucket.THIS_MONTH -> InstantRange(monthStart.toInstant(), weekMonday.toInstant())
        RecencyBucket.LATER -> InstantRange(null, monthStart.toInstant())
    }
}

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
