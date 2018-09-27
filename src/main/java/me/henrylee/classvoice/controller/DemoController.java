package me.henrylee.classvoice.controller;

import me.henrylee.classvoice.model.DemoEntity;
import me.henrylee.classvoice.service.DemoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/demo")
public class DemoController {

    private DemoService service;

    public DemoController(DemoService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.POST)
    public DemoEntity addDemoEntity(@RequestBody DemoEntity demoEntity) {
        return service.addDemoEntity(demoEntity);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public DemoEntity getDemoEntity(@PathVariable String id) {
        return service.getDemoEntity(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public DemoEntity updateDemoEntity(@PathVariable String id, @RequestBody DemoEntity demoEntity) {
        return service.updateDemoEntity(id, demoEntity);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public DemoEntity delDemoEntity(@PathVariable String id) {
        return service.delDemoEntity(id);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByKey")
    public List<DemoEntity> getDemoByKey(@RequestParam("key") String key) {
        return service.getDemoByKey(key);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/findByVal")
    public List<DemoEntity> getDemoByVal(@RequestParam("val") String val) {
        return service.getDemoByVal(val);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}/getId")
    public String getIdTest(@PathVariable String id) {
        return String.format("ID is: %s", id);
    }
}
