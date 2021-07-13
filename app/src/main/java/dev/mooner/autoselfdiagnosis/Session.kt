package dev.mooner.autoselfdiagnosis

object Session {
    val stateMap: HashMap<Int, Any> = hashMapOf()
    var nextAlarmTime: Long = System.currentTimeMillis()
}