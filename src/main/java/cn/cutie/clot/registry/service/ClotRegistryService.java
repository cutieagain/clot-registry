package cn.cutie.clot.registry.service;

import cn.cutie.clot.registry.model.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @Description: RegistryService默认实现
 * @Author: Cutie
 * @CreateDate: 2024/4/13 19:33
 * @Version: 0.0.1
 */
@Slf4j
public class ClotRegistryService implements RegistryService{

    // 注册的服务
    final static MultiValueMap<String, InstanceMeta> REGISTRY = new LinkedMultiValueMap<>();

    // 版本
    final static Map<String, Long> VERSIONS = new ConcurrentHashMap<>();

    // 时间戳更新，用于检测服务是否过期
    public final static Map<String, Long> TIMESTAMPS = new ConcurrentHashMap<>();

    // 版本号
    final static  AtomicLong VERSION = new AtomicLong(0);

    @Override
    public InstanceMeta register(String service, InstanceMeta instance) {
        List<InstanceMeta> instanceMetas = REGISTRY.get(service);
        if (instanceMetas != null && !instanceMetas.isEmpty()){
            if (instanceMetas.contains(instance)){
                log.info("===> instance {} already registered", instance.toUrl());
                instance.setStatus(true);
                return instance;
            }
        }
        log.info("===> registry instance {}", instance.toUrl());
        REGISTRY.add(service, instance);
        instance.setStatus(true);
        renew(instance, service);
        VERSIONS.put(service, VERSION.incrementAndGet());
        return instance;
    }

    @Override
    public void unregister(String service, InstanceMeta instance) {
        List<InstanceMeta> instanceMetas = REGISTRY.get(service);
        if (instanceMetas == null || instanceMetas.isEmpty()){
            log.info("===> unregister  instance{} not found", instance);
            return;
        }
        instanceMetas.removeIf(ins -> ins.equals(instance));
        instance.setStatus(false);
        renew(instance, service);
        VERSIONS.put(service, VERSION.incrementAndGet());
        log.info("===> instance {} already unregistered", instance.toUrl());
    }

    @Override
    public List<InstanceMeta> getAll(String serviceName) {
        return REGISTRY.get(serviceName);
    }

    @Override
    public long renew(InstanceMeta instance, String... services){
        // 时间戳做探活没针对的其实是实例
        long now = System.currentTimeMillis();
        for (String service : services) {
            TIMESTAMPS.put(service + "@" + instance.toUrl(), now);
        }
        return now;
    }

    @Override
    public Long version(String service){
        return VERSIONS.get(service);
    }

    @Override
    public Map<String, Long> versions(String... services){
        return Arrays.stream(services)
                .collect(Collectors.toMap(x-> x, VERSIONS::get, (a, b) -> b));
    }

}
