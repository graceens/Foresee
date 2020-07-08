package com.grace.foresee;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.grace.foresee.utils.DeviceUtil;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DeviceUtilTest {
    private Context mContext;

    @Before
    public void before() {
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void imeiTest() {
        String imei = DeviceUtil.getImei(mContext);
        Assert.assertThat(imei, Matchers.not(""));
    }

    @Test
    public void phoneNumberTest() {
        String phoneNumber = DeviceUtil.getPhoneNumber(mContext);
        Assert.assertThat(phoneNumber, Matchers.notNullValue());
    }

    @Test
    public void systemVersionTest() {
        String systemVersion = DeviceUtil.getSystemVersion();
        Assert.assertThat(systemVersion, Matchers.notNullValue());
    }

    @Test
    public void phoneModelTest() {
        String phoneModel = DeviceUtil.getPhoneModel();
        Assert.assertThat(phoneModel, Matchers.notNullValue());
    }

    @Test
    public void networkConnectedTest() {
        boolean connected = DeviceUtil.isNetworkConnected(mContext);
        Assert.assertThat(connected, Matchers.is(true));
    }

    @Test
    public void mobileNetworkTest() {
        boolean isMobile = DeviceUtil.isMobile(mContext);
        Assert.assertThat(isMobile, Matchers.is(false));
    }

    @Test
    public void wifiNetworkTest() {
        boolean isWifi = DeviceUtil.isWifi(mContext);
        Assert.assertThat(isWifi, Matchers.is(true));
    }

    @Test
    public void macTest() {
        String mac = DeviceUtil.getMac(mContext);
        Assert.assertNotEquals(mac, Matchers.isEmptyOrNullString());
    }
}
