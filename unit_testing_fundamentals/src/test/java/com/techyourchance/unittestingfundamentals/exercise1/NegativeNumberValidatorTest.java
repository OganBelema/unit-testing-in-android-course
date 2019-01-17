package com.techyourchance.unittestingfundamentals.exercise1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class NegativeNumberValidatorTest {

    private NegativeNumberValidator SUT;

    @Before
    public void setup(){
        SUT = new NegativeNumberValidator();
    }

    @Test
    public void negativeNumberTest(){
        boolean response = SUT.isNegative(-1);
        Assert.assertThat(response, is(true));
    }

    @Test
    public void whenNumberIsZeroTest(){
        boolean response = SUT.isNegative(0);
        Assert.assertThat(response, is(false));
    }

    @Test
    public void positiveNumberTest(){
        boolean response = SUT.isNegative(1);
        Assert.assertThat(response, is(false));
    }
}