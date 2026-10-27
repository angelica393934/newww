package com.nbs.banking.randomactivityidea.data.mapper

import com.nbs.banking.randomactivityidea.data.model.respone.RandomActivityIdeaResponse
import com.nbs.banking.randomactivityidea.domain.model.RandomActivityIdea
import com.nbs.banking.core.extensions.orFalse
import com.nbs.banking.core.extensions.orZero

fun RandomActivityIdeaResponse.toDomain(): RandomActivityIdea = RandomActivityIdea(
    activity = activity.orEmpty(),
    availability = availability.orZero(),
    type = type.orEmpty(),
    participants = participants.orZero(),
    price = price.orZero(),
    accessibility = accessibility.orEmpty(),
    duration = duration.orEmpty(),
    kidFriendly = kidFriendly.orFalse(),
    link = link.orEmpty(),
    key = key.orEmpty()
)
