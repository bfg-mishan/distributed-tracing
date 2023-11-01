package com.bf.app2.controllers;

import com.bf.app2.dtos.Event;
import com.bf.app2.trace.MultiValueMapHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.NewRelic;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController()
@Slf4j
@RequiredArgsConstructor
public class EventResource {

  private RestTemplate template = new RestTemplateBuilder().build();

  @PostMapping("/process")
  public Event handle(@RequestBody Event event, @RequestHeader MultiValueMap<String, String> headers) {
    //register trace context from another service
    if ("true".equalsIgnoreCase(headers.getFirst("x-debug-trace"))) {
      MultiValueMapHeaders valueMapHeaders = MultiValueMapHeaders.build(HeaderType.HTTP, headers);
      NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(valueMapHeaders);
    }

    event.setId(UUID.randomUUID().toString());
    log.info("Event received :{}, Headers: {}", event, headers);
    event.setData("Processed:" + event.getData());
    return event;
  }

}
