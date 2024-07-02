package testManager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TestManagerApplication

fun main(args: Array<String>) {
    runApplication<TestManagerApplication>(*args)
}
