package com.board.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Data
@Log4j2
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @NotBlank
    private String loginId;
    @NotBlank
    private String password;
    
}
