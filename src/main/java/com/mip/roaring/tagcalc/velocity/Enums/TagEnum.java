package com.mip.roaring.tagcalc.velocity.Enums;

public enum TagEnum {

    or("或关系"),
    and("与关系");

    private String text;

    TagEnum(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
