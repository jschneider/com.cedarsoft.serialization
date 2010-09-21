package com.cedarsoft.serialization.json;

import com.cedarsoft.Version;
import com.cedarsoft.license.License;
import com.cedarsoft.serialization.AbstractJsonVersionTest2;
import com.cedarsoft.serialization.Serializer;
import com.cedarsoft.serialization.VersionEntry;
import org.junit.Assert;
import org.junit.experimental.theories.DataPoint;

public class LicenseSerializerVersionTest
    extends AbstractJsonVersionTest2 <License>
{

    @DataPoint
    public final static VersionEntry ENTRY1 = LicenseSerializerVersionTest.create(Version.valueOf(1, 0, 0), LicenseSerializerVersionTest.class.getResource("License_1.0.0_1.json"));

    @Override
    protected Serializer<License> getSerializer()
        throws Exception
    {
        return new LicenseSerializer();
    }

    @Override
    protected void verifyDeserialized(License deserialized, Version version)
        throws Exception
    {
        Assert.assertEquals("daValue", deserialized.getId());
        Assert.assertEquals("daValue", deserialized.getName());
        Assert.assertEquals("daValue", deserialized.getUrl());
    }

}
