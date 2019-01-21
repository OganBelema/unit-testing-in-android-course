package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class IntervalsAdjacencyDetectorTest {

    IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }


    @Test
    public void isAdjacent_Interval1BeforeAndAdjacent_returnTrue() throws Exception {
        Interval interval1 = new Interval(-3, 5);
        Interval interval2 = new Interval(5, 9);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(true));
    }

    @Test
    public void isAdjacent_Interval1AfterAndAdjacent_returnTrue() throws Exception {
        Interval interval1 = new Interval(7, 10);
        Interval interval2 = new Interval(3, 7);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(true));
    }

    @Test
    public void isAdjacent_Interval1BeforeInterval2_returnFalse() throws Exception {
        Interval interval1 = new Interval(-1, 4);
        Interval interval2 = new Interval(6, 9);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_Interval1AfterInterval2_returnFalse() throws Exception {
        Interval interval1 = new Interval(6, 10);
        Interval interval2 = new Interval(3, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_Interval1OverlapsInterval2OnStart_returnFalse() throws Exception {
        Interval interval1 = new Interval(2, 10);
        Interval interval2 = new Interval(5, 10);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_Interval1OverlapsInterval2OnEnd_returnFalse() throws Exception {
        Interval interval1 = new Interval(3, 7);
        Interval interval2 = new Interval(3, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void isAdjacent_Interval1ContainsInterval2_returnFalse() throws Exception {
        Interval interval1 = new Interval(2, 8);
        Interval interval2 = new Interval(4, 7);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }
}