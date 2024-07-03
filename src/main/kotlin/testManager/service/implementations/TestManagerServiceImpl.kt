package testManager.service.implementations

import org.springframework.stereotype.Service
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
    override fun saveTest(test: TestInputDTO): TestOutputDTO {
        return if (test.testId != null) {
            updateTest(test)
        } else {
            createTest(test)
        }
    }

    private fun createTest(test: TestInputDTO): TestOutputDTO {
        val savedTest =
            testRepository.save(
                Test(
                    snippetId = test.snippetId,
                    name = test.name,
                ),
            )

        val savedInputs =
            testInputRepository.saveAll(
                test.inputs.map {
                    testInputRepository.save(
                        TestInput(
                            test = savedTest,
                            input = it,
                        ),
                    )
                },
            )

        val savedOutputs =
            testOutputRepository.saveAll(
                test.outputs.map {
                    testOutputRepository.save(
                        TestOutput(
                            test = savedTest,
                            output = it,
                        ),
                    )
                },
            )

        val envs = test.envs.split(";")

        val savedEnvs =
            testEnvRepository.saveAll(
                envs.map { env ->
                    val envSplit = env.split("=")
                    testEnvRepository.save(
                        TestEnv(
                            test = savedTest,
                            key = envSplit[0],
                            value = envSplit[1],
                        ),
                    )
                },
            )

        return TestOutputDTO(
            id = savedTest.id,
            snippetId = savedTest.snippetId,
            name = savedTest.name,
            inputs = savedInputs.map { it.input },
            outputs = savedOutputs.map { it.output },
            envs = savedEnvs.map { TestEnvDto(it.key, it.value) },
        )
    }

    private fun updateTest(test: TestInputDTO): TestOutputDTO {
        // TODO
        return TestOutputDTO(0, 0, "", emptyList(), emptyList(), emptyList())
    }
}
