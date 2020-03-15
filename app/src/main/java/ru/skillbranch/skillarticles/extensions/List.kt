package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int,Int>>.groupByBounds(bounds:List<Pair<Int, Int>>):MutableList<List<Pair<Int,Int>>>{
return mutableListOf(emptyList())
}