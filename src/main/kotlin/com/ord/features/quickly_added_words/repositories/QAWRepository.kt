package com.ord.features.quickly_added_words.repositories

import com.ord.features.quickly_added_words.model.QuicklyAddedWordEntity
import com.ord.shared.repositories.UserResourceRepository

interface QAWRepository :
    UserResourceRepository<QuicklyAddedWordEntity>,
    QAWRepositoryCustomMethods