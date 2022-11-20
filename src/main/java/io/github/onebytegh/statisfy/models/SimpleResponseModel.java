package io.github.onebytegh.statisfy.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public final class SimpleResponseModel {
    private final boolean error;
    private final String message;
}
