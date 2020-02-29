package ru.skillbranch.skillarticles.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PrefManager(context: Context) {
    private val preferences:SharedPreferences = context.getSharedPreferences("skill_articles", Context.MODE_PRIVATE)
    fun clearAll(){
        preferences.all.keys.forEach{
            preferences.edit().remove(it).apply()
        }
    }
}