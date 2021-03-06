package com.tngtech.configbuilder.util;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.tngtech.configbuilder.annotation.valueextractor.CommandLineValue;
import com.tngtech.configbuilder.configuration.ErrorMessageSetup;
import com.tngtech.configbuilder.exception.ConfigBuilderException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CommandLineHelperTest {

    private static class TestConfig {
        @CommandLineValue(shortOpt = "u", longOpt = "user", required = true)
        public String aString;
        @CommandLineValue(shortOpt = "v", longOpt = "vir", required = false)
        public String anotherString;
    }

    private CommandLineHelper commandLineHelper;
    private String[] args = null;

    @Mock
    private Options options;
    @Mock
    private GnuParser parser;
    @Mock
    private CommandLine commandLine;
    @Mock
    private ConfigBuilderFactory configBuilderFactory;
    @Mock
    private AnnotationHelper annotationHelper;
    @Mock
    private ErrorMessageSetup errorMessageSetup;


    @Before
    public void setUp() throws Exception {

        when(configBuilderFactory.getInstance(AnnotationHelper.class)).thenReturn(annotationHelper);
        when(configBuilderFactory.getInstance(ErrorMessageSetup.class)).thenReturn(errorMessageSetup);

        commandLineHelper = new CommandLineHelper(configBuilderFactory);

        Set<Field> fields = Sets.newHashSet(TestConfig.class.getDeclaredFields());
        when(annotationHelper.getFieldsAnnotatedWith(TestConfig.class, CommandLineValue.class)).thenReturn(fields);
        when(parser.parse(options, args)).thenReturn(commandLine);
    }

    @Test
    public void testGetCommandLine() throws Exception {
        when(configBuilderFactory.createInstance(GnuParser.class)).thenReturn(parser);
        when(configBuilderFactory.createInstance(Options.class)).thenReturn(options);
        ArgumentCaptor<Option> captor = ArgumentCaptor.forClass(Option.class);
        assertEquals(commandLine, commandLineHelper.getCommandLine(TestConfig.class, args));
        verify(options, times(2)).addOption(captor.capture());
        verify(parser).parse(options, args);
        List<Option> options = captor.getAllValues();

        assertEquals(2, options.size());

        final ImmutableList<Option> sortedOptions = FluentIterable.from(options).toSortedList(new Comparator<Option>() {
            @Override
            public int compare(Option o1, Option o2) {
                return o1.getLongOpt().compareTo(o2.getLongOpt());
            }
        });

        assertEquals("user", sortedOptions.get(0).getLongOpt());
        assertEquals("u", sortedOptions.get(0).getOpt());
        assertEquals(true, sortedOptions.get(0).isRequired());

        assertEquals("vir", sortedOptions.get(1).getLongOpt());
        assertEquals("v", sortedOptions.get(1).getOpt());
        assertEquals(false, sortedOptions.get(1).isRequired());
    }

    @Test(expected = ConfigBuilderException.class)
    public void testGetCommandLineThrowsException() throws Exception {
        when(configBuilderFactory.createInstance(GnuParser.class)).thenReturn(new GnuParser());
        when(configBuilderFactory.createInstance(Options.class)).thenReturn(new Options());
        args = new String[]{"nd", "notDefined"};
        commandLineHelper.getCommandLine(TestConfig.class, args);
    }

    @Test
    public void testGetOptions() throws Exception {
        Options options1 = new Options();
        when(configBuilderFactory.createInstance(Options.class)).thenReturn(options1);
        assertEquals(options1, commandLineHelper.getOptions(TestConfig.class));
        assertEquals("user", options1.getOption("user").getLongOpt());
        assertEquals("v", options1.getOption("vir").getOpt());
    }
}
