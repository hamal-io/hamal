package io.hamal.lib.web3.evm.impl.arbitrum.domain


data class ArbitrumError(
    val code: Int,
    val message: String
) {
    constructor(code: ErrorCode, message: String) : this(code.value, message)

    override fun toString(): String {
        return "ArbitrumError($code,'$message')"
    }

    sealed class ErrorCode(val value: Int) {
        data object ExecutionReverted : ErrorCode(-32000)
        data object ParseError : ErrorCode(-32700)
        data object InvalidRequest : ErrorCode(-32600)
        data object MethodNotFound : ErrorCode(-32601)
        data object InvalidParams : ErrorCode(-32602)
        data object InternalError : ErrorCode(-32603)

        companion object {
            val values = listOf(
                ExecutionReverted,
                ParseError,
                InvalidRequest,
                MethodNotFound,
                InvalidParams,
                InternalError
            )

            fun fromCode(code: Int): ErrorCode {
                return values.find { ec -> ec.value == code } ?: throw NoSuchElementException("ErrorCode not found")
            }
        }
    }
}
