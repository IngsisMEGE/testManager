package testManager.repository

import org.springframework.data.jpa.repository.JpaRepository
import testManager.entity.TestOutput

interface TestOutputRepository : JpaRepository<TestOutput, Long>
