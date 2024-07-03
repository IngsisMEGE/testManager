package testManager.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import testManager.entity.Test

@Repository
interface TestRepository : JpaRepository<Test, Long>
