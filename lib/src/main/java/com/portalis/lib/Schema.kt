package com.portalis.lib

import com.fasterxml.jackson.databind.JsonNode
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KParameter


object Schema {
    fun outputSchema(file: File) {
        val configBuilder =
            SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_7, OptionPreset.PLAIN_JSON)
        configBuilder.forFields().withRequiredCheck { field ->
            !field.member.name.startsWith("_")
        }
        val config = configBuilder.build()
        val generator = SchemaGenerator(config)
        val jsonSchema: JsonNode = generator.generateSchema(Source::class.java)

        file.writeText(jsonSchema.toPrettyString())
    }

    fun outputCustomSchema() {
        println("Printing Schema")
        val objClass = Source::class
        val constructor = objClass.constructors.filter { c ->
            !c.parameters.any {
                    p -> p.name != null && p.name!!.contains("serializationConstructorMarker")
            }
        }.first()
        for (parameter in constructor.parameters) {
                output(parameter, 0)
        }
    }

    private fun output(parameter: KParameter, depth: Int) {
        val objClass = parameter.type.classifier as KClass<*>
        val optional = parameter.type.isMarkedNullable;
        val constructor = objClass.constructors.filter { c ->
            !c.parameters.any {
                    p -> p.name != null && p.name!!.contains("serializationConstructorMarker")
            }
        }.first()
        println(" ".repeat(depth) + "${parameter.name} $objClass $optional")
        for (parameter in constructor.parameters) {
            output(parameter, depth + 2)
        }
    }
}