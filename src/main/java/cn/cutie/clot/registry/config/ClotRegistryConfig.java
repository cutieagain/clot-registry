package cn.cutie.clot.registry.config;

import cn.cutie.clot.registry.health.ClotHealthChecker;
import cn.cutie.clot.registry.health.HealthChecker;
import cn.cutie.clot.registry.service.ClotRegistryService;
import cn.cutie.clot.registry.service.RegistryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: bean的配置文件
 * @Author: Cutie
 * @CreateDate: 2024/4/13 19:50
 * @Version: 0.0.1
 */
@Configuration
public class ClotRegistryConfig {
    @Bean
    public RegistryService registryService(){
        return new ClotRegistryService();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public HealthChecker healthChecker(@Autowired RegistryService registryService){
        return new ClotHealthChecker(registryService);
    }

}
