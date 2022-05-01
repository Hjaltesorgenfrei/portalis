package com.portalis.lib

class ChapterContent(val content: List<ContentElement>)

sealed class ContentElement

class TextContent(val text: String) : ContentElement()

class ImageContent(val url: String) : ContentElement()

object HorizontalLine : ContentElement()