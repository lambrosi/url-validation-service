package com.lucasambrosi.axr.urlvalidationservice.input;

import java.io.Serializable;

public class RegexInput implements Serializable {

    private String client;
    private String regex;

    public RegexInput() {
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public String toString() {
        return "RegexInput{" +
                "client='" + client + '\'' +
                ", regex='" + regex + '\'' +
                '}';
    }
}
