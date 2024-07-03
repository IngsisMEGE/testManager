package testManager.repository

import org.springframework.data.jpa.repository.JpaRepository
import testManager.entity.Test

interface TestRepository : JpaRepository<Test, Long>
