package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class StringDuplicatorTest {

    private StringDuplicator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new StringDuplicator();
    }

    @Test
    public void duplicateString_whenStringIsEmpty_returnEmptyString() throws Exception {
        String result = SUT.duplicate("");
        Assert.assertThat(result, is(""));
    }

    @Test
    public void duplicateString_whenStringIsSingleCharacter_returnTheSingleCharacter() throws Exception {
        String result = SUT.duplicate("b");
        Assert.assertThat(result, is("bb"));
    }

    @Test
    public void duplicateString_whenLongString_returnTheLongString() throws Exception {
        String sentence = "Belema Ogan wrote this";

        String result = SUT.duplicate(sentence);

        Assert.assertThat(result, is(sentence + sentence));
    }
}