package com.techyourchance.unittestingfundamentals.exercise1;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class NegativeNumberValidatorTest {

    private NegativeNumberValidator sut;

    @Before
    public void init() {
        sut = new NegativeNumberValidator();
    }

    @Test
    public void isNegativeTest1() {
        Assert.assertThat(sut.isNegative(1), is(false));
    }

    @Test
    public void isNegativeTest2() {
        Assert.assertThat(sut.isNegative(0), is(false));
    }

    @Test
    public void isNegativeTest3() {
        Assert.assertThat(sut.isNegative(-1), is(true));
    }


}