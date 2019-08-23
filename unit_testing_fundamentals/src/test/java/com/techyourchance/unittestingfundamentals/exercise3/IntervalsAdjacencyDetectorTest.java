package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntervalsAdjacencyDetectorTest {

    private IntervalsAdjacencyDetector sut;

    @Before
    public void setUp() {
        sut = new IntervalsAdjacencyDetector();
    }

    @Test
    public void isAdjacent_interval1BeforeInterval2_falseReturned() {

        Interval interval1 = new Interval(1,5);
        Interval interval2 = new Interval(7,10);

        assertThat(sut.isAdjacent(interval1, interval2), is(false));
    }

    @Test
    public void isAdjacent_interval1OverlapsStartInterval2_falseReturned() {

        Interval interval1 = new Interval(1,5);
        Interval interval2 = new Interval(4,10);

        assertThat(sut.isAdjacent(interval1, interval2), is(false));
    }

    @Test
    public void isAdjacent_interval1IsContainedInInterval2_falseReturned() {

        Interval interval1 = new Interval(1,5);
        Interval interval2 = new Interval(7,10);

        assertThat(sut.isAdjacent(interval1, interval2), is(false));
    }

    @Test
    public void isAdjacent_interval1OverlapsEndInterval2_falseReturned() {

        Interval interval1 = new Interval(8,10);
        Interval interval2 = new Interval(3,9);

        assertThat(sut.isAdjacent(interval1, interval2), is(false));
    }

    @Test
    public void isAdjacent_interval1ContainsInInterval2_falseReturned() {

        Interval interval1 = new Interval(2,9);
        Interval interval2 = new Interval(5,6);

        assertThat(sut.isAdjacent(interval1, interval2), is(false));
    }

    @Test
    public void isAdjacent_interval1AfterInterval2_falseReturned() {

        Interval interval1 = new Interval(8,13);
        Interval interval2 = new Interval(2,5);

        assertThat(sut.isAdjacent(interval1, interval2), is(false));
    }

    @Test
    public void isAdjacent_interval1AdjacentStartInterval2_trueReturned() {

        Interval interval1 = new Interval(1,5);
        Interval interval2 = new Interval(5,10);

        assertThat(sut.isAdjacent(interval1, interval2), is(true));
    }

    @Test
    public void isAdjacent_interval1AdjacentEndInterval2_trueReturned() {

        Interval interval1 = new Interval(10,15);
        Interval interval2 = new Interval(7,10);

        assertThat(sut.isAdjacent(interval1, interval2), is(true));
    }

    @Test
    public void isAdjacent_interval1SameInterval2_tfalseReturned() {

        Interval interval1 = new Interval(11,15);
        Interval interval2 = new Interval(11,15);

        assertThat(sut.isAdjacent(interval1, interval2), is(false));
    }
}