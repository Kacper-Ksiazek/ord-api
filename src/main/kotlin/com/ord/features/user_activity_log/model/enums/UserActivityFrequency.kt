package com.ord.features.user_activity_log.model.enums

import com.ord.shared.annotations.ExportToOpenAPI

@ExportToOpenAPI
enum class UserActivityFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,

    NON_PERIODIC
}