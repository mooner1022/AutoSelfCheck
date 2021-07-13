package dev.mooner.autoselfdiagnosis

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.mooner.autoselfdiagnosis.enums.Regions
import dev.mooner.autoselfdiagnosis.enums.SchoolKind
import dev.mooner.autoselfdiagnosis.objects.Config

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val school = SchoolFinder().find("상원고등학교", Regions.GYEONGGI, SchoolKind.HIGH).schoolList.first()
        val checker = SelfChecker(
            config = Config(
                region = Regions.GYEONGGI,
                kind = SchoolKind.HIGH,
                school = school,
                name = "문민기",
                birthday = "031022",
                pw = "7734"
            )
        )
        checker.check()
    }
}