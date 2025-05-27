package com.sy.odokcompose.core.database.entity.mapper

import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.model.SearchBookUiModel

object SearchBookEntityMapper : EntityMapper<List<BookEntity>, List<SearchBookUiModel>> {
    override fun entityToModel(entity: List<BookEntity>): List<SearchBookUiModel> {
        return entity.map { bookEntity ->
            SearchBookUiModel(
                title = bookEntity.title,
                author = bookEntity.author,
                publisher = bookEntity.publisher, 
                isbn = bookEntity.isbn,
                cover = bookEntity.coverImageUrl,
                page = bookEntity.totalPageCnt,
                description = bookEntity.description,
                rate = bookEntity.rate
            )
        }
    }

    override fun modelToEntity(model: List<SearchBookUiModel>): List<BookEntity> {
        return model.map { uiModel ->
            BookEntity(
                title = uiModel.title,
                author = uiModel.author,
                publisher = uiModel.publisher,
                isbn = uiModel.isbn,
                coverImageUrl = uiModel.cover,
                totalPageCnt = uiModel.page,
                description = uiModel.description,
                rate = uiModel.rate
            )
        }
    }
}