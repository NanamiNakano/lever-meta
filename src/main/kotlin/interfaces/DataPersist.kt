package dev.thynanami.interfaces

interface DataPersist<T> {
    fun load():T
    fun save(data:T)
}
