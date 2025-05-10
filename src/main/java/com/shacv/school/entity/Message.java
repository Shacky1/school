package com.shacv.school.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {
    private String sender;
    private String content;
    private LocalDateTime timestamp;
    // Getters and setters
}
