package com.longkd.base_android.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LifecycleOwner

/**
 * @Author: longkd
 * @Since: 10:31 - 12/08/2023
 */
class NavigatorManager private constructor(
    private val fragmentManager: FragmentManager
) {
    companion object {
        fun of(fragmentManager: FragmentManager): NavigatorManager {
            return NavigatorManager(fragmentManager)
        }
    }

    private var idHolder: Int? = null

    fun withPlaceHolder(idHolder: Int): NavigatorManager {
        this.idHolder = idHolder
        return this
    }

    fun addBundle(key: String, bundle: Bundle): NavigatorManager {
        fragmentManager.setFragmentResult(key, bundle)
        return this
    }

    fun getBundle(
        key: String,
        lifecycleOwner: LifecycleOwner,
        listener: FragmentResultListener
    ): NavigatorManager {
        fragmentManager.setFragmentResultListener(key, lifecycleOwner, listener)
        return this
    }

    fun navigate(fClass: Class<out Fragment>, block: ((FragmentTransaction) -> Unit)? = null): Int {
        return idHolder?.let { holder ->
            fragmentManager
                .beginTransaction()
                .replace(holder, fClass.newInstance(), fClass.simpleName)
                .apply { block?.invoke(this) }
                .addToBackStack(null)
                .commit()
        } ?: -1
    }

    fun navigateOff(
        fClass: Class<out Fragment>,
        block: ((FragmentTransaction) -> Unit)? = null
    ): Int {
        return idHolder?.let { holder ->
            fragmentManager
                .beginTransaction()
                .replace(holder, fClass.newInstance(), fClass.simpleName)
                .apply { block?.invoke(this) }
                .commit()
        } ?: -1
    }

    fun navigateOffAll(
        fClass: Class<out Fragment>,
        block: ((FragmentTransaction) -> Unit)? = null
    ): Int {
        return idHolder?.let { holder ->
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            fragmentManager
                .beginTransaction()
                .replace(holder, fClass.newInstance(), fClass.simpleName)
                .apply { block?.invoke(this) }
                .addToBackStack(null)
                .commit()
        } ?: -1
    }

    fun add(fClass: Class<out Fragment>, block: ((FragmentTransaction) -> Unit)? = null): Int {
        return idHolder?.let { holder ->
            fragmentManager
                .beginTransaction()
                .add(holder, fClass.newInstance(), fClass.simpleName)
                .apply { block?.invoke(this) }
                .commit()
        } ?: -1
    }

    fun back() {
        fragmentManager.popBackStack()
    }

    fun commit(block: FragmentTransaction.() -> Unit): Int {
        return fragmentManager
            .beginTransaction()
            .apply { block() }
            .commit()
    }
}
