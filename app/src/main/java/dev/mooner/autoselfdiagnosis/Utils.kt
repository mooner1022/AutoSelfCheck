package dev.mooner.autoselfdiagnosis

import android.os.Build
import java.util.*

class Utils {
    companion object {
        fun decodeBase64(str: String): ByteArray {
            return if (Build.VERSION.SDK_INT >= 26) {
                Base64.getDecoder().decode(str)
            } else {
                android.util.Base64.decode(str, 0)
            }
        }

        fun encodeBase64(arr: ByteArray): String {
            return if (Build.VERSION.SDK_INT >= 26) {
                Base64.getEncoder().encodeToString(arr)
            } else {
                android.util.Base64.encodeToString(arr, 0)
            }
        }

        fun Int.addZero(): String {
            return if (this <= 9) "0$this"
            else this.toString()
        }

        fun Int.isWeekdays(): Boolean = this in 2..6

        fun formatTime(millis: Long): String {
            val day = 1000 * 60 * 60 * 24
            val seconds = millis / 1000
            val s = seconds % 60
            val m = (seconds / 60) % 60
            val h = (seconds / (60 * 60)) % 24
            return if (millis >= day) {
                val d = seconds / (60 * 60 * 24)
                String.format("%d일 %d시간 %02d분 %02d초", h, m, s, d)
            } else {
                String.format("%d시간 %02d분 %02d초", h, m, s)
            }
        }
    }
}