package cn.cutie.clot.registry.service;

import cn.cutie.clot.registry.model.InstanceMeta;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: Cutie
 * @CreateDate: 2024/4/13 19:26
 * @Version: 0.0.1
 */
public interface RegistryService {
    InstanceMeta register(String service, InstanceMeta instance);
    void unregister(String service, InstanceMeta instance);
    List<InstanceMeta> getAll(String service);

    long renew(InstanceMeta instance, String... services);

    Long version(String service);

    Map<String, Long> versions(String... services);


}
