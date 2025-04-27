package com.caesaryh.comp2005fe.utils.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient {
    private Integer id;

    @JsonProperty("surname")
    private String surname;

    @JsonProperty("forename")
    private String forename;

    @JsonProperty("nhsNumber")
    private String nhsNumber;
}