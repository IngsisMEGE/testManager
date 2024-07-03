package testManager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import testManager.entity.TestEnv

@Repository
interface TestEnvRepository : JpaRepository<TestEnv, Long> {
    fun deleteAllByTestId(testId: Long)
}
