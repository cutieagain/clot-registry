package cn.cutie.clot.registry.health;

import cn.cutie.clot.registry.model.InstanceMeta;
import cn.cutie.clot.registry.service.ClotRegistryService;
import cn.cutie.clot.registry.service.RegistryService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: Cutie
 * @CreateDate: 2024/4/13 20:42
 * @Version: 0.0.1
 */
@Slf4j
public class ClotHealthChecker implements HealthChecker{
    final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    RegistryService registryService;

    long timeout = 20_000; // 20s

    public ClotHealthChecker(RegistryService registryService) {
        this.registryService = registryService;
    }

    @Override
    public void start() {
        executorService.scheduleWithFixedDelay(()->{
            log.info("===> Health check running...");
            // 找出服务的时间戳，与现在时间相差15s的剔除
            long now = System.currentTimeMillis();
            ClotRegistryService.TIMESTAMPS.keySet().stream().forEach(serviceAndInst -> {
                long timestamp = ClotRegistryService.TIMESTAMPS.get(serviceAndInst);
                if (now - timestamp > timeout) {
                    log.info(" ===> Health checker: {} is down", serviceAndInst);
                    int index = serviceAndInst.indexOf("@");
                    String service = serviceAndInst.substring(0, index);
                    String url = serviceAndInst.substring(index + 1);
                    InstanceMeta instance = InstanceMeta.from(url);
                    registryService.unregister(service, instance);
                    ClotRegistryService.TIMESTAMPS.remove(serviceAndInst);
                }
            });
        }, 10, 30, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        executorService.shutdown();
    }
}
