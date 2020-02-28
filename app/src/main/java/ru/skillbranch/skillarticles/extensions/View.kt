package ru.skillbranch.skillarticles.extensions

import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

fun View.setMarginOptionally(left:Int = marginLeft, top:Int = marginTop, right:Int = marginRight, bottom:Int = marginBottom){
    (layoutParams as ViewGroup.MarginLayoutParams).apply {
//        setMargins(
//            context.dpToIntPx(left),
//            context.dpToIntPx(top),
//            context.dpToIntPx(right),
//            context.dpToIntPx(bottom)
//        )
        setMargins(
            left,
            top,
            right,
            bottom
        )
    }
}