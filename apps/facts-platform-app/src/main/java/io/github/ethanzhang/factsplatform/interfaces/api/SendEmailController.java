package io.github.ethanzhang.factsplatform.interfaces.api;

import common.web.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("email")
@RequiredArgsConstructor
public class SendEmailController {

    @PostMapping("send")
    public Message<String> sendEmail(@RequestParam("message") String message) {
        return Message.succeed(message);
    }
}
