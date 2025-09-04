package com.fcmh.femcodersmentorhub.requests;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SessionDuration {
    SHORT(45),
    LONG(90);

    private final int minutes;
}