package com.sinignaci.catalogservice.handler;

import lombok.Builder;

@Builder
public record ErrorMessage(String type, String message) {
}
