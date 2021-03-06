package com.yeahmobi.yscheduler.storage;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.yeahmobi.yscheduler.storage.service.FileService;

/**
 * @author abel.cui
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-test.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class })
public class DefaultFileServiceImplTest {

    @Autowired
    private FileService fileService;

    @Test
    public void testStore() throws Exception {
        String key = "temp";
        String nameSpace = "task";
        @SuppressWarnings("static-access")
        final InputStream is = this.getClass().getClassLoader().getSystemResourceAsStream("test.sh");
        MockMultipartFile fileUpload = new MockMultipartFile("test.sh", "test.sh", null, is);
        long actual = this.fileService.store(new FileKey(nameSpace, key),
                                             new FileEntry(fileUpload.getOriginalFilename(),
                                                           fileUpload.getInputStream()));
        Assert.assertTrue(((System.currentTimeMillis() - actual) < 1000) && ((System.currentTimeMillis() - actual) > 0));
    }

    @Test
    public void testGet() throws Exception {
        String key = "temp";
        String nameSpace = "task";
        @SuppressWarnings({ "static-access" })
        final InputStream is = this.getClass().getClassLoader().getSystemResourceAsStream("test.sh");
        long version = 0;
        FileEntry actual = this.fileService.get(new FileKey(nameSpace, key), version);
        Assert.assertEquals("test.sh", actual.fileName);
        Assert.assertEquals(is.available(), actual.getInputStream().available());
    }
}
