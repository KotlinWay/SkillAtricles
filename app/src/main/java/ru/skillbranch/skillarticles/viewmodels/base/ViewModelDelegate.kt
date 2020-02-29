package ru.skillbranch.skillarticles.viewmodels.base

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ViewModelDelegate<T : ViewModel>(
    private val clazz: Class<T>,
    private val arg: Any?) :
    ReadOnlyProperty<FragmentActivity, T> {

    private var viewModel:T? = null
    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        if(viewModel == null){
            val vmFactory = ViewModelFactory("0")
            viewModel = ViewModelProviders.of(thisRef, vmFactory).get(clazz)
        }
        return viewModel as T
    }

}