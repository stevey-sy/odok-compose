package com.sy.core.ui

import android.content.Context
import android.widget.Toast

/** Toast를 짧은 시간으로 띄우는 간단 확장함수 */
fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}