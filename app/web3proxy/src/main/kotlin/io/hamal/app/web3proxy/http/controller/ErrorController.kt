package io.hamal.app.web3proxy.http.controller

import io.hamal.lib.web3.eth.json
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpServletResponse.*
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.core.annotation.Order
import org.springframework.core.convert.ConversionFailedException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException

@Order(HIGHEST_PRECEDENCE)
@ControllerAdvice
internal class ErrorController {

    data class InvalidArgumentType(
        val message: String,
        val source: String,
        val target: String
    )

    @ExceptionHandler(value = [MethodArgumentTypeMismatchException::class])
    fun argumentTypeMismatch(res: HttpServletResponse, t: MethodArgumentTypeMismatchException) {
        t.printStackTrace()

        val cause = t.cause
        if (cause is ConversionFailedException) {
            res.status = 400
            res.addHeader("Content-Type", "application/json;charset=UTF-8")
            res.writer.write(
                json.serialize(
                    InvalidArgumentType(
                        message = "ArgumentTypeMismatch",
                        source = cause.sourceType?.toString() ?: "Unknown source type",
                        target = cause.targetType.toString()
                    )
                )
            )
        } else {
            res.addHeader("Content-Type", "application/json;charset=UTF-8")
            res.writer.write(json.serialize(ApiError("Bad request")))
        }
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun missingFields(res: HttpServletResponse, t: HttpMessageNotReadableException) {
        t.printStackTrace()

        res.status = 400
        res.addHeader("Content-Type", "application/json;charset=UTF-8")
        res.writer.write(json.serialize(ApiError(t.cause?.message ?: "Bad request")))
    }

    @ExceptionHandler(value = [NoHandlerFoundException::class])
    fun missingFields(res: HttpServletResponse, t: NoHandlerFoundException) {
        t.printStackTrace()

        res.status = SC_NOT_FOUND
        res.addHeader("Content-Type", "application/json;charset=UTF-8")
        res.writer.write(json.serialize(ApiError("Request handler not found")))
    }


    @ExceptionHandler(value = [IllegalCallerException::class])
    fun illegalCaller(res: HttpServletResponse, t: Throwable) {
        t.printStackTrace()

        res.status = SC_FORBIDDEN
        res.addHeader("Content-Type", "application/json;charset=UTF-8")
        res.writer.write(json.serialize(ApiError("FORBIDDEN")))
    }

    @ExceptionHandler(value = [Throwable::class])
    fun otherwise(res: HttpServletResponse, t: Throwable) {
        t.printStackTrace()

        val toHandle = when (t) {
            is ServletException -> t.cause
            else -> t
        }

        val statusCode = when (toHandle) {
            is IllegalArgumentException, is MethodArgumentTypeMismatchException -> SC_BAD_REQUEST
            is NoSuchElementException -> SC_NOT_FOUND
            is IllegalAccessError -> SC_NOT_FOUND
            is IllegalCallerException -> SC_FORBIDDEN
            else -> SC_INTERNAL_SERVER_ERROR
        }

        res.status = statusCode
        res.addHeader("Content-Type", "application/json;charset=UTF-8")
        res.writer.write(json.serialize(ApiError(toHandle?.message ?: "Unknown error")))
    }

    data class ApiError(
        val message: String?
    )

    class ApiException(message: String?) : RuntimeException(message) {
        constructor(err: ApiError) : this(err.message)
    }
}