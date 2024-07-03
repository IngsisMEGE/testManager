package testManager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import testManager.entity.TestInput

@Repository
interface TestInputRepository : JpaRepository<TestInput, Long> {
    fun deleteAllByTestId(testId: Long)
}
