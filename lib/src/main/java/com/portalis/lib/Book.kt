package com.portalis.lib

import java.io.Serializable

class Book(val title: String, val uri: String, val imageUri: String, val chapters: List<Chapter> = listOf()) : Serializable
