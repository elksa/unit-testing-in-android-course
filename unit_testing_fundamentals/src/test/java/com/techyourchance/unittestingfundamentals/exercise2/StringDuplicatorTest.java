package com.techyourchance.unittestingfundamentals.exercise2;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class StringDuplicatorTest {

    private StringDuplicator sut;

    @Before
    public void setUp() {
        sut = new StringDuplicator();
    }

    @Test
    public void duplicate_emptyString_emptyStringReturned() {
        assertThat(sut.duplicate(""), is(""));
    }

    @Test
    public void duplicate_singleCharacter_duplicatedStringreturned() {
        assertThat(sut.duplicate("a"), is("aa"));
    }

    @Test
    public void duplicate_longString_duplicatedStringreturned() {
        assertThat(sut.duplicate("hello elksa"), is("hello elksahello elksa"));
    }
}