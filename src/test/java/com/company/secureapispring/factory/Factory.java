package com.company.secureapispring.factory;

import com.company.secureapispring.entities.Country;
import com.company.secureapispring.entities.StateProvince;
import com.github.javafaker.Faker;

public class Factory {
    private static Faker faker = Faker.instance().instance();

    public static Faker getFaker() {
        return faker;
    }

    public static Builder<Country> country() {
        return Builder
                .of(Country::new)
                .with(Country::setAbbreviation, faker.country().countryCode3())
                .with(Country::setName, faker.country().name());
    }

    public static Builder<StateProvince> stateProvince() {
        return Builder
                .of(StateProvince::new)
                .with(StateProvince::setAbbreviation, faker.address().stateAbbr())
                .with(StateProvince::setName, faker.address().state());
    }
}
