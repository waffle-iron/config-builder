package com.tngtech.configbuilder;

import com.google.common.collect.Lists;
import com.tngtech.configbuilder.annotations.*;
import com.tngtech.configbuilder.interfaces.FieldValueProvider;
import com.tngtech.propertyloader.PropertyLoader;

import java.util.Collection;

@PropertyExtension("testproperties")
@PropertySuffixes(extraSuffixes = {"test"})
@PropertyLocations(resourcesForClasses = {PropertyLoader.class})
@PropertiesFiles("demoapp-configuration")
@ErrorMessageFile("errors")
public class Config {

    public static class PidFixFactory implements FieldValueProvider<Collection<String>> {

        public Collection<String> getValue(String optionValue) {
            Collection<String> coll = Lists.newArrayList();
            coll.add(optionValue + " success");
            return coll;
        }
    }

    @DefaultValue("user")
    private String userName;

    @PropertyValue("a")
    private String helloWorld;

    @CommandLineValue(shortOpt = "u", longOpt = "user")
    private String surName;

    @CommandLineValue(shortOpt = "p", longOpt = "pidFixFactory")
    @ValueProvider(PidFixFactory.class)
    private Collection<String> pidFixes;

    public String getValue(){
        return userName;
    }

    public String getHelloWorld(){
        return helloWorld;
    }

    public String getSurName(){
        return surName;
    }

    public Collection<String> getPidFixes() {
        return pidFixes;
    }
}