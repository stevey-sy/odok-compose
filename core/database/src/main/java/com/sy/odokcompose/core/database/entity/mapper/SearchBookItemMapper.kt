package com.sy.odokcompose.core.database.entity.mapper

import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.model.SearchBookUiModel

object SearchBookItemMapper : EntityMapper<BookEntity, SearchBookUiModel> {
    override fun entityToModel(entity: BookEntity): SearchBookUiModel {
        return SearchBookUiModel(
            title = entity.title,
            author = entity.author,
            publisher = entity.publisher,
            isbn = entity.isbn,
            cover = entity.coverImageUrl,
            page = entity.totalPageCnt,
            description = entity.description,
            rate = entity.rate
        )
    }

    override fun modelToEntity(model: SearchBookUiModel): BookEntity {
        return BookEntity(
            title = model.title,
            author = model.author,
            publisher = model.publisher,
            isbn = model.isbn,
            coverImageUrl = model.cover,
            totalPageCnt = model.page,
            description = model.description,
            rate = model.rate,
        )
    }
} 