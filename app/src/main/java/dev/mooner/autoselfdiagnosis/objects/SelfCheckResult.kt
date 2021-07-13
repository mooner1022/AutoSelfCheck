package dev.mooner.autoselfdiagnosis.objects

data class SelfCheckResult(
    val name: String,
    val birthday: String,
    val school: SchoolInfo,
    val checkTime: String,
    val schoolCode: String
)
