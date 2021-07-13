package dev.mooner.autoselfdiagnosis.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SchoolInfo(
    val orgCode: String,
    @SerialName("kraOrgNm")
    val krName: String,
    @SerialName("engOrgNm")
    val enName: String,
    val insttClsfCode: String,
    @SerialName("lctnScCode")
    val regionCode: String,
    @SerialName("lctnScNm")
    val regionName: String,
    @SerialName("sigCode")
    val signatureCode: String,
    val juOrgCode: String,
    @SerialName("schulKndScCode")
    val schoolType: String,
    @SerialName("orgAbrvNm01")
    val orgAbrvName: String = "",
    @SerialName("addres")
    val address: String
)