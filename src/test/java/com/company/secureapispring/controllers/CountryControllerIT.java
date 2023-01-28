package com.company.secureapispring.controllers;


import com.company.secureapispring.entities.Country;
import com.company.secureapispring.factory.Factory;
import com.company.secureapispring.repositories.CountryRepository;
import com.company.secureapispring.utils.TestJWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CountryControllerIT extends AbstractIT {

    private static String COUNTRY_ENDPOINT = "/api/countries";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testGetWhenAuthenticated() throws Exception {
        Country expected = Factory
                .country()
                .build(countryRepository::saveAndFlush);
        mockMvc.perform(get(CountryControllerIT.COUNTRY_ENDPOINT + "/" + expected.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(expected.getId())))
                .andExpect(jsonPath("abbreviation", is(expected.getAbbreviation())))
                .andExpect(jsonPath("name", is(expected.getName())));
    }

    @Test
    public void testGetWithoutAuthenticationThenFail() throws Exception {
        Integer id = Factory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(CountryControllerIT.COUNTRY_ENDPOINT + "/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetWhenAuthenticatedAndNotFoundThenFail() throws Exception {
        Integer id = Factory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(CountryControllerIT.COUNTRY_ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindAllWhenAuthenticated() throws Exception {
        List<Country> expected =  IntStream
                .range(1, Factory.getFaker().number().numberBetween(2, 5))
                .mapToObj(n -> Factory
                        .country()
                        .build(countryRepository::saveAndFlush)
                )
                .sorted(Comparator.comparing(Country::getName))
                .collect(Collectors.toList());
        int lastIndex = expected.size()-1;
        mockMvc.perform(get(CountryControllerIT.COUNTRY_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expected.size())))
                .andExpect(jsonPath("[0].name", equalTo(expected.get(0).getName())))
                .andExpect(jsonPath(String.format("[%d].name", lastIndex), equalTo(expected.get(lastIndex).getName())));
    }

    @Test
    public void testFindAllWithoutAuthentication() throws Exception {
        this.mockMvc.perform(
                        get(CountryControllerIT.COUNTRY_ENDPOINT)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
