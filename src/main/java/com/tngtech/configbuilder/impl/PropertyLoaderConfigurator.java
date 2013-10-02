package com.tngtech.configbuilder.impl;


import com.tngtech.configbuilder.annotationprocessors.interfaces.IPropertyLoaderConfigurationProcessor;
import com.tngtech.configbuilder.annotations.metaannotations.PropertyLoaderConfigurationAnnotation;
import com.tngtech.propertyloader.PropertyLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class PropertyLoaderConfigurator {
    private AnnotationUtils annotationUtils;
    private MiscFactory miscFactory;

    @Autowired
    public PropertyLoaderConfigurator(AnnotationUtils annotationUtils, MiscFactory miscFactory) {
        this.annotationUtils = annotationUtils;
        this.miscFactory = miscFactory;
    }

    public PropertyLoader configurePropertyLoader(Class<?> configClass) {

        PropertyLoader propertyLoader = miscFactory.createPropertyLoader().withDefaultConfig();
        for (Annotation annotation : annotationUtils.getAnnotationsAnnotatedWith(configClass.getDeclaredAnnotations(), PropertyLoaderConfigurationAnnotation.class)) {
            Class<? extends IPropertyLoaderConfigurationProcessor> processor;
            processor = annotation.annotationType().getAnnotation(PropertyLoaderConfigurationAnnotation.class).value();
            miscFactory.getPropertyConfiguratorProcessor(processor).configurePropertyLoader(annotation, propertyLoader);
        }
        return propertyLoader;
    }
}
