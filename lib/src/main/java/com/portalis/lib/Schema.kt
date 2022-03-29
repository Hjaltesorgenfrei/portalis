package com.portalis.lib

import com.fasterxml.jackson.databind.JsonNode
import com.github.victools.jsonschema.generator.OptionPreset
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import com.github.victools.jsonschema.generator.SchemaVersion
import org.jetbrains.annotations.Nullable
import java.io.File


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
}