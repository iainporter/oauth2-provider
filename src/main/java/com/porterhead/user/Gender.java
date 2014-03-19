package com.porterhead.user;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author: Iain Porter
 */
@XmlRootElement
@XmlEnum
public enum Gender {

    male("male"), female("female");

    private String value;

    Gender(String value) {
        this.value = value.toLowerCase();
    }

    Gender fromValue(String value) {
         if(value != null) {
            for(Gender gender : values()) {
                if(value.equalsIgnoreCase(gender.value)) {
                    return gender;
                }
            }
         }
        throw new IllegalArgumentException("Invalid value: " + value);
    }


}
