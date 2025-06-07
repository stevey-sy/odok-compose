package com.sy.odokcompose.core.database.entity.mapper

import com.sy.odokcompose.core.database.entity.MemoEntity
import com.sy.odokcompose.model.MemoUiModel

object MemoEntityMapper : EntityMapper<MemoEntity, MemoUiModel> {
    override fun entityToModel(entity: MemoEntity, ): MemoUiModel {
        return MemoUiModel(
            memoId = entity.memoId,
            bookId = entity.bookId,
            content = entity.content,
            pageNumber = entity.pageNumber,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            backgroundId = entity.backgroundId,
            tags = emptyList()
        )
    }

    override fun modelToEntity(model: MemoUiModel): MemoEntity {
        return MemoEntity(
            memoId = model.memoId,
            bookId = model.bookId,
            content = model.content,
            pageNumber = model.pageNumber,
            createdAt = model.createdAt,
            updatedAt = model.updatedAt,
            backgroundId = model.backgroundId
        )
    }
}