package com.sy.odokcompose.core.supabase.auth.model

/**
 * 인증 결과를 나타내는 sealed class
 */
sealed class AuthResult<out T> {
    
    /**
     * 성공
     * @param data 성공 시 반환되는 데이터
     */
    data class Success<T>(val data: T) : AuthResult<T>()
    
    /**
     * 실패
     * @param exception 발생한 예외
     * @param message 에러 메시지
     */
    data class Error(
        val exception: Throwable,
        val message: String = exception.message ?: "알 수 없는 오류가 발생했습니다"
    ) : AuthResult<Nothing>()
    
    /**
     * 로딩 중
     */
    object Loading : AuthResult<Nothing>()
    
    /**
     * 성공인지 확인
     */
    val isSuccess: Boolean
        get() = this is Success
    
    /**
     * 실패인지 확인
     */
    val isError: Boolean
        get() = this is Error
    
    /**
     * 로딩 중인지 확인
     */
    val isLoading: Boolean
        get() = this is Loading
    
    /**
     * 성공 시 데이터 반환, 실패 시 null
     */
    fun getOrNull(): T? {
        return if (this is Success) data else null
    }
    
    /**
     * 실패 시 예외 반환, 성공 시 null
     */
    fun exceptionOrNull(): Throwable? {
        return if (this is Error) exception else null
    }
}