package dev.mooner.autoselfdiagnosis

import dev.mooner.autoselfdiagnosis.Const.Companion.ENC_KEY
import dev.mooner.autoselfdiagnosis.objects.Config
import dev.mooner.autoselfdiagnosis.objects.SelfCheckResult
import dev.mooner.autoselfdiagnosis.objects.UserSearchResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.security.KeyFactory
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

class SelfChecker(
    private val config: Config
) {

    companion object {
        private const val PATH_FIND_USER = "/v2/findUser"
        private const val PATH_VALIDATE_PW = "/v2/validatePassword"
        private const val PATH_SELECT_USER_GROUP = "/v2/selectUserGroup"
        private const val PATH_GET_USER_INFO = "/v2/getUserInfo"
        private const val PATH_REGISTER_SERVEY = "/registerServey"

        private val headers: Map<String, String> = mapOf(
            "Content-Type" to "application/json; Charset=UTF-8",
            "Origin" to "https://hcs.eduro.go.kr",
            "Referer" to "https://hcs.eduro.go.kr/"
        )
    }

    private lateinit var userPNo: String

    private val serializer: ThreadLocal<Json> = object : ThreadLocal<Json>() {
        override fun initialValue(): Json {
            return Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
    }

    private fun encrypt(msg: String): String {
        val bytes = Utils.decodeBase64(ENC_KEY)
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(bytes))
        val cipher = Cipher.getInstance("RSA/None/PKCS1Padding").apply {
            init(Cipher.ENCRYPT_MODE, publicKey)
        }
        return Utils.encodeBase64(cipher.doFinal(msg.toByteArray(Charsets.UTF_8)))
    }

    private fun getFirstToken(): String {
        val url = config.region.url + PATH_FIND_USER
        val data = mapOf(
            "orgCode" to config.school.orgCode,
            "name" to encrypt(config.name),
            "birthday" to encrypt(config.birthday),
            "stdntPNo" to null,
            "loginType" to "school"
        )

        val response = Jsoup.connect(url).apply {
            method(Connection.Method.POST)
            ignoreContentType(true)
            userAgent(Const.USER_AGENT)
            headers(headers)
            requestBody(serializer.get()!!.encodeToString(data))
        }.execute()
        val document: UserSearchResponse = serializer.get()!!.decodeFromString(response.parse().wholeText())
        return document.token
    }

    private fun getSecondToken(): String {
        val firstToken = getFirstToken()
        val url = config.region.url + PATH_VALIDATE_PW
        val data = mapOf(
            "password" to encrypt(config.pw),
            "deviceUuid" to ""
        )
        val response = Jsoup.connect(url).apply {
            method(Connection.Method.POST)
            ignoreContentType(true)
            userAgent(Const.USER_AGENT)
            header("Authorization", firstToken)
            headers(headers)
            requestBody(serializer.get()!!.encodeToString(data))
        }.execute()
        val doc = response.parse()
        return doc.text().replace("\"", "")
    }

    private fun getThirdToken(): String {
        val secondToken = getSecondToken()
        val url = config.region.url + PATH_SELECT_USER_GROUP
        val response = Jsoup.connect(url).apply {
            method(Connection.Method.POST)
            ignoreContentType(true)
            userAgent(Const.USER_AGENT)
            header("Authorization", secondToken)
            headers(headers)
            requestBody("{}")
        }.execute()
        val arr: Array<Map<String, String>> = serializer.get()!!.decodeFromString((response).parse().wholeText())
        val doc = arr.first()
        this.userPNo = doc["userPNo"]!!
        return doc["token"]!!
    }

    fun check(): SelfCheckResult {
        val token = getThirdToken()
        val url = config.region.url + PATH_REGISTER_SERVEY
        val data = mapOf(
            "rspns01" to "1",
            "rspns02" to "1",
            "rspns03" to null,
            "rspns04" to null,
            "rspns05" to null,
            "rspns06" to null,
            "rspns07" to null,
            "rspns08" to null,
            "rspns09" to "0",
            "rspns10" to null,
            "rspns11" to null,
            "rspns12" to null,
            "rspns13" to null,
            "rspns14" to null,
            "rspns15" to null,
            "rspns00" to "Y",
            "deviceuuid" to "",
            "upperToken" to token,
            "upperUserNameEncpt" to config.name
        )
        val response = Jsoup.connect(url).apply {
            method(Connection.Method.POST)
            ignoreContentType(true)
            userAgent(Const.USER_AGENT)
            header("Authorization", token)
            headers(headers)
            requestBody(serializer.get()!!.encodeToString(data))
        }.execute()
        val doc: Map<String, String> = serializer.get()!!.decodeFromString(response.parse().wholeText())
        val checkTime = doc["inveYmd"]!!
        return SelfCheckResult(
            name = config.name,
            birthday = config.birthday,
            school = config.school,
            checkTime = checkTime,
            schoolCode = config.school.orgCode
        )
    }
}