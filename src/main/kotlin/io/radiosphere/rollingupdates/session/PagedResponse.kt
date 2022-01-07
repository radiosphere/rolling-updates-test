package io.radiosphere.rollingupdates.session


data class PagedResponse<T> ( val total: Int, val items: List<T>)