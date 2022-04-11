package com.portalis.lib

import org.json.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

object Schema {
    fun prettyPrintedSchema() : String {
        return createSchema().toString(2)
    }

    private fun createSchema() : JSONObject {
        val schema = expand(SchemaWrapper::class.fields().first())
        schema.put("\$schema", "http://json-schema.org/draft-07/schema#")
        schema.getJSONObject("properties").put("\$schema", SchemaProperty)
        return schema
    }

    val SchemaProperty = run {
        val j = JSONObject()
        j.put("type", "string")
    }

    private class SchemaWrapper(val source: Source)

    private fun KClass<*>.fields(): List<KProperty<*>> {
        return members.filterIsInstance<KProperty<*>>()
    }

    private fun KClass<*>.requiredFields(): List<KProperty<*>> {
        return fields().filter { m -> !m.returnType.isMarkedNullable }
    }

    private fun expand(prop: KProperty<*>) : JSONObject {
        val type = prop.returnType
        val objClass = type.classifier as KClass<*>

        val values = JSONObject()
        when (objClass) {
            String::class -> {
                val pattern = prop.findAnnotation<Pattern>()
                pattern?.let { p -> values.put("pattern", p.pattern) }
            }
            else -> {
                val properties = JSONObject()
                objClass.fields().forEach { m -> properties.put(m.name, expand(m))}
                values.put("properties", properties)

                val required = objClass.requiredFields()
                if (required.any()) {
                    values.put("required", required.map { f -> f.name})
                }

                values.put("additionalProperties", false)
            }
        }

        val typeString = when (objClass) {
            String::class -> "string"
            else -> "object"
        }
        values.put("type", typeString)
        return values
    }
}