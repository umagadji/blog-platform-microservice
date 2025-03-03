package ru.umagadzhi.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserEvent {
    private Long id;
    private String username;
    private String email;
    private String eventType; // Например, "USER_REGISTERED"
}
