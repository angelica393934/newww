package com.nbs.banking.core.extensions
//
//import kotlinx.serialization.DeserializationStrategy
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.encodeToString
//import kotlinx.serialization.json.Json
//import kotlinx.serialization.json.JsonArray
//import kotlinx.serialization.json.JsonArrayBuilder
//import kotlinx.serialization.json.JsonElement
//import kotlinx.serialization.json.JsonNull
//import kotlinx.serialization.json.JsonObject
//import kotlinx.serialization.json.JsonObjectBuilder
//import kotlinx.serialization.json.JsonPrimitive
//import kotlinx.serialization.json.booleanOrNull
//import kotlinx.serialization.json.buildJsonArray
//import kotlinx.serialization.json.buildJsonObject
//import kotlinx.serialization.json.decodeFromJsonElement
//import kotlinx.serialization.json.doubleOrNull
//import kotlinx.serialization.json.encodeToJsonElement
//import kotlinx.serialization.json.intOrNull
//import kotlinx.serialization.json.jsonObject
//import kotlinx.serialization.json.longOrNull
//import timber.log.Timber
//
///**
// * Shared [Json] instance configured with defaults that match the previous Gson behavior:
// * - [JsonBuilder.encodeDefaults][kotlinx.serialization.json.JsonBuilder.encodeDefaults] keeps properties with default values.
// * - [JsonBuilder.explicitNulls][kotlinx.serialization.json.JsonBuilder.explicitNulls] serializes null properties.
// * - [JsonBuilder.ignoreUnknownKeys][kotlinx.serialization.json.JsonBuilder.ignoreUnknownKeys] tolerates extra fields coming from the backend.
// *
// * Field naming policies (the old `GsonFieldNameRules`) are handled per serializer via
// * `@SerialName` or by constructing a dedicated [Json] instance with a `namingStrategy`.
// */
//val defaultJson: Json = Json {
//    encodeDefaults = true
//    explicitNulls = true
//    ignoreUnknownKeys = true
//    isLenient = true
//    coerceInputValues = true
//}
//
////region empty values
//
//const val EMPTY_JSON_OBJECT: String = "{}"
//
//const val EMPTY_JSON_ARRAY: String = "[]"
//
//fun emptyJsonObject(): String = EMPTY_JSON_OBJECT
//
//fun emptyJsonArray(): String = EMPTY_JSON_ARRAY
//
//fun String.isNotEmptyJsonObject(): Boolean = this != emptyJsonObject()
//
//fun String.isNotEmptyJsonArray(): Boolean = this != emptyJsonArray()
//
//fun String.isEmptyJsonObject(): Boolean = this == emptyJsonObject()
//
//fun String.isEmptyJsonArray(): Boolean = this == emptyJsonArray()
//
////endregion
//
////region value <-> JsonElement conversion
//
///**
// * Converts any value into its [JsonElement] representation.
// * Map/List/primitive aware; unknown types fall back to their `toString()`.
// */
//fun Any?.toJsonElement(): JsonElement = when (this) {
//    null -> JsonNull
//    is JsonElement -> this
//    is String -> JsonPrimitive(this)
//    is Number -> JsonPrimitive(this)
//    is Boolean -> JsonPrimitive(this)
//    is Char -> JsonPrimitive(this.toString())
//    is Map<*, *> -> buildJsonObject {
//        this@toJsonElement.forEach { (key, value) -> put(key.toString(), value.toJsonElement()) }
//    }
//    is Iterable<*> -> buildJsonArray {
//        this@toJsonElement.forEach { add(it.toJsonElement()) }
//    }
//    is Array<*> -> buildJsonArray {
//        this@toJsonElement.forEach { add(it.toJsonElement()) }
//    }
//    else -> JsonPrimitive(this.toString())
//}
//
///**
// * Converts a [JsonElement] back into a plain Kotlin value
// * ([Map], [List], [String], [Boolean], [Int], [Long], [Double] or null).
// */
//fun JsonElement.toAnyType(): Any? = when (this) {
//    is JsonNull -> null
//    is JsonObject -> mapValues { it.value.toAnyType() }
//    is JsonArray -> this.map { it.toAnyType() }
//    is JsonPrimitive -> when {
//        isString -> content
//        booleanOrNull != null -> booleanOrNull
//        intOrNull != null -> intOrNull
//        longOrNull != null -> longOrNull
//        doubleOrNull != null -> doubleOrNull
//        else -> content
//    }
//}
//
////endregion
//
////region safe casts
//
//@Suppress("UNCHECKED_CAST")
//fun Any?.asMapStringAny(): Map<String, Any?>? = runCatching {
//    when (this) {
//        is JsonObject -> mapValues { it.value.toAnyType() }
//        is Map<*, *> -> this as? Map<String, Any?>
//        else -> null
//    }
//}.onFailure { e ->
//    Timber.e("Failed to cast to Map<String, Any>: %s", e.message)
//}.getOrNull()
//
//@Suppress("UNCHECKED_CAST")
//fun Any?.asHashMapStringAny(): HashMap<String, Any?>? = runCatching {
//    when (this) {
//        is JsonObject -> HashMap(mapValues { it.value.toAnyType() })
//        is Map<*, *> -> (this as? Map<String, Any?>)?.let(::HashMap)
//        else -> null
//    }
//}.onFailure { e ->
//    Timber.e("Failed to cast to HashMap<String, Any>: %s", e.message)
//}.getOrNull()
//
//fun Any?.asListMapStringAny(): List<Map<String, Any?>> = runCatching {
//    when (this) {
//        is JsonArray -> mapNotNull { (it as? JsonObject)?.mapValues { e -> e.value.toAnyType() } }
//        is List<*> -> mapNotNull { it.asMapStringAny() }
//        else -> emptyList()
//    }
//}.onFailure { e ->
//    Timber.e("Failed to cast to List<Map<String, Any>>: %s", e.message)
//}.getOrNull().orEmpty()
//
//fun Any?.asString(): String? = runCatching {
//    when (this) {
//        is String -> this
//        is JsonPrimitive -> content
//        else -> null
//    }
//}.onFailure { e ->
//    Timber.e("Failed to cast to String: %s", e.message)
//}.getOrNull()
//
//fun Any?.asListString(): List<String> = runCatching {
//    when (this) {
//        is JsonArray -> mapNotNull { (it as? JsonPrimitive)?.content }
//        is List<*> -> this.mapNotNull { it?.toString() }
//        else -> emptyList()
//    }
//}.onFailure { e ->
//    Timber.e("Failed to cast to List<String>: %s", e.message)
//}.getOrNull().orEmpty()
//
//@Suppress("UNCHECKED_CAST")
//inline fun <reified T> Any?.asGenericType(): T? = runCatching {
//    this as? T ?: if (this is JsonElement) defaultJson.decodeFromJsonElement<T>(this) else null
//}.onFailure { e ->
//    Timber.e("Failed to cast to ${T::class.java.simpleName}: %s", e.message)
//}.getOrNull()
//
//@Suppress("UNCHECKED_CAST")
//inline fun <reified T> Any?.asListGenericType(): List<T> = runCatching {
//    when (this) {
//        is JsonArray -> mapNotNull { it.asGenericType<T>() }
//        is List<*> -> this as? List<T>
//        else -> emptyList()
//    }
//}.onFailure { e ->
//    Timber.e("Failed to cast to List<${T::class.java.simpleName}>: %s", e.message)
//}.getOrNull().orEmpty()
//
//fun Any?.asListUndefined(): List<*> = runCatching {
//    when (this) {
//        is JsonArray -> this
//        is List<*> -> this
//        else -> emptyList<Any>()
//    }
//}.onFailure { e ->
//    Timber.e("Failed to cast to undefined")
//}.getOrNull().orEmpty()
//
////endregion
//
////region encode
//
///**
// * Converts an object of type [T] to a compact JSON string, or empty string if null.
// */
//inline fun <reified T> T?.toJsonOrEmpty(json: Json = defaultJson): String =
//    runCatching {
//        this?.let { json.encodeToString<T>(it) }.orEmpty()
//    }.onFailure { e ->
//        Timber.e("Error converting object to Json: %s", e.message)
//    }.getOrNull().orEmpty()
//
///**
// * Re-serializes a JSON string through a [JsonElement] tree.
// * Returns an empty string if the input is null or invalid.
// */
//fun String?.toJsonOrEmpty(json: Json = defaultJson): String =
//    runCatching {
//        json.parseToJsonElement(this.orEmpty()).toString()
//    }.onFailure { e ->
//        Timber.e("Error converting object to JsonElement: %s", e.message)
//    }.getOrNull().orEmpty()
//
///**
// * Parses a JSON string to a [JsonElement], or an empty [JsonObject] on failure.
// */
//fun String?.toJsonElementOrEmpty(json: Json = defaultJson): JsonElement = runCatching {
//    json.parseToJsonElement(this.orEmpty())
//}.onFailure { e ->
//    Timber.e("Error converting object to JsonElement: %s", e.message)
//}.getOrElse { JsonObject(emptyMap()) }
//
///**
// * Converts an object of type [T] to a [JsonElement]; an empty [JsonObject] is returned if null.
// */
//inline fun <reified T> T?.toJsonTreeObject(json: Json = defaultJson): JsonElement = runCatching {
//    this?.let { json.encodeToJsonElement<T>(it) } ?: JsonObject(emptyMap())
//}.onFailure { e ->
//    Timber.e("Error converting object to JsonTree: %s", e.message)
//}.getOrElse { JsonObject(emptyMap()) }
//
///**
// * Converts a [List] to a [JsonArray]; an empty array is returned if null.
// */
//fun List<*>?.toJsonTreeArray(): JsonElement = runCatching {
//    buildJsonArray {
//        this@toJsonTreeArray?.forEach { value ->
//            add(value.toJsonElement())
//        }
//    }
//}.onFailure { e ->
//    Timber.e("Error converting list to JSON: %s", e.message)
//}.getOrElse { JsonArray(emptyList()) }
//
///**
// * Adds a property to a JSON object builder, skipping unsupported values.
// */
//fun JsonObjectBuilder.safeAddProperty(
//    key: String,
//    value: Any?,
//    shouldSerializeNull: Boolean = true,
//): JsonObjectBuilder = apply {
//    runCatching {
//        when (value) {
//            null -> if (shouldSerializeNull) put(key, JsonNull)
//            else -> put(key, value.toJsonElement())
//        }
//    }.onFailure { e ->
//        Timber.e("Failed to add property to JSON: %s", e.message)
//    }
//}
//
///**
// * Converts an object of type [T] to a [Map] of JSON values.
// */
//inline fun <reified T> T?.toMapStringAny(json: Json = defaultJson): Map<String, Any?> = runCatching {
//    this?.let { json.encodeToJsonElement<T>(it) }?.jsonObject?.mapValues { it.value.toAnyType() }
//        .orEmpty()
//}.onFailure { e ->
//    Timber.e("Error parsing object to map: %s", e.message)
//}.getOrNull().orEmpty()
//
///**
// * Converts an object of type [T] to a [HashMap] of JSON values.
// */
//inline fun <reified T> T?.toHashMapStringAny(json: Json = defaultJson): HashMap<String, Any?> = runCatching {
//    this?.let { json.encodeToJsonElement<T>(it) }?.jsonObject?.let { map -> HashMap(map.mapValues { it.value.toAnyType() }) }
//        ?: hashMapOf()
//}.onFailure { e ->
//    Timber.e("Error parsing object to map: %s", e.message)
//}.getOrNull() ?: hashMapOf()
//
///**
// * Converts any value to a [JsonObject]; an empty [JsonObject] is returned if the value
// * is not a JSON object.
// */
//fun Any?.toJsonObjectOrEmpty(): JsonObject = runCatching {
//    when (val element = toJsonElement()) {
//        is JsonObject -> element
//        else -> JsonObject(emptyMap())
//    }
//}.onFailure { e ->
//    Timber.e("Error converting object to JSON: %s", e.message)
//}.getOrDefault(JsonObject(emptyMap()))
//
///**
// * Parses a JSON string to a [JsonObject] and applies [block] to build/mutate it.
// */
//fun String?.toJsonObjectOrEmpty(block: JsonObjectBuilder.() -> Unit = {}): JsonObject = runCatching {
//    val parsed = toJsonElementOrEmpty()
//    buildJsonObject {
//        if (parsed is JsonObject) {
//            parsed.forEach { (key, value) -> put(key, value) }
//        }
//        block()
//    }
//}.onFailure { e ->
//    Timber.e("Error converting object to JSONObject: %s", e.message)
//}.getOrDefault(JsonObject(emptyMap()))
//
///**
// * Converts a JSON string to a [HashMap] of JSON values.
// */
//fun String?.toHashMapStringAny(json: Json = defaultJson): HashMap<String, Any>? = runCatching {
//    if (this.isNullOrEmpty()) return@runCatching null
//    json.parseToJsonElement(this).jsonObject
//        .entries
//        .fold(HashMap<String, Any>()) { acc, entry -> acc.apply { put(entry.key, entry.value.toAnyType() as Any) } }
//}.onFailure { e ->
//    Timber.e("Error converting JSON to HashMap: %s", e.message)
//}.getOrNull()
//
////endregion
//
////region decode
//
///**
// * Parses a JSON string to an object of type [T], or null on failure.
// */
//inline fun <reified T> String?.fromJsonOrNull(json: Json = defaultJson): T? = runCatching {
//    if (this.isNullOrEmpty()) return@runCatching null
//    json.decodeFromString<T>(this)
//}.onFailure { e ->
//    Timber.e("Failed to parse fromJsonOrNull: %s", e.message)
//}.getOrNull()
//
///**
// * Parses a JSON string to an object using an explicit [DeserializationStrategy], or null on failure.
// */
//fun <T> String?.fromJsonOrNull(
//    deserializer: DeserializationStrategy<T>,
//    json: Json = defaultJson,
//): T? = runCatching {
//    if (this.isNullOrEmpty()) return@runCatching null
//    json.decodeFromString(deserializer, this)
//}.onFailure { e ->
//    Timber.e("Failed to parse fromJsonOrNull: %s", e.message)
//}.getOrNull()
//
///**
// * Returns the receiver [JsonElement] unchanged, or null if it is already null.
// */
//fun JsonElement?.fromJsonOrNull(): JsonElement? = this
//
///**
// * Parses a [JsonElement] to an object of type [T], or null on failure.
// */
//inline fun <reified T> JsonElement?.fromJsonOrNull(json: Json = defaultJson): T? = runCatching {
//    if (this == null) return@runCatching null
//    json.decodeFromJsonElement<T>(this)
//}.onFailure { e ->
//    Timber.e("Failed to parse fromJsonElementOrNull: %s", e.message)
//}.getOrNull()
//
///**
// * Parses a [JsonElement] to an object of type [T] using an explicit [DeserializationStrategy],
// * or null on failure.
// */
//fun <T> JsonElement?.fromJsonOrNull(
//    deserializer: DeserializationStrategy<T>,
//    json: Json = defaultJson,
//): T? = runCatching {
//    if (this == null) return@runCatching null
//    json.decodeFromJsonElement(deserializer, this)
//}.onFailure { e ->
//    Timber.e("Failed to parse fromJsonElementOrNull: %s", e.message)
//}.getOrNull()
//
///**
// * Parses a JSON string to a [List] of [T]; an empty list is returned on failure.
// */
//inline fun <reified T> String?.fromListJsonOrEmpty(json: Json = defaultJson): List<T> = runCatching {
//    if (this.isNullOrEmpty()) return@runCatching emptyList<T>()
//    json.decodeFromString<List<T>>(this)
//}.onFailure { e ->
//    Timber.e("Failed to parse from List JSON: %s", e.message)
//}.getOrNull().orEmpty()
//
///**
// * Parses a [Map] back to an object of type [T], or null on failure.
// */
//inline fun <reified T> Map<String, Any?>.parseToObject(json: Json = defaultJson): T? = runCatching {
//    json.decodeFromJsonElement<T>(toJsonElement() as JsonObject)
//}.onFailure { e ->
//    Timber.e("Failed to parse from Map: %s", e.message)
//}.getOrNull()
//
///**
// * Parses a [Map] back to an object of type [T] using an explicit [DeserializationStrategy],
// * or null on failure.
// */
//fun <T> Map<String, Any?>.parseToObject(
//    deserializer: DeserializationStrategy<T>,
//    json: Json = defaultJson,
//): T? = runCatching {
//    val element = toJsonElement() as JsonObject
//    json.decodeFromJsonElement(deserializer, element)
//}.onFailure { e ->
//    Timber.e("Failed to parse from Map: %s", e.message)
//}.getOrNull()
//
///**
// * Parses a [List] of [Map] back to a [List] of [T]; an empty list is returned on failure.
// */
//inline fun <reified T> List<Map<String, Any?>>.parseToListOfObject(json: Json = defaultJson): List<T> = runCatching {
//    json.decodeFromJsonElement<List<T>>(toJsonElement() as JsonArray)
//}.onFailure { e ->
//    Timber.e("Failed to parse from List Map: %s", e.message)
//}.getOrNull().orEmpty()
//
////endregion