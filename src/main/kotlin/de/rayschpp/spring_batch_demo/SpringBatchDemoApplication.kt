package de.rayschpp.spring_batch_demo

import jakarta.annotation.security.RunAs
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableBatchProcessing
class SpringBatchDemoApplication

fun main(args: Array<String>) {
	runApplication<SpringBatchDemoApplication>(*args)
}
