package dev.mooner.autoselfdiagnosis

import android.util.Log
import dev.mooner.autoselfdiagnosis.enums.Regions
import dev.mooner.autoselfdiagnosis.enums.SchoolKind
import dev.mooner.autoselfdiagnosis.objects.SchoolQueryResult
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup
import java.net.URLEncoder

class SchoolFinder(
    private val name: String = "",
    private val region: Regions = Regions.GYEONGGI,
    private val kind: SchoolKind = SchoolKind.HIGH,
    private val debug: Boolean = false
) {

    private val serializer: ThreadLocal<Json> = object : ThreadLocal<Json>() {
        override fun initialValue(): Json {
            return Json {
                ignoreUnknownKeys = true
                isLenient = true
            }
        }
    }

    fun find(
        name: String = this.name,
        region: Regions = this.region,
        kind: SchoolKind = this.kind
    ): SchoolQueryResult {
        val response = Jsoup
            .connect(
                "https://hcs.eduro.go.kr/v2/searchSchool?lctnScCode=${region.code}&schulCrseScCode=${kind.code}&orgName=${
                    URLEncoder.encode(
                        name,
                        "UTF-8"
                    )
                }"
            )
            .apply {
                ignoreContentType(true)
                userAgent(Const.USER_AGENT)
            }.execute()
        val doc = response.parse()
        if (debug) {
            Log.d("SchoolFinder", doc.wholeText())
        }
        return serializer.get()!!.decodeFromString(doc.wholeText())
    }
}