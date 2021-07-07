package com.example.nsa.model

data class ResponseWrapper(val response: Response)

data class Response (val docs: ArrayList<Docs>)

data class Docs(
    val web_url: String,
    val multimedia: ArrayList<Media>,
    val headline: Headline
)

data class Media(
    val subtype: String,
    val url: String
)

data class Headline(
    val main: String
)
