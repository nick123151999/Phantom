package com.wlqq.phantom.library.utils;

import junit.framework.Assert;

import org.junit.Test;


public class TimingUtilsTest {

    @Test
    public void normalizeDuration_isCorrect() throws Exception {
        Assert.assertEquals("<=50",
                TimingUtils.normalizeDuration(49, TimingUtils.SECTION_DURATION_50_MS, TimingUtils.MAX_SECTION_20));
    }

    @Test
    public void normalizeDuration_isCorrect_1() throws Exception {
        Assert.assertEquals("<=50",
                TimingUtils.normalizeDuration(50, TimingUtils.SECTION_DURATION_50_MS, TimingUtils.MAX_SECTION_20));
    }

    @Test
    public void normalizeDuration_isCorrect_2() throws Exception {
        Assert.assertEquals("<=100",
                TimingUtils.normalizeDuration(51, TimingUtils.SECTION_DURATION_50_MS, TimingUtils.MAX_SECTION_20));
    }

    @Test
    public void normalizeDuration_isCorrect_3() throws Exception {
        Assert.assertEquals("<=200",
                TimingUtils.normalizeDuration(199, TimingUtils.SECTION_DURATION_50_MS, TimingUtils.MAX_SECTION_20));
    }

    @Test
    public void normalizeDuration_isCorrect_4() throws Exception {
        Assert.assertEquals(">1000",
                TimingUtils.normalizeDuration(1001, TimingUtils.SECTION_DURATION_50_MS, TimingUtils.MAX_SECTION_20));
    }

    @Test
    public void normalizeDuration_isCorrect_5() throws Exception {
        Assert.assertEquals(">1000",
                TimingUtils.normalizeDuration(1001, TimingUtils.SECTION_DURATION_50_MS, TimingUtils.MAX_SECTION_20));
    }
}
