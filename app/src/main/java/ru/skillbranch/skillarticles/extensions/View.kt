package ru.skillbranch.skillarticles.extensions

import android.content.Context
import android.view.View
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import ru.skillbranch.skillarticles.extensions.dpToIntPx

fun View.setMarginOptionally(left:Int = marginLeft, top:Int = marginTop, right: Int = marginRight, bottom:Int = marginBottom){
    val lp = layoutParams

//    this. = context.dpToIntPx(left).toFloat()
}