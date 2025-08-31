package com.fcmh.femcodersmentorhub.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SessionDuration {
    SHORT(30),
    LONG(60);

    private final int minutes;
}
