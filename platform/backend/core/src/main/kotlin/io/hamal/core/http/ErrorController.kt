package io.hamal.core.http

import io.hamal.lib.sdk.api.ApiError
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
internal class ErrorController(
    private val json: Json
) {

    @Serializable
    data class InvalidArgumentType(
        val message: String,
        val source: String,
        val target: String
    )

    @OptIn(ExperimentalSerializationApi::class)
    @ExceptionHandler(value = [MethodArgumentTypeMismatchException::class])
    fun argumentTypeMismatch(
        req: HttpServletRequest,
        res: HttpServletResponse,
        t: MethodArgumentTypeMismatchException
    ) {
        t.printStackTrace()

        val cause = t.cause
        if (cause is ConversionFailedException) {

            val encoded = json.encodeToString(
                InvalidArgumentType(
                    message = "ArgumentTypeMismatch",
                    source = cause.sourceType.toString(),
                    target = cause.targetType.toString()
                )
            )
            res.status = 400
            res.addHeader("Content-Type", "application/json")
            res.writer.write(encoded)
        } else {
            val encoded = json.encodeToString(ApiError("Bad request"))
            res.status = 400
            res.addHeader("Content-Type", "application/json")
            res.writer.write(encoded)
        }
    }


    @Serializable
    data class MissingFieldsError(
        val message: String,
        val fields: List<String>
    )

    @OptIn(ExperimentalSerializationApi::class)
    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun missingFields(req: HttpServletRequest, res: HttpServletResponse, t: HttpMessageNotReadableException) {
        t.printStackTrace()

        val cause = t.cause
        if (cause is MissingFieldException) {

            val encoded = json.encodeToString(
                MissingFieldsError(
                    message = "Fields are missing",
                    fields = cause.missingFields
                )
            )
            res.status = 400
            res.addHeader("Content-Type", "application/json")
            res.writer.write(encoded)
        } else {
            val encoded = json.encodeToString(ApiError("Bad request"))
            res.status = 400
            res.addHeader("Content-Type", "application/json")
            res.writer.write(encoded)
        }
    }

    @ExceptionHandler(value = [NoHandlerFoundException::class])
    fun missingFields(req: HttpServletRequest, res: HttpServletResponse, t: NoHandlerFoundException) {
        t.printStackTrace()
        val encoded = json.encodeToString(ApiError("Request handler not found"))
        res.status = 404
        res.addHeader("Content-Type", "application/json")
        res.writer.write(encoded)
    }


    @ExceptionHandler(value = [IllegalCallerException::class])
    fun otherwise(req: HttpServletRequest, res: HttpServletResponse, t: IllegalCallerException) {
        val encoded = json.encodeToString(ApiError.serializer(), ApiError("FORBIDDEN"))
        res.status = HttpServletResponse.SC_FORBIDDEN
        res.addHeader("Content-Type", "application/json")
        res.writer.write(encoded)
    }


    @ExceptionHandler(value = [Throwable::class])
    fun otherwise(req: HttpServletRequest, res: HttpServletResponse, t: Throwable) {
        t.printStackTrace()

        val toHandle = when (t) {
            is ServletException -> t.cause
            else -> t
        }

        val statusCode = when (toHandle) {
            is IllegalArgumentException, is MethodArgumentTypeMismatchException -> HttpServletResponse.SC_BAD_REQUEST
            is NoSuchElementException -> HttpServletResponse.SC_NOT_FOUND
            is IllegalCallerException -> HttpServletResponse.SC_FORBIDDEN
            else -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }

        val encoded = json.encodeToString(ApiError.serializer(), ApiError(toHandle?.message ?: "Unknown error"))
        res.status = statusCode
        res.addHeader("Content-Type", "application/json")
        res.writer.write(encoded)
    }


}