package io.github.curo.data

sealed interface ShareScreenData {
    object Loading : ShareScreenData
    object Error : ShareScreenData
    object Hidden : ShareScreenData

    @JvmInline
    value class Url(val url: String) : ShareScreenData
}