package com.longkd.base_android.ktx

import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding

/**
 * @Author: longkd
 * @Since: 10:30 - 12/08/2023
 */
class ViewBindingLazy<VB : ViewBinding> @JvmOverloads constructor(
    lifecycle: Lifecycle,
    private val initViewBinding: () -> VB
) : Lazy<VB> {
    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                cached = null
                super.onDestroy(owner)
            }
        })
    }

    private var cached: VB? = null
    override val value: VB
        get() {
            return cached ?: initViewBinding().also { cached = it }
        }

    override fun isInitialized(): Boolean = cached != null
}

inline fun <reified VB : ViewBinding> ComponentActivity.viewBindings(): Lazy<VB> {
    val inflateMethod = VB::class.java.getMethod("inflate", LayoutInflater::class.java)
    return ViewBindingLazy(lifecycle) {
        inflateMethod.invoke(null, layoutInflater) as VB
    }
}

inline fun <reified VB : ViewBinding> Fragment.viewBindings(): Lazy<VB> {
    val inflateMethod = VB::class.java.getMethod("inflate", LayoutInflater::class.java)
    return ViewBindingLazy(lifecycle) {
        inflateMethod.invoke(null, layoutInflater) as VB
    }
}