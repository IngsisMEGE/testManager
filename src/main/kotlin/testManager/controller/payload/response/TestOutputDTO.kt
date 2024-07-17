package testManager.controller.payload.response

data class TestOutputDTO(
    val id: Long,
    val snippetId: Long,
    val name: String,
    val inputs: List<String>,
    val outputs: List<String>,
    val envs: String,
)
