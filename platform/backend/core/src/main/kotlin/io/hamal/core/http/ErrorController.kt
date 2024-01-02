package io.hamal.core.http

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
        val cause = t.cause
        if (cause is ConversionFailedException) {
            res.status = 400
            res.addHeader("Content-Type", "application/json")
//            res.writer.write(
//                json.encodeToString(
//                    InvalidArgumentType(
//                        message = "ArgumentTypeMismatch",
//                        source = cause.sourceType?.toString() ?: "Unknown source type",
//                        target = cause.targetType.toString()
//                    )
//                ))
            TODO()
        } else {
            res.addHeader("Content-Type", "application/json")
//            res.writer.write(json.encodeToString(ApiError("Bad request")))
            TODO()
        }
    }


    data class MissingFieldsError(
        val message: String,
        val fields: List<String>
    )

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun missingFields(res: HttpServletResponse, t: HttpMessageNotReadableException) {
        val cause = t.cause
//        if (cause is MissingFieldException) {
//            res.status = 400
//            res.addHeader("Content-Type", "application/json")
//            res.writer.write(
//                json.encodeToString(
//                    MissingFieldsError(
//                        message = "Fields are missing",
//                        fields = cause.missingFields
//                    )
//                )
//            )
//        } else {
        res.status = 400
        res.addHeader("Content-Type", "application/json")
//            res.writer.write(json.encodeToString(ApiError("Bad request")))
        TODO()
//        }
    }

    @ExceptionHandler(value = [NoHandlerFoundException::class])
    fun missingFields(res: HttpServletResponse) {
        res.status = SC_NOT_FOUND
        res.addHeader("Content-Type", "application/json")
//        res.writer.write(json.encodeToString(ApiError("Request handler not found")))
        TODO()
    }


    @ExceptionHandler(value = [IllegalCallerException::class])
    fun otherwise(res: HttpServletResponse) {
        res.status = SC_FORBIDDEN
        res.addHeader("Content-Type", "application/json")
//        res.writer.write(json.encodeToString(ApiError.serializer(), ApiError("FORBIDDEN")))
        TODO()
    }

    @ExceptionHandler(value = [Throwable::class])
    fun otherwise(res: HttpServletResponse, t: Throwable) {
        val toHandle = when (t) {
            is ServletException -> t.cause
            else -> t
        }

        val statusCode = when (toHandle) {
            is IllegalArgumentException, is MethodArgumentTypeMismatchException -> SC_BAD_REQUEST
            is NoSuchElementException -> SC_NOT_FOUND
            is IllegalCallerException -> SC_FORBIDDEN
            else -> SC_INTERNAL_SERVER_ERROR
        }

        res.status = statusCode
        res.addHeader("Content-Type", "application/json")
//        res.writer.write(json.encodeToString(ApiError.serializer(), ApiError(toHandle?.message ?: "Unknown error")))
        TODO()
    }

}