package testManager.repository

import org.springframework.data.jpa.repository.JpaRepository
import testManager.entity.TestEnv

interface TestEnvRepository : JpaRepository<TestEnv, Long>
