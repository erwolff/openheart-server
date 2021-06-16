package art.openhe.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.lang.Exception


/**
 * Wrapper class for Jackson ObjectMapper
 * Configures the ObjectMapper specifically for this project
 */
object Mapper {

    private val mapper: ObjectMapper = ObjectMapper()
        .registerModule(Jdk8Module())
        .registerModule(KotlinModule())
        .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

    fun toJson(obj: Any): JsonNode = mapper.convertValue(obj, JsonNode::class.java)

    fun toJsonString(obj: Any) = toJson(obj).toString()

    fun <T> fromString(json: String, toClass: Class<T>): T? =
        try {
            mapper.readValue(json, toClass)
        } catch (e: Exception) {
            logError(e) { "Unable to convert string: $json to class of type: ${toClass.simpleName}" }
            null
        }

}