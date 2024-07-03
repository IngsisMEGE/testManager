package testManager.controller

import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import testManager.repository.TestEnvRepository
import testManager.repository.TestInputRepository
import testManager.repository.TestOutputRepository
import testManager.repository.TestRepository
import testManager.service.interfaces.TestManagerService

@SpringBootTest
@AutoConfigureMockMvc
class TestManagerControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var testManagerService: TestManagerService

    @Autowired
    lateinit var testRepository: TestRepository

    @Autowired
    lateinit var testEnvRepository: TestEnvRepository

    @Autowired
    lateinit var testInputRepository: TestInputRepository

    @Autowired
    lateinit var testOutputRepository: TestOutputRepository

    @MockBean
    private lateinit var securityFilterChain: SecurityFilterChain

    @MockBean
    private lateinit var jwtDecoder: JwtDecoder

    private val testJwt = "test"

    @BeforeEach
    fun logIn() {
        val jwt =
            Jwt.withTokenValue(testJwt)
                .header("alg", "RS256")
                .claim("email", "test@test.com")
                .build()
        `when`(jwtDecoder.decode(testJwt)).thenReturn(jwt)

        val authorities = listOf(SimpleGrantedAuthority("SCOPE_write:snippets"))

        val authentication =
            TestingAuthenticationToken(
                jwt,
                "testPassword",
                authorities,
            )

        SecurityContextHolder.getContext().authentication = authentication

        `when`(securityFilterChain.matches(any(HttpServletRequest::class.java)))
            .thenReturn(true)
    }

    @Test
    fun test001_saveTestWithMissingParametersShouldReturn400() {
        mockMvc.perform(
            post("/testManager/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "snippetId": 1,
                        "name": "Test",
                        "inputs": ["Test"],
                        "outputs": ["Test"],
                        "authorEmail": "test@test.com"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun test002_saveTestWithNotAuthorizedUserShouldReturn403() {
        mockMvc.perform(
            post("/testManager/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "snippetId": 1,
                        "name": "Test",
                        "inputs": ["Test"],
                        "outputs": ["Test"],
                        "envs": "Test=3",
                        "authorEmail": "test@invalid.com"
                    }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun test003_saveTestWithInvalidInputShouldReturn400() {
        mockMvc.perform(
            post("/testManager/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "snippetId": 1,
                        "name": "Test",
                        "inputs": [],
                        "outputs": [],
                        "envs": "Test=3",
                        "authorEmail": ""
                        }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun test004_saveTestWithInvalidEnvsFormatShouldReturn400() {
        mockMvc.perform(
            post("/testManager/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "snippetId": 1,
                        "name": "Test",
                        "inputs": ["test"],
                        "outputs": ["test"],
                        "envs": "Test=3;test4",
                        "authorEmail": "mail@mail.com"
                        }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun test005_saveTestWithValidInputShouldReturn200() {
        mockMvc.perform(
            post("/testManager/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "snippetId": 1,
                        "name": "Test",
                        "inputs": ["test"],
                        "outputs": ["test"],
                        "envs": "Test=3",
                        "authorEmail": "test@test.com"
                        }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
    }

    @Test
    fun test006_editTestWithNonExistentTestShouldReturn404() {
        mockMvc.perform(
            post("/testManager/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "testId": 1,
                        "snippetId": 1,
                        "name": "Test",
                        "inputs": ["test"],
                        "outputs": ["test"],
                        "envs": "Test=3",
                        "authorEmail": "test@test.com"
                        }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun test007_editTestWithValidInputShouldReturn200() {
        mockMvc.perform(
            post("/testManager/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "snippetId": 1,
                        "name": "Test",
                        "inputs": ["test"],
                        "outputs": ["test"],
                        "envs": "Test=3",
                        "authorEmail": "test@test.com"
                        }
                    """.trimIndent(),
                ),
        )

        logIn()
        mockMvc.perform(
            post("/testManager/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "testId": 1,
                        "snippetId": 1,
                        "name": "Test2",
                        "inputs": ["test2"],
                        "outputs": ["test2"],
                        "envs": "Test2=3",
                        "authorEmail": "test@test.com"
                        }
                    """.trimIndent(),
                ),
        )
            .andExpect(status().isOk)
    }

    @Test
    fun test008_deleteTestShouldReturn200() {
        mockMvc.perform(
            delete("/testManager/delete/1")
                .contentType(MediaType.APPLICATION_JSON)
                .param("authorEmail", "test@test.com"),
        )
            .andExpect(status().isOk)
    }

    @Test
    fun test009_deleteTestWithInvalidAuthorEmailShouldReturn403() {
        mockMvc.perform(
            delete("/testManager/delete/1")
                .contentType(MediaType.APPLICATION_JSON)
                .param("authorEmail", "invalid"),
        )
            .andExpect(status().isForbidden)
    }

    @Test
    fun test010_getTestsShouldReturn200EmptyList() {
        val tests =
            mockMvc.perform(
                get("/testManager/get/99")
                    .contentType(MediaType.APPLICATION_JSON),
            )
                .andExpect(status().isOk)
                .andReturn()

        assert(tests.response.contentAsString == "[]")
    }

    @Test
    fun test011_getTestsShouldReturn200() {
        mockMvc.perform(
            post("/testManager/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "snippetId": 1,
                        "name": "Test",
                        "inputs": ["test"],
                        "outputs": ["test"],
                        "envs": "Test=3",
                        "authorEmail": "test@test.com"
                        }
                    """.trimIndent(),
                ),
        )

        logIn()
        val tests =
            mockMvc.perform(
                get("/testManager/get/1")
                    .contentType(MediaType.APPLICATION_JSON),
            )
                .andExpect(status().isOk)
                .andReturn()

        assert(tests.response.contentAsString != "[]")
    }
}
