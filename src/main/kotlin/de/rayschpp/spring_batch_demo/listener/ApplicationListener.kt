package de.rayschpp.spring_batch_demo.listener

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import javax.sql.DataSource

@Component
class ApplicationListener(
    private val dataSource: DataSource
) {
    private val logger: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    @EventListener(ApplicationStartedEvent::class)
    fun onApplicationStartedEvent(): Unit = logger.info("Using datasource: {}", dataSource.connection.metaData.url)
}
