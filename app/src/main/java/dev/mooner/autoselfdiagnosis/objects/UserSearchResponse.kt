package dev.mooner.autoselfdiagnosis.objects

import kotlinx.serialization.Serializable

@Serializable
data class UserSearchResponse(
    val orgName: String,
    val userName: String,
    val token: String
)