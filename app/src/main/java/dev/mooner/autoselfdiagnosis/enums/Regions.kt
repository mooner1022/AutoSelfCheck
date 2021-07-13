package dev.mooner.autoselfdiagnosis.enums

enum class Regions(
    val krName: String,
    val code: String,
    val url: String
) {
    SEOUL(
        krName = "서울",
        code = "01",
        url = "https://senhcs.eduro.go.kr"
    ),
    BUSAN(
        krName = "부산",
        code = "02",
        url = "https://penhcs.eduro.go.kr"
    ),
    DAEGU(
        krName = "대구",
        code = "03",
        url = "https://dgehcs.eduro.go.kr"
    ),
    INCHEON(
        krName = "인천",
        code = "04",
        url = "https://icehcs.eduro.go.kr"
    ),
    GWANGJU(
        krName = "광주",
        code = "05",
        url = "https://genhcs.eduro.go.kr"
    ),
    DAEJEON(
        krName = "대전",
        code = "06",
        url = "https://djehcs.eduro.go.kr"
    ),
    ULSAN(
        krName = "울산",
        code = "07",
        url = "https://usehcs.eduro.go.kr"
    ),
    SEJONG(
        krName = "세종",
        code = "08",
        url = "https://sjehcs.eduro.go.kr"
    ),
    GYEONGGI(
        krName = "경기",
        code = "10",
        url = "https://goehcs.eduro.go.kr"
    ),
    GANGWON(
        krName = "강원",
        code = "11",
        url = "https://kwehcs.eduro.go.kr"
    ),
    CHOONGBUK(
        krName = "충북",
        code = "12",
        url = "https://cbehcs.eduro.go.kr"
    ),
    CHOONGNAM(
        krName = "충남",
        code = "13",
        url = "https://cnehcs.eduro.go.kr"
    ),
    JEONBUK(
        krName = "전북",
        code = "14",
        url = "https://jbehcs.eduro.go.kr"
    ),
    JEONNAM(
        krName = "전남",
        code = "15",
        url = "https://jnehcs.eduro.go.kr"
    ),
    GYEONGBUK(
        krName = "경북",
        code = "16",
        url = "https://gbehcs.eduro.go.kr"
    ),
    GYEONGNAM(
        krName = "경남",
        code = "17",
        url = "https://gnehcs.eduro.go.kr"
    ),
    JEJU(
        krName = "제주",
        code = "18",
        url = "https://jjehcs.eduro.go.kr"
    )
}