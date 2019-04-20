package com.lucasambrosi.axr.urlvalidationservice.output;

import java.io.Serializable;

public class ValidationOutput implements Serializable {

    private boolean match;
    private String regex;
    private Integer correlationId;

    public ValidationOutput() {
    }

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public Integer getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(Integer correlationId) {
        this.correlationId = correlationId;
    }

    @Override
    public String toString() {
        return "ValidationOutput{" +
                "match=" + match +
                ", regex='" + regex + '\'' +
                ", correlationId=" + correlationId +
                '}';
    }
}
