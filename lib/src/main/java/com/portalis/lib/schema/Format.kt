package com.portalis.lib.schema

enum class Formats(val jsonValue: String) {
    // Taken from https://json-schema.org/understanding-json-schema/reference/string.html#built-in-formats

    // Dates and times
    DATE_TIME("date-time"),
    TIME("time"),
    DATE("date"),
    DURATION("duration"),

    // Email addresses
    EMAIL("email"),
    IDN_EMAIL("idn-hostname"),

    // Hostnames
    HOSTNAME("hostname"),
    IDN_HOSTNAME("idn-hostname"),

    // IP Addresses
    IPV4("ipv4"),
    IPV6("ipv6"),

    // Resource identifiers
    UUID("uuid"),
    URI("uri"),
    URI_REFERENCE("uri-reference"),
    IRI("iri"),
    IRI_REFERENCE("iri-reference")
}

annotation class Format(val format: Formats) {
}
