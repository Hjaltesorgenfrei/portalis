package com.hjadal.portalis

import com.portalis.lib.Parser
import javax.inject.Inject

private const val source = """
{
  "baseurl": "https://www.royalroad.com",
  "topRated": "/fictions/best-rated",
  "overviewSelector": {
    "book": ".fiction-list-item.row",
    "bookTitle": ".fiction-title",
    "bookUri": "a[href]",
    "bookImageUri": "img[src]"
  },
  "bookSelector": {
    "title": ".fic-title h1",
    "author": ".fic-title h4 a",
    "description": ".description",
    "imageUri": "img.thumbnail[src]",
    "chapterTitle": "a",
    "chapter": ".chapter-row",
    "chapterDate": "time",
    "chapterUri": "a"
  }
}

"""

class RoyalRoadParser @Inject constructor() {
    public val parser = Parser(source)
}