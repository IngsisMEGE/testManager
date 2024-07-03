package testManager.controller

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import testManager.controller.payload.request.TestInputDTO
import testManager.controller.payload.response.TestOutputDTO
import testManager.exception.AccessDeniedException
import testManager.service.interfaces.TestManagerService

@RestController
@RequestMapping("/testManager")
class TestManagerController(val testManagerService: TestManagerService) {
    @PostMapping("/save")
    fun saveTest(
        @Valid @RequestBody test: TestInputDTO,
        @AuthenticationPrincipal userData: Jwt,
    ): ResponseEntity<TestOutputDTO> {
        if (userData.claims["email"] != test.authorEmail) throw AccessDeniedException("No tienes permisos para realizar esta acción")
        return ResponseEntity.ok(testManagerService.saveTest(test))
    }
}
