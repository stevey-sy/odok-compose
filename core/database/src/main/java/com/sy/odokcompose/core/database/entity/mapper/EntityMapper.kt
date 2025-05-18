package com.sy.odokcompose.core.database.entity.mapper

/**
 * Entity와 UI모델 간 변환을 위한 매퍼 인터페이스
 */
interface EntityMapper<Entity, Model> {
    fun mapFromEntity(entity: Entity): Model
    fun mapToEntity(model: Model): Entity
}