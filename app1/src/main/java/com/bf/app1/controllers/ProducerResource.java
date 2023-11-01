package com.bf.app1.controllers;

import com.bf.app1.dtos.Event;
import com.bf.app1.trace.MultiValueMapHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.TransportType;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController()
@Slf4j
@RequiredArgsConstructor
public class ProducerResource {

  private final RestTemplate template = new RestTemplateBuilder().build();
  private final String app2 = "http://localhost:8082";

  @PostMapping("/produce")
  public Event handle(@RequestBody Event event, @RequestHeader MultiValueMap<String, String> headers) {
    MultiValueMapHeaders headers1 = MultiValueMapHeaders.build(HeaderType.HTTP, headers);

    if ("true".equalsIgnoreCase(headers.getFirst("x-debug-trace"))) {
      NewRelic.getAgent().getTransaction().insertDistributedTraceHeaders(headers1);
      NewRelic.getAgent().getTransaction().acceptDistributedTraceHeaders(TransportType.HTTP, headers1);
    }

    event.setId(UUID.randomUUID().toString());
    log.info("Event received :{}", event);

    HttpEntity<Event> httpEntity = new HttpEntity<>(event, headers1.getMapCopy());
    String url = String.format("%s/process", app2);
    ResponseEntity<Event> exchange = template.exchange(url, HttpMethod.POST, httpEntity, Event.class);

    return exchange.getBody();
  }

}
