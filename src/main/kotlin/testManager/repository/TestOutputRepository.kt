package testManager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import testManager.entity.TestOutput

@Repository
interface TestOutputRepository : JpaRepository<TestOutput, Long> {
    fun deleteAllByTestId(testId: Long)
}
