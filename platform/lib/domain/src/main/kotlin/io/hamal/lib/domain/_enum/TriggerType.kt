package io.hamal.lib.domain._enum

enum class TriggerType(val value: Int) {
    Event(1),
    FixedRate(2),
    Hook(3),
    Cron(4)
}