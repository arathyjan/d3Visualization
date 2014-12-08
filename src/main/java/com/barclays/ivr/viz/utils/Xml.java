package com.barclays.ivr.viz.utils;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Xml {

    private Xml() {
    }

    public static Element toXmlElement(String xml) {
        final ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
        return toXmlElement(in);
    }

    public static Element toXmlElement(InputStream in) {
        try {
            return new SAXBuilder().build(in).getRootElement();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read inputstream. Make sure that it exists.", e);
        }
    }
}

