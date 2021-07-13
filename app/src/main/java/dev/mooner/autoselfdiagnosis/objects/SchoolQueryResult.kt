package dev.mooner.autoselfdiagnosis.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SchoolQueryResult(
    @SerialName("schulList")
    val schoolList: List<SchoolInfo>,
    @SerialName("sizeover")
    val isSizeOver: Boolean
)
