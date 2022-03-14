package com.portalis.lib

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class Parser(input: String) {
    val bookSelector: BookSelector = Json.decodeFromString(input)
}

@Serializable
class BookSelector(val book: String, val title: String, val uri: String, val imageUri: String)