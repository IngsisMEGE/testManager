package testManager.service.interfaces

import testManager.controller.payload.request.TestInputDTO
import testManager.controller.payload.response.TestOutputDTO

interface TestManagerService {
    fun saveTest(test: TestInputDTO): TestOutputDTO
}
