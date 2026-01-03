package com.wlqq.phantom.library.utils;


import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class FileUtilsTest {
    @Test
    public void testCalculateMd5ForInputStream() throws Exception {
        Assert.assertEquals("2a66a876a40630539e220890dc897550",
                FileUtils.calculateMd5(getInputStream("com.wlqq.phantom.plugin.sample1_1.0.0.apk")));
    }

    private InputStream getInputStream(String filename) throws IOException {
        return getClass().getClassLoader().getResourceAsStream(filename);
    }
}
