package com.portalis.lib.schema

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


internal class SchemaTest {
    @Test
    fun addition_isCorrect() {
        class BooleanClass(val b: Boolean)

        val schema = Schema.createSchema(BooleanClass::class)
        val props = schema.getJSONObject("properties")
        val b = props.getJSONObject("b")
        assertEquals("bool", b.getString("type"))
    }
}