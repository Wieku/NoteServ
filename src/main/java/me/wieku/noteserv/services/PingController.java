package me.wieku.noteserv.services;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class Ping {
    public String content = "Pong!";
}

@RestController
public class PingController {

    @RequestMapping("/ping")
    public Ping greeting() {
        return new Ping();
    }

}
