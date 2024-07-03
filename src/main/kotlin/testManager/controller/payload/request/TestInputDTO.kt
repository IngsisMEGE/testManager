package testManager.controller.payload.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

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
    val envs: String,
    @field:NotBlank(message = "El email del autor no puede estar vacío")
    val authorEmail: String,
)
