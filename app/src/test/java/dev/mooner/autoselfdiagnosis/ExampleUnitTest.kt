package dev.mooner.autoselfdiagnosis

import dev.mooner.autoselfdiagnosis.enums.Regions
import dev.mooner.autoselfdiagnosis.enums.SchoolKind
import dev.mooner.autoselfdiagnosis.objects.Config
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
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
        checker.getFirstToken()
    }
}