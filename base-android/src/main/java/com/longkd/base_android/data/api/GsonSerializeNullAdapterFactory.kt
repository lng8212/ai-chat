package com.longkd.base_android.data.api

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.longkd.base_android.annotation.SerializeNull
import com.longkd.base_android.ktx.serializedName

/**
 * @Author: longkd
 * @Since: 12:51 - 13/08/2023
 */
class GsonSerializeNullAdapterFactory : TypeAdapterFactory {

    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {

        val declaredFields = type.rawType.declaredFields
        val nullableFieldNames = declaredFields
            .filter { it.declaredAnnotations.filterIsInstance<SerializeNull>().isNotEmpty() }
            .map { it.serializedName() }
        val nonNullableFieldNames =
            declaredFields.map { it.serializedName() } - nullableFieldNames.toSet()

        return if (nullableFieldNames.isEmpty()) {
            null
        } else object : TypeAdapter<T>() {
            private val delegateAdapter =
                gson.getDelegateAdapter(this@GsonSerializeNullAdapterFactory, type)
            private val elementAdapter = gson.getAdapter(JsonElement::class.java)

            override fun write(writer: JsonWriter, value: T?) {
                val jsonObject = delegateAdapter.toJsonTree(value).asJsonObject
                nonNullableFieldNames
                    .filter { jsonObject.get(it) is JsonNull }
                    .forEach { jsonObject.remove(it) }
                val originalSerializeNulls = writer.serializeNulls
                writer.serializeNulls = true
                elementAdapter.write(writer, jsonObject)
                writer.serializeNulls = originalSerializeNulls
            }

            override fun read(reader: JsonReader): T {
                return delegateAdapter.read(reader)
            }
        }
    }
}