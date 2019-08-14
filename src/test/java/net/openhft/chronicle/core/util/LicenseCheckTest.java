/*
 * Copyright (c) 2016-2019 Chronicle Software Ltd
 */

package net.openhft.chronicle.core.util;

import junit.framework.TestCase;
import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.onoes.ExceptionKey;
import org.junit.After;
import org.junit.Test;

import javax.naming.TimeLimitExceededException;
import java.util.Map;

import static org.junit.Assert.*;

public class LicenseCheckTest {

    @After
    public void tearDown() {
        Jvm.resetExceptionHandlers();
    }

    @Test
    public void checkEval() {
        Map<ExceptionKey, Integer> map = Jvm.recordExceptions();
        // Evaluation license
        LicenseCheck.check("test", getClass());
        assertEquals("{ExceptionKey{level=WARN, clazz=class net.openhft.chronicle.core.util.LicenseCheck, message='Evaluation version expires in 92 days', throwable=}=1}", map.toString());
    }

    @Test
    public void checkEvalExpired() {
        Map<ExceptionKey, Integer> map = Jvm.recordExceptions();
        // Evaluation license
        try {
            LicenseCheck.check("test", TestCase.class);
            fail();
        } catch (AssertionError e) {
            assertEquals(TimeLimitExceededException.class, e.getCause().getClass());
        }
        assertTrue(map.toString().contains("Evaluation version expires in -"));
    }

    @Test
    public void checkLicense() {
        System.setProperty("test.lic", "product=test,owner=Test Unit,expires=9999-01-01,code=123456789");

        Map<ExceptionKey, Integer> map = Jvm.recordExceptions();
        // licensed
        LicenseCheck.check("test", getClass());
        assertTrue(map.toString().contains("License for Test Unit expires in 29"));
    }

    @Test
    public void checkLicenseExpired() {
        System.setProperty("test.lic", "product=test,owner=Test Unit,expires=2019-01-01,code=123456789");

        Map<ExceptionKey, Integer> map = Jvm.recordExceptions();
        // licensed
        try {
            LicenseCheck.check("test", getClass());
            fail();
        } catch (AssertionError e) {
            assertEquals(TimeLimitExceededException.class, e.getCause().getClass());
        }
        assertTrue(map.toString().contains("License for Test Unit expires in -"));
    }
}