package com.portalis.lib

import kotlinx.serialization.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

annotation class Choices(val value: Array<KClass<out Overview>>)


@Serializable
@Choices([OverviewHtmlSelection::class, OverviewScriptSelection::class])
sealed class Overview {

}

@Serializable
class OverviewScriptSelection(val book: String = "10") : Overview() {
}

@Serializable
class OverviewHtmlSelection(val book: String) : Overview() {
}

fun testPolymorphic() {
    val k = Overview::class.findAnnotation<Choices>()
    k?.let {
        it.value.forEach {
            println(it.qualifiedName)
        }
    }
}