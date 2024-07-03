package testManager.service.implementations

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import testManager.controller.payload.request.TestInputDTO
import testManager.controller.payload.response.TestEnvDto
import testManager.controller.payload.response.TestOutputDTO
import testManager.entity.Test
import testManager.entity.TestEnv
import testManager.entity.TestInput
import testManager.entity.TestOutput
import testManager.repository.TestEnvRepository
import testManager.repository.TestInputRepository
import testManager.repository.TestOutputRepository
import testManager.repository.TestRepository
import testManager.service.interfaces.TestManagerService

@Service
class TestManagerServiceImpl(
    val testRepository: TestRepository,
    val testEnvRepository: TestEnvRepository,
    val testInputRepository: TestInputRepository,
    val testOutputRepository: TestOutputRepository,
) : TestManagerService {
    @Transactional
    override fun saveTest(test: TestInputDTO): TestOutputDTO {
        return if (test.testId != null) {
            updateTest(test)
        } else {
            createTest(test)
        }
    }

    @Transactional
    override fun deleteTest(id: Long) {
        testRepository.deleteById(id)
    }

    override fun getTests(snippetId: Long): List<TestOutputDTO> {
        val tests = testRepository.findAllBySnippetId(snippetId)
        return tests.map { test ->
            constructTestOutputDTO(test, test.testInputs, test.testOutputs, test.testEnvs)
        }
    }

    private fun createTest(test: TestInputDTO): TestOutputDTO {
        val savedTest = testRepository.save(Test(snippetId = test.snippetId, name = test.name))
        val savedInputs = saveInputs(test.inputs, savedTest)
        val savedOutputs = saveOutputs(test.outputs, savedTest)
        val savedEnvs = saveEnvs(test.envs, savedTest)

        return constructTestOutputDTO(savedTest, savedInputs, savedOutputs, savedEnvs)
    }

    private fun updateTest(test: TestInputDTO): TestOutputDTO {
        val oldTest =
            testRepository.findById(
                test.testId!!,
            ).orElseThrow { NoSuchElementException("Test no encontrado con el id ${test.testId}") }
        oldTest.name = test.name
        val savedTest = testRepository.save(oldTest)

        testInputRepository.deleteAllByTestId(savedTest.id)
        testOutputRepository.deleteAllByTestId(savedTest.id)
        testEnvRepository.deleteAllByTestId(savedTest.id)
        val savedInputs = saveInputs(test.inputs, savedTest)
        val savedOutputs = saveOutputs(test.outputs, savedTest)
        val savedEnvs = saveEnvs(test.envs, savedTest)

        return constructTestOutputDTO(savedTest, savedInputs, savedOutputs, savedEnvs)
    }

    private fun saveInputs(
        inputs: List<String>,
        test: Test,
    ): List<TestInput> = testInputRepository.saveAll(inputs.map { TestInput(test = test, input = it) })

    private fun saveOutputs(
        outputs: List<String>,
        test: Test,
    ): List<TestOutput> = testOutputRepository.saveAll(outputs.map { TestOutput(test = test, output = it) })

    private fun saveEnvs(
        envs: String,
        test: Test,
    ): List<TestEnv> =
        testEnvRepository.saveAll(
            envs.split(";").map { env ->
                val (key, value) = env.split("=")
                TestEnv(test = test, key = key, value = value)
            },
        )

    private fun constructTestOutputDTO(
        test: Test,
        inputs: List<TestInput>,
        outputs: List<TestOutput>,
        envs: List<TestEnv>,
    ): TestOutputDTO =
        TestOutputDTO(
            id = test.id,
            snippetId = test.snippetId,
            name = test.name,
            inputs = inputs.map { it.input },
            outputs = outputs.map { it.output },
            envs = envs.map { TestEnvDto(it.key, it.value) },
        )
}
