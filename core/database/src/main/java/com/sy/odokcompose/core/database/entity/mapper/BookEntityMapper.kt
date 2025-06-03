package com.sy.odokcompose.core.database.entity.mapper

import com.sy.odokcompose.core.database.entity.BookEntity
import com.sy.odokcompose.model.BookUiModel

object BookEntityMapper : EntityMapper<BookEntity, BookUiModel> {
    override fun entityToModel(entity: BookEntity): BookUiModel {
        return BookUiModel(
            itemId = entity.itemId,
            title = entity.title,
            author = entity.author,
            publisher = entity.publisher,
            isbn = entity.isbn,
            coverImageUrl = entity.coverImageUrl,
            bookType = entity.bookType,
            totalPageCnt = entity.totalPageCnt,
            currentPageCnt = entity.currentPageCnt,
            challengePageCnt = entity.challengePageCnt,
            startDate = entity.startDate,
            endDate = entity.endDate,
            elapsedTimeInSeconds = entity.elapsedTimeInSeconds,
            completedReadingCnt = entity.completedReadingCnt,
            finishedReadCnt = entity.finishedReadCnt,
            shelfPosition = -1
        )
    }

    fun toUiModels(entities: List<BookEntity>): List<BookUiModel> {
        return entities.map { entityToModel(it) }
    }

    override fun modelToEntity(model: BookUiModel): BookEntity {
        return BookEntity(
            itemId = model.itemId,
            title = model.title,
            author = model.author,
            publisher = model.publisher,
            isbn = model.isbn,
            coverImageUrl = model.coverImageUrl,
            bookType = model.bookType,
            totalPageCnt = model.totalPageCnt,
            currentPageCnt = model.currentPageCnt,
            challengePageCnt = model.challengePageCnt,
            startDate = model.startDate,
            endDate = model.endDate,
            elapsedTimeInSeconds = model.elapsedTimeInSeconds,
            completedReadingCnt = model.completedReadingCnt,
            finishedReadCnt = model.finishedReadCnt,
        )
    }
}