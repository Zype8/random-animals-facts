package com.michalk7.random_animals_facts.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public  class GeneratorParametersDTO {

    @NotNull(message = "Animal Type is mandatory")
    private String animalType;

    @NotNull
    @Min(value = 1, message = "Facts count should be greater or equal 1")
    private int factsAmount;
}
