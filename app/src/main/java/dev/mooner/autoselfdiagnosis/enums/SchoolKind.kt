package dev.mooner.autoselfdiagnosis.enums

enum class SchoolKind(
    val krName: String,
    val code: Int
) {
    KINDERGARDEN(
        krName = "유치원",
        code = 1
    ),
    ELEMENTARY(
        krName = "초등학교",
        code = 2
    ),
    MIDDLE(
        krName = "중학교",
        code = 3
    ),
    HIGH(
        krName = "고등학교",
        code = 4
    ),
    SPECIAL(
        krName = "특수학교",
        code = 5
    ),
}