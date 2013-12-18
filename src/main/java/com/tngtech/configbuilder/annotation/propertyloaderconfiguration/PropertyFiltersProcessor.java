package com.tngtech.configbuilder.annotation.propertyloaderconfiguration;

import com.google.common.collect.Maps;
import com.tngtech.propertyloader.PropertyLoader;
import com.tngtech.propertyloader.impl.DefaultPropertyFilterContainer;
import com.tngtech.propertyloader.impl.filters.DecryptingFilter;
import com.tngtech.propertyloader.impl.filters.EnvironmentResolvingFilter;
import com.tngtech.propertyloader.impl.filters.ThrowIfPropertyHasToBeDefined;
import com.tngtech.propertyloader.impl.filters.VariableResolvingFilter;
import com.tngtech.propertyloader.impl.filters.WarnOnSurroundingWhitespace;
import com.tngtech.propertyloader.impl.interfaces.PropertyLoaderFilter;

import java.lang.annotation.Annotation;
import java.util.Map;

public class PropertyFiltersProcessor implements PropertyLoaderConfigurationProcessor {

  private static interface Action {
    void execute();
  }

  public void configurePropertyLoader( Annotation annotation, PropertyLoader propertyLoader ) {
    DefaultPropertyFilterContainer filterContainer = propertyLoader.getFilters();
    Map<Class<? extends PropertyLoaderFilter>, Action> classActionMap = createFilterMap( filterContainer );

    filterContainer.clear();
    Class<? extends PropertyLoaderFilter>[] filters = ((PropertyFilters) annotation).value();
    for ( Class<? extends PropertyLoaderFilter> filter : filters ) {
      if ( classActionMap.containsKey( filter ) ) {
        classActionMap.get( filter ).execute();
      } else {
        throw new IllegalStateException( "unhandled filter class " + filter.getSimpleName() );
      }
    }
  }

  private Map<Class<? extends PropertyLoaderFilter>, Action> createFilterMap( final DefaultPropertyFilterContainer
                                                                                  filterContainer ) {
    Map<Class<? extends PropertyLoaderFilter>, Action> actionMap = Maps.newHashMap();

    actionMap.put( VariableResolvingFilter.class, new Action() {
      @Override
      public void execute() {
        filterContainer.withVariableResolvingFilter();
      }
    } );

    actionMap.put( DecryptingFilter.class, new Action() {
      @Override
      public void execute() {
        filterContainer.withDecryptingFilter();
      }
    } );

    actionMap.put( EnvironmentResolvingFilter.class, new Action() {
      @Override
      public void execute() {
        filterContainer.withEnvironmentResolvingFilter();
      }
    } );

    actionMap.put( WarnOnSurroundingWhitespace.class, new Action() {
      @Override
      public void execute() {
        filterContainer.withWarnOnSurroundingWhitespace();
      }
    } );

    actionMap.put( ThrowIfPropertyHasToBeDefined.class, new Action() {
      @Override
      public void execute() {
        filterContainer.withWarnIfPropertyHasToBeDefined();
      }
    } );

    return actionMap;
  }
}
