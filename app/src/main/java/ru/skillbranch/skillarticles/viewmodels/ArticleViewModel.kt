package ru.skillbranch.skillarticles.viewmodels

import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations.map
import ru.skillbranch.skillarticles.data.ArticleData
import ru.skillbranch.skillarticles.data.ArticlePersonalInfo
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.data.toArticlePersonalInfo
import ru.skillbranch.skillarticles.extensions.format
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.Notify


class ArticleViewModel(private val articleId: String) :
    BaseViewModel<ArticleState>(ArticleState()), IArticleViewModel {

    private val repository = ArticleRepository

    init {
        subscribeOnDataSource(getArticleData()) { article, state ->
            article ?: return@subscribeOnDataSource null
            state.copy(
                shareLink = article.shareLink,
                title = article.title,
                category = article.category,
                categoryIcon = article.categoryIcon,
                date = article.date.format(),
                author = article.author
            )
        }

        subscribeOnDataSource(getArticleContent()) { content, state ->
            content ?: return@subscribeOnDataSource null
            state.copy(
                isLoadingContent = false,
                content = content
            )
        }

        subscribeOnDataSource(getArticlePersonalInfo()) { info, state ->
            info ?: return@subscribeOnDataSource null
            state.copy(
                isBookmark = info.isBookmark,
                isLike = info.isLike
            )
        }

        subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
            Log.wtf("ArticleViewModel", "${settings.isDarkMode} ")
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )

        }
    }

    override fun getArticleContent(): LiveData<List<Any>?> {
        return repository.loadArticleContent(articleId)
    }

    override fun getArticleData(): LiveData<ArticleData?> {
        return repository.getArticle(articleId)
    }

    override fun getArticlePersonalInfo(): LiveData<ArticlePersonalInfo?> {
        return repository.loadArticlePersonalInfo(articleId)
    }

    override fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    override fun handleDownText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    override fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    override fun handleLike() {
        val isLiked = currentState.isLike
        val toggleLike = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isLike = !info.isLike))
        }

        toggleLike()

        val msg = if (currentState.isLike) Notify.TextMessage("Mark is liked")
        else {
            Notify.ActionMessage(
                "Don`t like it anymore",
                "No, still like it",
                toggleLike
            )
        }

        notify(msg)
    }

    override fun handleBookmark() {
        val toggleBookmark = {
            val info = currentState.toArticlePersonalInfo()
            repository.updateArticlePersonalInfo(info.copy(isBookmark = !info.isBookmark))
        }

        toggleBookmark()

        val msg = if (currentState.isBookmark) Notify.TextMessage("Add to bookmarks")
        else {
            Notify.ActionMessage(
                "Remove from bookmarks",
                "No, still in bookmark it",
                toggleBookmark
            )
        }

        notify(msg)

    }

    override fun handleShare() {
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }

    override fun handleToggleMenu() {
        updateState {
            it.copy(isShowMenu = !it.isShowMenu)
        }
    }

    fun handleSearchAction(isOpen: Boolean) {
        if (isOpen) {
            updateState {
                it.copy(isSearch = isOpen, searchQuery = null)
            }
        } else {
            updateState {
                it.copy(isSearch = isOpen)
            }
        }

    }

    override fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch, isShowMenu = false, searchPosition = 0) }
    }

    override fun handleSearch(query: String?) {
        Log.wtf("ArticleViewModel","handleSearch ")
        query ?: return
        val result = (currentState.content.firstOrNull() as? String).indexesOf(query)
            .map { it to it + query.length }
        updateState { it.copy(searchQuery = query, searchResults = result, searchPosition = 0) }
    }

    fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }

    fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

}

data class ArticleState(
    val isAuth: Boolean = false,
    val isLoadingContent: Boolean = true,
    val isLoadingReviews: Boolean = true,
    val isLike: Boolean = false,
    val isBookmark: Boolean = false,
    val isShowMenu: Boolean = false,
    val isBigText: Boolean = false,
    val isDarkMode: Boolean = false,
    val isSearch: Boolean = false,
    val searchQuery: String? = null,
    val searchResults: List<Pair<Int, Int>> = emptyList(),
    val searchPosition: Int = 0,
    val shareLink: String? = null,
    val title: String? = null,
    val category: String? = null,
    val categoryIcon: Any? = null,
    val date: String? = null,
    val author: Any? = null,
    val poster: String? = null,
    val content: List<Any> = emptyList(),
    val reviews: List<Any> = emptyList()
) : IViewModelState {
    override fun save(outState: Bundle) {
        outState.putAll(
            bundleOf(
                "isSearch" to isSearch,
                "searchQuery" to searchQuery,
                "searchResults" to searchResults,
                "searchPosition" to searchPosition
            )
        )
    }

    override fun restore(savedState: Bundle): IViewModelState {
        return copy(
         isSearch = savedState["isSearch"] as Boolean,
         searchQuery = savedState["searchQuery"] as? String,
         searchResults = savedState["searchResults"] as List<Pair<Int, Int>>,
         searchPosition = savedState["searchPosition"] as Int
        )
    }
}