package io.github.ethanzhang.factsplatform.interfaces;

import io.github.ethanzhang.factsplatform.application.IngestEventApplication;
import io.github.ethanzhang.factsplatform.application.IngestRawEventReport;
import io.github.ethanzhang.factsplatform.interfaces.api.IngestEventController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IngestEventController.class)
class IngestEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngestEventApplication ingestEventApplication;

    @Test
    void shouldIngestEvent() throws Exception {
        given(ingestEventApplication.ingest(any()))
                .willReturn(new IngestRawEventReport("123456"));

        mockMvc.perform(post("/event/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "source": "app",
                                  "identifyKey": "user-1",
                                  "eventBody": "{\\"foo\\":\\"bar\\"}",
                                  "zoneId": "Asia/Shanghai"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.message").value("Success"))
                .andExpect(jsonPath("$.data.eventId").value("123456"));
    }
}
