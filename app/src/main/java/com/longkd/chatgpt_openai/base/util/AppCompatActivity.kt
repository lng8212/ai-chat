package com.longkd.chatgpt_openai.base.util

import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.longkd.chatgpt_openai.R


fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    tag: String,
    @IdRes frameId: Int,
    addToBackStack: Boolean = true,
    enableAnimation: Boolean = true
) {
    if (addToBackStack) {
        if (enableAnimation) {
            supportFragmentManager.transactWithAnimation({
                replace(frameId, fragment, tag)
            }, tag)
        } else {
            supportFragmentManager.transact({
                replace(frameId, fragment, tag)
            }, tag)
        }
    } else {
        if (enableAnimation) {
            supportFragmentManager.transactWithAnimation {
                replace(frameId, fragment, tag)
            }
        } else {
            supportFragmentManager.transact {
                replace(frameId, fragment, tag)
            }
        }
    }
}

fun AppCompatActivity.replaceFm(@IdRes frameId: Int, fragment: Fragment) {
    val backStateName = fragment.javaClass.name

    val manager = supportFragmentManager
    val fragmentPopped = manager.popBackStackImmediate(backStateName, 0)

    if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) { //fragment not in back stack, create it.
        val ft = manager.beginTransaction()
        ft.replace(frameId, fragment, backStateName)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.addToBackStack(backStateName)
        ft.commitAllowingStateLoss()
    }
}

fun AppCompatActivity.addFragmentWithNoAnimation(
    fromFragment: Fragment? = null,
    fragment: Fragment,
    tag: String, @IdRes frameId: Int
) {
    supportFragmentManager.transact({
        add(frameId, fragment, tag)
        fromFragment?.let { hide(it) }
    }, tag)
}

fun AppCompatActivity.addFragmentWithAnimation(
    fromFragment: Fragment? = null,
    fragment: Fragment,
    tag: String, @IdRes frameId: Int,
    addToBackStack: Boolean = true

) {
    supportFragmentManager.transactWithAnimation({
        add(frameId, fragment, tag)
        fromFragment?.let { hide(it) }
    }, tag, addToBackStack)
}

fun AppCompatActivity.addFragmentWithAnimationZoom(
    fromFragment: Fragment? = null,
    fragment: Fragment,
    tag: String, @IdRes frameId: Int,
    addToBackStack: Boolean = true
) {
    supportFragmentManager.transactWithAnimationZoom({
        add(frameId, fragment, tag)
        fromFragment?.let { hide(it) }
    }, tag, addToBackStack)
}

fun AppCompatActivity.replaceFragmentWithAnimation(
    fromFragment: Fragment? = null,
    fragment: Fragment,
    tag: String, @IdRes frameId: Int
) {
    supportFragmentManager.transactWithAnimationZoom({
        replace(frameId, fragment, tag)
        fromFragment?.let { hide(it) }
    }, tag)
}

private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commitAllowingStateLoss()
}

private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit, name: String) {
    beginTransaction().apply {
        action()
    }.addToBackStack(name).commitAllowingStateLoss()
}

private inline fun FragmentManager.transactWithAnimation(action: FragmentTransaction.() -> Unit) {
    beginTransaction().setCustomAnimations(
        R.anim.enter_from_right, R.anim.exit_to_left,
        R.anim.enter_from_left, R.anim.exit_to_right
    ).apply {
        action()
    }.commitAllowingStateLoss()
}

private inline fun FragmentManager.transactWithAnimationZoom(
    action: FragmentTransaction.() -> Unit,
    name: String,
    addToBackStack: Boolean = true
) {
    val trc = beginTransaction().setCustomAnimations(
        R.anim.enter_zoom_out, R.anim.exit_zoom_in,
        R.anim.enter_zoom_out1, R.anim.exit_zoom_in1
    ).apply {
        action()
    }
    if (addToBackStack)
        trc.addToBackStack(name)
    trc.commitAllowingStateLoss()
}

private inline fun FragmentManager.transactWithAnimation(
    action: FragmentTransaction.() -> Unit,
    name: String,
    addToBackStack: Boolean = true
) {
    val trc = beginTransaction().setCustomAnimations(
        R.anim.enter_from_right, R.anim.exit_to_left,
        R.anim.enter_from_left, R.anim.exit_to_right
    ).apply {
        action()
    }
    if (addToBackStack)
        trc.addToBackStack(name)
    trc.commitAllowingStateLoss()
}

private inline fun FragmentManager.transactWithAnimation(
    action: FragmentTransaction.() -> Unit,
    name: String
) {
    beginTransaction().setCustomAnimations(
        R.anim.enter_from_right, R.anim.exit_to_left,
        R.anim.enter_from_left, R.anim.exit_to_right
    ).apply {
        action()
    }.addToBackStack(name).commitAllowingStateLoss()
}

inline fun FragmentManager.transactWithAnimation(
    action: FragmentTransaction.() -> Unit, name: String,
    @AnimatorRes @AnimRes enter: Int,
    @AnimatorRes @AnimRes exit: Int,
    @AnimatorRes @AnimRes popEnter: Int,
    @AnimatorRes @AnimRes popExit: Int
) {
    beginTransaction().setCustomAnimations(enter, exit, popEnter, popExit).apply {
        action()
    }.addToBackStack(name).commitAllowingStateLoss()
}

fun AppCompatActivity.clearAllBackStackFragment() {
    if (supportFragmentManager.backStackEntryCount > 0) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
}

fun AppCompatActivity.clearAllBackStackFragmentImmediately() {
    if (supportFragmentManager.backStackEntryCount > 0) {
        for (i in 0 until supportFragmentManager.backStackEntryCount)
            supportFragmentManager.popBackStack()
    }

}
