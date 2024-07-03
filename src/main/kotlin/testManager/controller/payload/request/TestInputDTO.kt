package testManager.controller.payload.request

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import kotlin.reflect.KClass

data class TestInputDTO(
    val testId: Long?,
    @field:NotNull(message = "El snippetId no puede ser nulo")
    val snippetId: Long,
    @field:NotBlank(message = "El nombre no puede estar vacío")
    val name: String,
    @field:NotEmpty(message = "Debe ingresar al menos un input")
    val inputs: List<@NotBlank String>,
    @field:NotEmpty(message = "Debe ingresar al menos un output esperado")
    val outputs: List<@NotBlank String>,
    @field:NotBlank(message = "Debe ingresar al menos una variable de entorno")
    @field:Pattern(regexp = "^(\\w+=\\d+)(;\\w+=\\d+)*$", message = "El formato de las variables de entorno no es válido")
    @field:ValidEnvs
    val envs: String,
    @field:NotBlank(message = "El email del autor no puede estar vacío")
    val authorEmail: String,
)

@Constraint(validatedBy = [EnvsUnique::class])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ValidEnvs(
    val message: String = "Las variables de entorno no pueden repetirse",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class EnvsUnique : ConstraintValidator<ValidEnvs, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        if (value == null) return false

        val envNames = value.split(";").map { it.substringBefore("=") }
        return envNames.size == envNames.toSet().size
    }
}
