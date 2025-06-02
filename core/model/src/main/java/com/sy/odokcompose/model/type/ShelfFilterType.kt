package com.sy.odokcompose.model.type

enum class ShelfFilterType(val code: Int) {
    NONE(0),
    FINISHED(1),
    READING(2);

    companion object {
        fun fromCode(code: Int): ShelfFilterType {
            return values().find { it.code == code } ?: NONE
        }
    }
}