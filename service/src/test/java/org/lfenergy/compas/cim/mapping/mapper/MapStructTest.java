// SPDX-FileCopyrightText: 2021 Alliander N.V.
//
// SPDX-License-Identifier: Apache-2.0
package org.lfenergy.compas.cim.mapping.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MapStructTest {
    @Test
    void map_WhenPassingASourceClass_ThenDestinationISReturnedAndFilled() {
        var address = new SourceAddress(null, "Amsterdam");
        var source = new Source(1234, "Just some name", address);

        var destination = TestMapper.INSTANCE.sourceToDestination(source);

        assertNotNull(destination);
        assertEquals(source.getId(), destination.getId());
        assertEquals(source.getName(), destination.getSurname());
        assertEquals(0, destination.getHouseNumber());
        assertEquals(source.getAddress().getCity(), destination.getMunicipal());
    }

    @Mapper
    public interface TestMapper {
        TestMapper INSTANCE = Mappers.getMapper(TestMapper.class);

        @Mapping(source = "name", target = "surname")
        @Mapping(source = "address.houseNumber", target = "houseNumber")
        @Mapping(source = "address.city", target = "municipal")
        Destination sourceToDestination(Source source);
    }

    public static class Source {
        private int id;
        private String name;
        private SourceAddress address;

        public Source(int id, String name, SourceAddress address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public SourceAddress getAddress() {
            return address;
        }

        public void setAddress(SourceAddress address) {
            this.address = address;
        }
    }

    public static class SourceAddress {
        private Integer houseNumber;
        private String city;

        public SourceAddress(Integer houseNumber, String city) {
            this.houseNumber = houseNumber;
            this.city = city;
        }

        public Integer getHouseNumber() {
            return houseNumber;
        }

        public void setHouseNumber(Integer houseNumber) {
            this.houseNumber = houseNumber;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }
    }

    public static class Destination {
        private int id;
        private String surname;
        private int houseNumber;
        private String municipal;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public int getHouseNumber() {
            return houseNumber;
        }

        public void setHouseNumber(int houseNumber) {
            this.houseNumber = houseNumber;
        }

        public String getMunicipal() {
            return municipal;
        }

        public void setMunicipal(String municipal) {
            this.municipal = municipal;
        }
    }
}

