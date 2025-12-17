package com.michalk7.random_animals_facts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.michalk7.random_animals_facts.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class FactsServiceImpl implements FactsService {

    private final WebClient webClient;

    private static final String DOG_API = "https://dogapi.dog/api/v2/facts";
    private static final String CAT_API = "https://meowfacts.herokuapp.com";

    public FactsServiceImpl(WebClient.Builder webClientBuilder) {
            this.webClient = webClientBuilder.build();

    }

    @Override
    public List<Fact> callApiForFacts(String animalType, int factsAmount) {
        String baseUrl = getBaseUrl(animalType);
        String paramName = getParamName(animalType);
        if(animalType.equalsIgnoreCase("Dog") && factsAmount>5){
            factsAmount=5;
        }
        int finalFactsAmount = factsAmount;

        Mono<String> factsStream = this.webClient.get()
                .uri(baseUrl, uriBuilder -> uriBuilder
                        .queryParam(paramName, finalFactsAmount)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<String>() {});

        ObjectMapper objectMapper = new ObjectMapper();
        String factList = factsStream.block();
        if(animalType.equalsIgnoreCase("Dog")){
            try {
                DogFactResponse factResponse = objectMapper.readValue(factList, DogFactResponse.class);
                return dogFactResponsetoFact(factResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        else if(animalType.equalsIgnoreCase("Cat")){
            try {
                CatFactResponse factResponse = objectMapper.readValue(factList, CatFactResponse.class);
                return catFactResponsetoFact(factResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            throw new RuntimeException("Invalid animalType: " + animalType);
        }
    }

    private String getBaseUrl(String animalType) {
        if ("Dog".equalsIgnoreCase(animalType)) {
            return DOG_API;
        } else if ("Cat".equalsIgnoreCase(animalType)) {
            return CAT_API;
        } else {
            throw new RuntimeException("Invalid animalType: " + animalType);
        }
    }

    private String getParamName(String animalType){
        if ("Dog".equalsIgnoreCase(animalType)) {
            return "limit";
        } else if ("Cat".equalsIgnoreCase(animalType)) {
            return "count";
        } else {
            throw new RuntimeException("Invalid animalType: " + animalType);
        }
    }

    private List<Fact> catFactResponsetoFact(CatFactResponse response){
        List<String> list = response.getData();
        List<Fact> facts = new ArrayList<>();
        for (String item : list){
            Fact fact = new Fact();
            fact.setText(item);
            fact.setUser("System");
            fact.setSource(getBaseUrl("Cat"));
            Status status = new Status();
            status.setSentCount(1);
            status.setVerified(true);
            fact.setStatus(status);
            facts.add(fact);
        }
        return facts;
    }

    private List<Fact> dogFactResponsetoFact(DogFactResponse response){
        List<Data> listdata = response.getData();
        List<String> list = new ArrayList<>();
        for (Data item: listdata){
            String factText = item.attributes().body();
            list.add(factText);
        }
        List<Fact> facts = new ArrayList<>();
        for (String item : list){
            Fact fact = new Fact();
            fact.setText(item);
            fact.setUser("System");
            fact.setSource(getBaseUrl("Dog"));
            Status status = new Status();
            status.setSentCount(1);
            status.setVerified(true);
            fact.setStatus(status);
            facts.add(fact);
        }
        return facts;
    }

}
