package io.hamal.lib.web3.eth.domain

data class EthError(
    val code: ErrorCode,
    val message: String
) {
    override fun toString(): String {
        return "EthError($code,'$message')"
    }

    enum class ErrorCode(
        val code: Int
    ) {
        ExecutionReverted(-32000),
        ParseError(-32700),
        InvalidRequest(-32600),
        MethodNotFound(-32601),
        InvalidParams(-32602),
        InternalError(-32603);

        companion object {
            fun fromCode(code: Int): ErrorCode {
                return values().find { ec -> ec.code == code }
                    ?: throw NoSuchElementException("Error code not found")

            }
        }
    }
}
