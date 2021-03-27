package tech.xuanwu.northstar.engine.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.corundumstudio.socketio.SocketIOServer;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.engine.broadcast.SocketIOMessageEngine;
import tech.xuanwu.northstar.engine.event.DisruptorFastEventEngine;
import tech.xuanwu.northstar.engine.event.DisruptorFastEventEngine.WaitStrategyEnum;
import tech.xuanwu.northstar.engine.event.EventEngine;

/**
 * 引擎配置
 * @author KevinHuangwl
 *
 */
@Slf4j
@Configuration
public class EngineConfig {

	@Bean
	public EventEngine createEventEngine() {
		
		return new DisruptorFastEventEngine(WaitStrategyEnum.BlockingWaitStrategy);
	}
	
	@Value("${socketio.host}")
    private String host;
	
	@Value("${socketio.port}")
    private int port;

    private int bossCount = 1;

    private int workCount = 100;

    @Bean
    public SocketIOServer socketIOServer() throws IOException {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setBossThreads(bossCount);
        config.setWorkerThreads(workCount);
        log.info("WebSocket服务地址：{}:{}", host, port);
        SocketIOServer socketServer = new SocketIOServer(config);
        socketServer.start();
        return socketServer;
    }
	
	@Autowired
	@Bean
	public SocketIOMessageEngine createMessageEngine(EventEngine ee, SocketIOServer server) {
		
		return new SocketIOMessageEngine(ee, server);
	}
}
