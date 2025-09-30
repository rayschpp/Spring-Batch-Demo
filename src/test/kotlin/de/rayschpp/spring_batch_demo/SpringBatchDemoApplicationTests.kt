package de.rayschpp.spring_batch_demo

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.io.TempDir
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import javax.sql.DataSource

@SpringBootTest
@SpringBatchTest
@ActiveProfiles("test")
class SpringBatchDemoApplicationTests {

    @Autowired
    lateinit var dataSource: DataSource

    @Autowired
    lateinit var env: Environment

    @Test
    fun contextLoads() {
        with(SoftAssertions()) {
            assertThat(env.getProperty("spring.batch.jdbc.platform")).isEqualTo("sqlite")
            assertThat(env.getProperty("spring.datasource.url")).isNotEmpty
            assertThat(env.getProperty("spring.sql.init.mode")).isEqualTo("always")
            assertThat(env.getProperty("spring.sql.init.schema-locations")).isNotEmpty
            assertAll()
        }
    }

    /**
     * Test to validate if required batch database tables are created.
     */
    @Suppress("SqlDialectInspection")
    @Test
    fun testBatchTablesCreated() {
        dataSource.connection.use { connection ->
            val tables = listOf(
                "BATCH_JOB_INSTANCE",
                "BATCH_JOB_EXECUTION",
                "BATCH_JOB_EXECUTION_PARAMS",
                "BATCH_STEP_EXECUTION"
            )
            val statement = connection.createStatement()
            tables.forEach { table ->
                assertDoesNotThrow { statement.execute("SELECT COUNT(*) FROM $table") }
            }
        }
    }

    companion object {
        /**
         * Overwrite datasource URL to use a temporary SQLite database file.
         */
        @Suppress("unused")
        @DynamicPropertySource
        fun tempDatasource(@TempDir tempDir: java.io.File, registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { "jdbc:sqlite:" + tempDir.resolve("spring-batch-demo.db").absolutePath }
        }
    }

}
