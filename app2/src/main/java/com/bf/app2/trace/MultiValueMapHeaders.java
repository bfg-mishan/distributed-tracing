package com.bf.app2.trace;

import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class MultiValueMapHeaders implements Headers {
  private final HeaderType headerType;
  private final MultiValueMap<String, String> headers;

  private MultiValueMapHeaders(HeaderType headerType) {
    this(headerType, new LinkedMultiValueMap<>());
  }

  private MultiValueMapHeaders(HeaderType headerType, MultiValueMap<String, String> headers) {
    this.headerType = headerType;
    this.headers = headers;
  }

  public static MultiValueMapHeaders build(HeaderType headerType) {
    return new MultiValueMapHeaders(headerType);
  }

  public static MultiValueMapHeaders build(HeaderType headerType, MultiValueMap<String, String> map) {
    return new MultiValueMapHeaders(headerType, map);
  }

  @Override
  public HeaderType getHeaderType() {
    return this.headerType;
  }

  @Override
  public String getHeader(String name) {
    return this.headers.getFirst(name);
  }

  @Override
  public Collection<String> getHeaders(String name) {
    return this.headers.get(name);
  }

  @Override
  public void setHeader(String name, String value) {
    List<String> list = new LinkedList<>();
    list.add(value);
    headers.put(name, list);
  }

  @Override
  public void addHeader(String name, String value) {
    List<String> headers = this.headers.getOrDefault(name, new LinkedList<>());
    headers.add(value);
    this.headers.put(name, headers);
  }

  @Override
  public Collection<String> getHeaderNames() {
    return headers.keySet();
  }

  @Override
  public boolean containsHeader(String name) {
    return headers.containsKey(name);
  }

  public MultiValueMap<String, String> getMapCopy() {
    return new LinkedMultiValueMap<>(headers);
  }
}
