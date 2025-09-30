package de.rayschpp.spring_batch_demo.batch

import org.slf4j.LoggerFactory
import org.springframework.batch.core.Job
import org.springframework.batch.core.Step
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.batch.repeat.RepeatStatus.FINISHED
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager

@Configuration
class ExampleJobConfig {
    private val log = LoggerFactory.getLogger(ExampleJobConfig::class.java)

    @Bean
    fun exampleStep(jobRepository: JobRepository, transactionManager: PlatformTransactionManager): Step = StepBuilder("exampleStep", jobRepository)
        .tasklet({ _, chunkContext ->
            val params = chunkContext.stepContext.jobParameters
            log.info("Hello from Example Step! params={}", params)
            FINISHED
        }, transactionManager)
        .build()

    @Bean
    fun exampleJob(jobRepository: JobRepository, exampleStep: Step): Job = JobBuilder("exampleJob", jobRepository)
        .incrementer(RunIdIncrementer())
        .start(exampleStep)
        .build()
}
