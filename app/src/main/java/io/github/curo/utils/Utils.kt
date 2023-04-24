package io.github.curo.utils

fun <T> MutableCollection<T>.setAll(another: Iterable<T>) {
    this.clear()
    this.addAll(another)
}