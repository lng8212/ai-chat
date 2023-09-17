package com.longkd.base_android.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType
import java.util.Objects

/**
 * @Author: longkd
 * @Since: 23:04 - 11/08/2023
 */

internal object BindingReflex {

    fun <VB : ViewBinding> reflexViewBinding(
        aClass: Class<*>,
        layoutInflater: LayoutInflater?
    ): VB {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType)
                    .actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<Any>
                try {
                    tClass = actualTypeArguments[i] as Class<Any>
                } catch (e: Exception) {
                    continue
                }
                if (ViewBinding::class.java.isAssignableFrom(tClass)) {
                    val inflate = tClass.getMethod("inflate", LayoutInflater::class.java)
                    return inflate.invoke(null, layoutInflater) as VB
                }
            }
            return reflexViewBinding(aClass.superclass, layoutInflater)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        throw RuntimeException("$aClass not found")
    }

    fun <VB : ViewBinding> reflexViewBinding(
        aClass: Class<*>,
        from: LayoutInflater?,
        viewGroup: ViewGroup?,
        b: Boolean
    ): VB {
        try {
            val actualTypeArguments =
                (Objects.requireNonNull(aClass.genericSuperclass) as ParameterizedType).actualTypeArguments
            for (i in actualTypeArguments.indices) {
                val tClass: Class<Any>
                try {
                    tClass = actualTypeArguments[i] as Class<Any>
                } catch (e: Exception) {
                    continue
                }
                if (ViewBinding::class.java.isAssignableFrom(tClass)) {
                    val inflate = tClass.getDeclaredMethod(
                        "inflate",
                        LayoutInflater::class.java,
                        ViewGroup::class.java,
                        Boolean::class.javaPrimitiveType
                    )
                    return inflate.invoke(null, from, viewGroup, b) as VB
                }
            }
            return reflexViewBinding<ViewBinding>(aClass.superclass, from, viewGroup, b) as VB
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        throw RuntimeException("$aClass not found")
    }
}