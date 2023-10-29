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
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@Component
@ControllerAdvice
internal class ErrorController(
    private val json: Json
) {

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
            TODO()
        }
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
            else -> HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }

        val encoded = json.encodeToString(ApiError.serializer(), ApiError(toHandle?.message ?: "Unknown error"))
        res.status = statusCode
        res.addHeader("Content-Type", "application/json")
        res.writer.write(encoded)
    }


}