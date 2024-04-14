package cn.cutie.clot.registry;

import cn.cutie.clot.registry.model.InstanceMeta;
import cn.cutie.clot.registry.service.RegistryService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Description: rest controller for registry service
 * @Author: Cutie
 * @CreateDate: 2024/4/13 19:49
 * @Version: 0.0.1
 */
@Slf4j
@RestController
public class ClotRegistryController {

    @Autowired
    RegistryService registryService;

    @RequestMapping("/reg")
    public InstanceMeta register(@RequestParam String service, @RequestBody InstanceMeta instance){
        log.info("===> register {} @ {}", service, instance);
        instance = registryService.register(service, instance);
        return instance;
    }

    @RequestMapping("/unreg")
    public InstanceMeta unregister(@RequestParam String service, @RequestBody InstanceMeta instance){
        log.info("===> unregister {} @ {}", service, instance);
        registryService.unregister(service, instance);
        return instance;
    }

    @RequestMapping("/findAll")
    public List<InstanceMeta> findAllInstances(@RequestParam String service){
        log.info("===> findAllInstances {}", service);
        return registryService.getAll(service);
    }

    @RequestMapping("/renew")
    public long renew(@RequestParam String service, @RequestBody InstanceMeta instance){
        log.info("===> renew {} @ {}", service, instance);
        return registryService.renew(instance, service);
    }

    @RequestMapping("/renews")
    public long renews(@RequestParam String services, @RequestBody InstanceMeta instance){
        log.info("===> renew {} @ {}", services, instance);
        return registryService.renew(instance, services.split(","));
    }

    @RequestMapping("/version")
    public long version(@RequestParam String service, @RequestBody InstanceMeta instance){
        log.info("===> version {}", service);
        return registryService.version(service);
    }

    @RequestMapping("/versions")
    public Object versions(@RequestParam String services){
        log.info("===> versions {}", services);
        return registryService.versions(services.split(","));
    }

    public static void main(String[] args) {
        InstanceMeta meta = InstanceMeta.http("127.0.0.1", 8081)
                .addParams(Map.of("env", "dev", "tag", "RED"));
        System.out.println(JSON.toJSONString(meta));
        // {"context":"clotrpc","host":"127.0.0.1","parameters":{"env":"dev","tag":"RED"},"port":8081,"scheme":"http","status":false}
    }

}
