package com.nbs.banking.randomactivityidea.domain.model

data class RandomActivityIdea(
    val activity: String,
    val availability: Double,
    val type: String,
    val participants: Int,
    val price: Double,
    val accessibility: String,
    val duration: String,
    val kidFriendly: Boolean,
    val link: String,
    val key: String,
)
