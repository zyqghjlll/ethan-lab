package io.github.ethanzhang.factsplatform.interfaces;

import io.github.ethanzhang.common.web.Message;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("email")
public class SendEmailController {

    @PostMapping("send")
    public Message<String> sendEmail(@RequestParam("message") String message) {
        return Message.succeed(message);
    }
}
