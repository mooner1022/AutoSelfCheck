package dev.mooner.autoselfdiagnosis.objects

import dev.mooner.autoselfdiagnosis.enums.Regions
import dev.mooner.autoselfdiagnosis.enums.SchoolKind
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val region: Regions,
    val kind: SchoolKind,
    val school: SchoolInfo,
    val name: String,
    val birthday: String,
    val pw: String,
    var hour: Int,
    var minute: Int
)