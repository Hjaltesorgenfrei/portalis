package com.portalis.lib

import com.fasterxml.jackson.databind.JsonNode
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion


object Schema {
    fun outputSchema() {
        val configBuilder =
            SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_7, OptionPreset.PLAIN_JSON)
        val config = configBuilder.build()
        val generator = SchemaGenerator(config)
        val jsonSchema: JsonNode = generator.generateSchema(Source::class.java)

        println(jsonSchema.toString())
    }
}