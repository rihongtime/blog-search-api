package com.rihongo.search.config.datasource

import com.rihongo.search.util.Logger
import com.rihongo.search.util.PortCheckUtil.findAvailablePort
import com.rihongo.search.util.PortCheckUtil.isRunning
import org.h2.tools.Server
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextClosedEvent
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import java.sql.SQLException

@Configuration
class H2ServerConfig(
    private val h2Properties: H2Properties
) {
    private var webServer: Server? = null
    private val logger by Logger()

    @EventListener(ContextRefreshedEvent::class)
    @Throws(SQLException::class)
    fun start() {
        val h2Port = if (isRunning(h2Properties.port.toInt())) findAvailablePort().toString() else h2Properties.port
        logger.info("-----------------------------------")
        logger.info("[H2ServerConfig] web port : $h2Port")
        logger.info("-----------------------------------")
        webServer = Server.createWebServer("-webPort", h2Port as String?)
        webServer?.start()
    }

    @EventListener(ContextClosedEvent::class)
    fun stop() {
        webServer?.stop()
    }
}
