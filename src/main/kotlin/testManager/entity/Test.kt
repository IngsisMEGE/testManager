package testManager.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany

@Entity
data class Test(
    val snippetId: Long,
    var name: String,
    @OneToMany(mappedBy = "test", cascade = [CascadeType.REMOVE])
    val testOutputs: List<TestOutput> = mutableListOf(),
    @OneToMany(mappedBy = "test", cascade = [CascadeType.REMOVE])
    val testInputs: List<TestInput> = mutableListOf(),
    @OneToMany(mappedBy = "test", cascade = [CascadeType.REMOVE])
    val testEnvs: List<TestEnv> = mutableListOf(),
) : BaseEntity() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0
}
