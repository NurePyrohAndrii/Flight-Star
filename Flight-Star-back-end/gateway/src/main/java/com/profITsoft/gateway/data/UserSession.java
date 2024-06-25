package com.profITsoft.gateway.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.Instant;

@Getter
@Setter
@RedisHash("UserSession")
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSession {

  @Id
  private String id;
  private String email;
  private String name;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
  private Instant expiresAt;

  public boolean isExpired() {
    return expiresAt.isBefore(Instant.now());
  }
}
