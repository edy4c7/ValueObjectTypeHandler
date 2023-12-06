package io.github.edy4c7.valueobjecttypehandler;

import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum TestEnum {
  VALUE1("1"), VALUE2("2");

  @Getter
  private final String value;

  public static TestEnum of(String value) {
    return Stream.of(TestEnum.class.getEnumConstants())
        .filter(e -> e.getValue().equals(value))
        .findFirst()
        .orElseThrow();
  }
}
