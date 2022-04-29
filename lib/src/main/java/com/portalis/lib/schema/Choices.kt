package com.portalis.lib.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

annotation class Choices(val value: Array<KClass<out Overview>>)


@Serializable
@Choices([OverviewHtmlSelection::class, OverviewScriptSelection::class])
sealed class Overview {

}

@Serializable
@SerialName("SCRIPT")
class OverviewScriptSelection(val book: String) : Overview() {
}

@Serializable
@SerialName("HTML")
class OverviewHtmlSelection(val bookster: String) : Overview() {
}

@Serializable
class PolyParser(val overview: Overview)

fun testPolymorphic() {
    val k = Overview::class.findAnnotation<Choices>()
    k?.let {
        it.value.forEach {
            println(it.qualifiedName)
        }
    }
    val o: PolyParser = PolyParser(OverviewScriptSelection("qwd"))
    val s = Json.encodeToString(o)
    println(s)
    val o_out: PolyParser = Json.decodeFromString(s)
    println(o_out.overview)
}