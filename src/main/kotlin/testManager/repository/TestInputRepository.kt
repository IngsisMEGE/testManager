package testManager.repository

import org.springframework.data.jpa.repository.JpaRepository
import testManager.entity.TestInput

interface TestInputRepository : JpaRepository<TestInput, Long>
