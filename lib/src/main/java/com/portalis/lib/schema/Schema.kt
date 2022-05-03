package com.portalis.lib.schema

import com.portalis.lib.Source
import org.json.JSONObject
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

object Schema {
    fun prettyPrintedSchema(): String {
        return createSchema().toString(2)
    }

    private fun createSchema(): JSONObject {
        val definitions: MutableMap<String, KClass<*>> = mutableMapOf()
        val schema = expandClass(Source::class, definitions)
        schema.put("\$schema", "http://json-schema.org/draft-07/schema#")
        schema.getJSONObject("properties").put("\$schema", SchemaProperty)

        val outDefinitions = JSONObject()
        for ((name, definition) in definitions) {
            outDefinitions.put(name, expandClass(definition, definitions))
        }
        schema.put("definitions", outDefinitions)
        return schema
    }

    private val SchemaProperty: JSONObject = run {
        val j = JSONObject()
        j.put("type", "string")
    }

    private fun KClass<*>.fields(): List<KProperty<*>> {
        return members.filterIsInstance<KProperty<*>>()
    }

    private fun KClass<*>.requiredFields(): List<KProperty<*>> {
        return fields().filter { m -> !m.returnType.isMarkedNullable }
    }

    private fun expandProp(
        prop: KProperty<*>,
        definitions: MutableMap<String, KClass<*>>
    ): JSONObject {
        val objClass = prop.returnType.classifier as KClass<*>
        val values = expandClass(objClass, definitions)

        prop.findAnnotation<Comment>()?.let { values.put("\$comment", it.value) }
        when (objClass) {
            String::class -> {
                prop.findAnnotation<Pattern>()?.let { values.put("pattern", it.pattern) }
                prop.findAnnotation<Format>()?.let { values.put("format", it.format.jsonValue) }
            }
        }
        return values
    }

    private fun expandClass(
        objClass: KClass<*>,
        definitions: MutableMap<String, KClass<*>>
    ): JSONObject {

        val values = JSONObject()


        when (objClass.isSealed) {
            false -> expandNormal(objClass, values, definitions)
            true -> expandSealed(objClass, values, definitions)
        }

        return values
    }

    private fun expandNormal(
        objClass: KClass<*>,
        values: JSONObject,
        definitions: MutableMap<String, KClass<*>>
    ) {

        when (objClass) {
            String::class -> {}
            Int::class -> {}
            Long::class -> {}
            Double::class -> {}
            Float::class -> {}
            Boolean::class -> {}
            else -> {
                val isDefinition = definitions.values.contains(objClass)
                val requiredFields = objClass.requiredFields().map { f -> f.name }.toMutableList()
                val properties = JSONObject()
                objClass.fields()
                    .forEach { m -> properties.put(m.name, expandProp(m, definitions)) }

                if (isDefinition) {
                    requiredFields.add("type")
                    val constObject = JSONObject()
                    constObject.put("const", objClass.qualifiedName)
                    properties.put("type", constObject)
                }

                values.put("properties", properties)
                if (requiredFields.any()) {
                    values.put("required", requiredFields)
                }
                values.put("additionalProperties", false)
            }
        }

        val typeString = when (objClass) {
            String::class -> "string"
            Int::class -> "integer"
            Long::class -> "integer"
            Double::class -> "number"
            Float::class -> "number"
            Boolean::class -> "bool"
            else -> "object"
        }
        values.put("type", typeString)
    }

    private fun expandSealed(
        objClass: KClass<*>,
        values: JSONObject,
        definitions: MutableMap<String, KClass<*>>
    ) {
        val references = mutableListOf<JSONObject>()
        objClass.sealedSubclasses.forEach {
            val name = it.qualifiedName!!
            val jsonObject = JSONObject()
            jsonObject.put("\$ref", "#/definitions/$name")
            references.add(jsonObject)

            definitions[name] = it
        }
        values.put("oneOf", references)
    }
}