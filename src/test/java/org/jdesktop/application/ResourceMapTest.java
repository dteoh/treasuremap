/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */

package org.jdesktop.application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import junit.framework.TestCase;

import org.jdesktop.application.ResourceConverter.ResourceConverterException;

/*
 * ResourceMapTest.java - JUnit based test
 *
 * This test depends on ResourceBundles and an image file:
 * <pre>
 * resources/Basic.properties
 * resources/Parent.properties
 * resources/Child.properties
 * resources/Injection.properties
 * resources/black1x1.png
 * resources/ExprEval.properties
 * resources/Basic_zz.properties
 * resources/Basic_WindowsXP.properties
 * resources/Basic_WindowsXP_zz.properties
 * </pre>
 *
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */
public class ResourceMapTest extends TestCase {

    public ResourceMapTest(final String testName) {
        super(testName);
    }

    public void testSupportedResourceTypes() {
        Class[] supportedTypes = { Boolean.class, boolean.class, Integer.class,
                int.class, Long.class, long.class, Short.class, short.class,
                Byte.class, byte.class, Float.class, float.class, Double.class,
                double.class, Icon.class, ImageIcon.class, Color.class,
                Font.class, KeyStroke.class, MessageFormat.class, Point.class,
                Dimension.class, Rectangle.class, Insets.class,
                EmptyBorder.class, URL.class, URI.class };
        ClassLoader classLoader = getClass().getClassLoader();
        ResourceMap rm = new ResourceMap(null, classLoader, "noBundle");
        for (Class type : supportedTypes) {
            String msg = "default supported ResourceConverter type: "
                    + type.getName();
            assertNotNull(msg, ResourceConverter.forType(type));
        }
    }

    private static class TestColor extends Color {
        public final String key;

        public TestColor(final String key, final int r, final int g, final int b) {
            super(r, g, b);
            this.key = key;
        }

        public TestColor(final String key, final int a, final int r,
                final int g, final int b) {
            super(r, g, b, a);
            this.key = key;
        }
    }

    private void checkBlack1x1Icon(final String accessPathString,
            final Icon icon) {
        assertNotNull(accessPathString, icon);
        assertEquals(accessPathString + ".getIconWidth()", icon.getIconWidth(),
                1);
        assertEquals(accessPathString + ".getIconHeight()",
                icon.getIconHeight(), 1);
    }

    private ResourceMap basicResourceMap() {
        String bundleBaseName = getClass().getPackage().getName()
                + ".resources.Basic";
        /*
         * If the ResourceBundle can't be found, getBundle() will throw an
         * exception. ResourceMap isn't supposed to complain if it can't find a
         * ResourceBundle however the tests that follow expect Basic
         * ResourceBundle to exist.
         */
        ResourceBundle.getBundle(bundleBaseName);
        ClassLoader classLoader = getClass().getClassLoader();
        ResourceMap resourceMap = new ResourceMap(null, classLoader,
                bundleBaseName);
        return resourceMap;
    }

    private void checkBasicResourceMap(final ResourceMap rm) {
        assertFalse("containsKey(\"noSuchResource\")",
                rm.containsKey("noSuchResource"));
        assertNull("getObject(\"noSuchResource\")",
                rm.getObject("noSuchResource", Object.class));

        String aStringResource = "aStringResource";
        assertTrue("containsKey(\"aStringResource\")",
                rm.containsKey(aStringResource));
        assertEquals("getString(\"aStringResource\")", aStringResource,
                rm.getString(aStringResource));
        String testString = (String) (rm.getObject(aStringResource,
                String.class));
        assertEquals("getObject(\"aStringResource\")", aStringResource,
                testString);

        String aHelloMessage = "aHelloMessage";
        assertEquals("getString(\"aHelloMessage\")", "Hello World",
                rm.getString("aHelloMessage", "World"));

        String integer123 = "integer123";
        String short123 = "short123";
        String long123 = "long123";
        String byte123 = "byte123";
        String float123 = "float123";
        String double123 = "double123";
        assertEquals("getInteger(\"" + integer123 + "\")",
                rm.getInteger(integer123).intValue(), 123);
        assertEquals("getShort(\"" + short123 + "\")", rm.getShort(short123)
                .shortValue(), 123);
        assertEquals("getLong(\"" + long123 + "\")", rm.getLong(long123)
                .longValue(), 123L);
        assertEquals("getByte(\"" + byte123 + "\")", rm.getByte(byte123)
                .byteValue(), 123);
        assertEquals("getFloat(\"" + float123 + "\")", rm.getFloat(float123)
                .floatValue(), 123.0f);
        assertEquals("getDouble(\"" + double123 + "\")", rm
                .getDouble(double123).doubleValue(), 123.0);

        String integer0 = "integer0";
        String short0 = "short0";
        String long0 = "long0";
        String byte0 = "byte0";
        assertEquals("getInteger(\"" + integer0 + "\")", rm
                .getInteger(integer0).intValue(), 0);
        assertEquals("getShort(\"" + short0 + "\")", rm.getShort(short0)
                .shortValue(), 0);
        assertEquals("getLong(\"" + long0 + "\")", rm.getLong(long0)
                .longValue(), 0L);
        assertEquals("getByte(\"" + byte0 + "\")", rm.getByte(byte0)
                .byteValue(), 0);

        String integerNegative1 = "integerNegative1";
        String shortNegative1 = "shortNegative1";
        String longNegative1 = "longNegative1";
        String byteNegative1 = "byteNegative1";
        assertEquals("getInteger(\"" + integerNegative1 + "\")",
                rm.getInteger(integerNegative1).intValue(), -1);
        assertEquals("getShort(\"" + shortNegative1 + "\")",
                rm.getShort(shortNegative1).shortValue(), -1);
        assertEquals("getLong(\"" + longNegative1 + "\")",
                rm.getLong(longNegative1).longValue(), -1L);
        assertEquals("getByte(\"" + byteNegative1 + "\")",
                rm.getByte(byteNegative1).byteValue(), -1);

        try {
            rm.getInteger("badlyFormattedInteger");
            fail("rm.getInteger(\"badlyFormattedInteger\") expected throw");
        } catch (ResourceMap.LookupException e) {
        }

        /*
         * ResourceConverterException has a little bit of logic to limit limit
         * the length of the message string. Check the output log (also checking
         * programatically here).
         */
        String badlyFormattedGiantInteger = "badlyFormattedGiantInteger";
        try {
            rm.getInteger(badlyFormattedGiantInteger);
            fail("rm.getString(\"" + badlyFormattedGiantInteger
                    + "\") expected throw");
        } catch (ResourceMap.LookupException e) {
        }
        ResourceConverter sc = ResourceConverter.forType(Integer.class);
        String badGiantInteger = rm.getString(badlyFormattedGiantInteger);
        try {
            sc.parseString(badGiantInteger, rm);
        } catch (ResourceConverterException e) {
            boolean f = e.getMessage().length() < badGiantInteger.length();
            assertTrue(
                    "ResourceConverterException message should be truncated", f);
        }

        String[] trueKeys = { "booleanTrue", "booleanTRUE", "booleanYes",
                "booleanOn" };
        for (String key : trueKeys) {
            assertTrue("getBoolean(\"" + key + "\")", rm.getBoolean(key));
        }

        String[] falseKeys = { "booleanFalse", "booleanFALSE", "booleanNo",
                "booleanOff" };
        for (String key : falseKeys) {
            assertFalse("getBoolean(\"" + key + "\")", rm.getBoolean(key));
        }

        // Color formats are: #RRGGBB",  "#AARRGGBB", "R, G, B", "R, G, B, A"
        TestColor[] testColors = {
                new TestColor("color123", 1, 2, 3),
                new TestColor("color345", 3, 4, 5),
                new TestColor("color567", 5, 6, 7),
                new TestColor("color5678", 8, 5, 6, 7), // TestColor(a, r, g, b)
                new TestColor("color556677", 0x55, 0x66, 0x77),
                new TestColor("color55667788", 0x55, 0x66, 0x77, 0x88) };
        for (TestColor testColor : testColors) {
            Color c1 = rm.getColor(testColor.key);
            Color c2 = rm.getColor(testColor.key);
            assertEquals("getColor(\"" + testColor.key + "\") c1", c1,
                    testColor);
            assertEquals("getColor(\"" + testColor.key + "\") c2", c2,
                    testColor);
            assertTrue("getColor(\"" + testColor.key + "\") c1==c2", c2 == c2);
        }

        /*
         * Verify that looking up an integer resource that names a Color
         * resourced that has already been loaded triggers a LookupException.
         */
        Color c123 = rm.getColor("color123");
        assertEquals("unexpected failure", c123, new Color(1, 2, 3));
        try {
            rm.getInteger("color123");
            fail("rm.getInteger(\"color123\") expected throw");
        } catch (ResourceMap.LookupException e) {
        }

        String fontArialPLAIN12 = "fontArialPLAIN12";
        Font f1 = rm.getFont(fontArialPLAIN12);
        Font f2 = rm.getFont(fontArialPLAIN12);
        assertNotNull("getFont(\"" + fontArialPLAIN12 + "\")", f1);
        assertEquals("getFont(\"" + fontArialPLAIN12 + "\") f1.equals(f2)", f1,
                f2);
        // Note that ResourceMap is not required to cache values, so it's not
        // necessarily true that f1==f2

        ImageIcon imageIcon = rm.getImageIcon("black1x1Icon");
        checkBlack1x1Icon("getImageIcon(\"black1x1Icon\")", imageIcon);

        Icon icon = rm.getIcon("black1x1Icon");
        checkBlack1x1Icon("getIcon(\"black1x1Icon\")", icon);

        // Verify that absolute pathnames work
        ImageIcon absIcon = rm.getImageIcon("AbsoluteBlack1x1Icon");
        checkBlack1x1Icon("getImageIcon(\"AbsoluteBlack1x1Icon\")", absIcon);

        // Check the support for Point, Dimension, Rectangle, Insets,
        // EmptyBorder
        {
            String[] resourceKeys = { "point12", "pointMinus12", "dimension56",
                    "dimension89", "rectangle1234", "rectangleMinus5309",
                    "insets7890", "emptyBorder8675", "emptyBorder0000" };
            Object[] resourceValues = { new Point(1, 2), new Point(-1, -2),
                    new Dimension(5, 6), new Dimension(8, 9),
                    new Rectangle(1, 2, 3, 4), new Rectangle(-5, -3, 0, 9),
                    new Insets(7, 8, 9, 0), new EmptyBorder(8, 6, 7, 5),
                    new EmptyBorder(0, 0, 0, 0) };
            for (int i = 0; i < resourceKeys.length; i++) {
                String key = resourceKeys[i];
                Object expectedValue = resourceValues[i];
                Class type = expectedValue.getClass();
                String msg = String.format("get%s(\"%s\")",
                        type.getSimpleName(), key);
                if (EmptyBorder.class.isAssignableFrom(type)) {
                    EmptyBorder border = (EmptyBorder) rm.getObject(key, type);
                    EmptyBorder expectedBorder = (EmptyBorder) expectedValue;
                    assertEquals(msg, expectedBorder.getBorderInsets(),
                            border.getBorderInsets());
                } else {
                    assertEquals(msg, expectedValue, rm.getObject(key, type));
                }
            }
        }

        // Check the support for URLs
        URL url = null;
        try {
            url = new URL("http://www.sun.com");
        } catch (MalformedURLException e) {
            throw new Error(e);
        }
        assertEquals(url, rm.getObject("sunURL", URL.class));

        // Check the support for URIs
        URI uri = null;
        try {
            uri = new URI("mailto:users@appframework.dev.java.net");
        } catch (URISyntaxException e) {
            throw new Error(e);
        }
        assertEquals(uri, rm.getObject("mailURI", URI.class));
    }

    /**
     * Verify that the we can load a ResourceBundle and get/cache all its
     * resources through a ResourceMap.
     */
    public void testBasics() {
        checkBasicResourceMap(basicResourceMap());
    }

    /**
     * Verify that an empty ResourceMap whose parent is basicResourcMap looks
     * the same as (just) basicResourceMap.
     */
    public void testParentResourceMap() {
        ClassLoader classLoader = getClass().getClassLoader();
        checkBasicResourceMap(new ResourceMap(basicResourceMap(), classLoader,
                "noSuchBundle"));
    }

    public void testMultipleBundleNames() {
        String b2 = getClass().getPackage().getName()
                + ".resources.MultiBundle";
        String b1 = b2 + "_AllLocales";
        String b0 = b2 + "_WindowsXP";
        ClassLoader classLoader = getClass().getClassLoader();
        ResourceMap resourceMap = new ResourceMap(null, classLoader, b0, b1, b2);
        assertEquals(3, resourceMap.getBundleNames().size());
        assertEquals(b0, resourceMap.getBundleNames().get(0));
        assertEquals(b1, resourceMap.getBundleNames().get(1));
        assertEquals(b2, resourceMap.getBundleNames().get(2));
        assertEquals("OK", resourceMap.getString("noOverride"));
        assertEquals("AllLocales OK",
                resourceMap.getString("AllLocales_override"));
        assertEquals("WindowsXP OK",
                resourceMap.getString("WindowsXP_override"));
        assertEquals("XP", resourceMap.getString("WindowsXP_only"));
        assertEquals("AllLocales", resourceMap.getString("AllLocales_only"));
    }

    private static class TestType {
        public final String value;

        TestType(final String value) {
            this.value = value;
        }
    }

    private static class TestResourceConverter extends ResourceConverter {
        TestResourceConverter() {
            super(TestType.class);
        }

        @Override
        public Object parseString(final String s, final ResourceMap ignore) {
            return new TestType(s);
        }
    }

    public void testRegisterResourceConverter() {
        ResourceMap rm = basicResourceMap();
        ResourceConverter.register(new TestResourceConverter());
        assertNotNull(ResourceConverter.forType(TestType.class));
        String testAddResourceConverter = "testAddResourceConverter";
        assertEquals(testAddResourceConverter,
                rm.getString(testAddResourceConverter));
        Object tt = rm.getObject(testAddResourceConverter, TestType.class);
        assertNotNull("getString(\"" + testAddResourceConverter + "\")", tt);
        Boolean b = ((TestType) tt).value.equals(testAddResourceConverter);
        assertTrue("getString(\"" + testAddResourceConverter + "\").value", b);
    }

    private ResourceMap childResourceMap() {
        String bundleBaseName = getClass().getPackage().getName()
                + ".resources.Child";
        ResourceBundle.getBundle(bundleBaseName);
        ClassLoader classLoader = getClass().getClassLoader();
        return new ResourceMap(null, classLoader, bundleBaseName);
    }

    public void testKeySetBasics() {
        Set<String> keys = childResourceMap().keySet();
        assertNotNull("childResourceMap.keySet()", keys);
        String[] expectedKeys = { "p1", "p2", "p3", "p4", "p5" };
        assertEquals("childResourceMap().keySet().size()", expectedKeys.length,
                keys.size());
        // TBD check that childResourceMap is read-only
        Set<String> shouldBeEmpty = new HashSet(keys);
        assertTrue("Should remove all keys",
                shouldBeEmpty.removeAll(Arrays.asList(expectedKeys)));
        assertEquals("Should (now) be an empty set", shouldBeEmpty.size(), 0);
        // TBD check that the keys iterator works
        // and check again with empty resourcemap with child as parent
    }

    private ResourceMap parentChildResourceMap() {
        String bundleBaseName = getClass().getPackage().getName()
                + ".resources.Parent";
        ResourceBundle.getBundle(bundleBaseName);
        ClassLoader classLoader = getClass().getClassLoader();
        return new ResourceMap(childResourceMap(), classLoader, bundleBaseName);
    }

    public void testKeySetParentChild() {
        Set<String> keys = parentChildResourceMap().keySet();
        assertNotNull("parentChildResourceMap.keySet()", keys);
        String[] expectedKeys = { "p1", "p2", "p3", "p4", "p5", "p6", "p7",
                "p8", "p9", "p10" };
        assertEquals("parentChildResourceMap().keySet().size()",
                expectedKeys.length, keys.size());
        Set<String> shouldBeEmpty = new HashSet(keys);
        assertTrue("Should remove all keys",
                shouldBeEmpty.removeAll(Arrays.asList(expectedKeys)));
        assertEquals("Should (now) be an empty set", shouldBeEmpty.size(), 0);
    }

    private ResourceMap injectionResourceMap() {
        String bundleBaseName = getClass().getPackage().getName()
                + ".resources.Injection";
        ResourceBundle.getBundle(bundleBaseName);
        ClassLoader classLoader = getClass().getClassLoader();
        ResourceMap resourceMap = new ResourceMap(null, classLoader,
                bundleBaseName);
        return resourceMap;
    }

    public void testInjectComponentProperties() {
        JLabel label = new JLabel();
        label.setName("testLabel");
        ResourceMap rm = injectionResourceMap();
        rm.injectComponent(label);
        assertEquals("label.getText()", "Hello World", label.getText());
        assertEquals("label.getAlignmentX()", 0.5f, label.getAlignmentX());
        assertFalse("label.getEnabled()", label.isEnabled());
        Color expectedColor = new Color(0x55, 0x66, 0x77);
        assertEquals("label.getBackground()", expectedColor,
                label.getBackground());
        Font font = label.getFont();
        assertNotNull("label.getFont()", font);
        Icon icon = label.getIcon();
        assertNotNull("label.getIcon()", icon);
        assertEquals("label.getIcon().getIconWidth()", 1, icon.getIconWidth());
        assertEquals("label.getIcon().getIconHeight()", 1, icon.getIconHeight());
        assertEquals("label.getDisplayedMnemonicIndex()", 3,
                label.getDisplayedMnemonicIndex());
        JLabel labelNullText = new JLabel("Hello World");
        labelNullText.setName("labelNullText");
        assertNotNull(labelNullText.getText());
        rm.injectComponent(labelNullText);
        assertNull(labelNullText.getText());
    }

    public void testInjectComponentHierarchyProperties() {
        JFrame mainFrame = new JFrame();
        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu();
        JMenuItem item = new JMenuItem();
        JPanel parentPanel = new JPanel();
        JTextField textField1 = new JTextField();
        JTextField textField2 = new JTextField();
        JPanel childPanel = new JPanel();
        JLabel mnemonicLabel1 = new JLabel();
        JLabel mnemonicLabel2 = new JLabel();
        JButton button = new JButton();
        mainFrame.setName("mainFrame");
        menuBar.setName("menuBar");
        menu.setName("Edit.menu");
        item.setName("item");
        parentPanel.setName("parentPanel");
        childPanel.setName("childPanel");
        textField1.setName("textField1");
        textField2.setName("textField2");
        mnemonicLabel1.setName("mnemonicLabel1");
        mnemonicLabel2.setName("mnemonicLabel2");
        button.setName("button");
        mainFrame.add(parentPanel);
        mainFrame.setJMenuBar(menuBar);
        menuBar.add(menu);
        menu.add(item);
        parentPanel.add(childPanel);
        parentPanel.add(textField1);
        parentPanel.add(mnemonicLabel1);
        childPanel.add(new JScrollPane(textField2));
        childPanel.add(mnemonicLabel2);
        childPanel.add(button);
        injectionResourceMap().injectComponents(mainFrame);
        assertEquals("mainFrame.getTitle()", "Frame title",
                mainFrame.getTitle());
        Image image = mainFrame.getIconImage();
        assertNotNull("mainFrame.getIconImage()", image);
        assertEquals("mainFrame.getIconImage().getWidth()",
                image.getWidth(null), 1);
        assertEquals("mainFrame.getIconImage().getHeight()",
                image.getHeight(null), 1);
        assertEquals("menu.getMnemonic()", 68, menu.getMnemonic());
        assertEquals("item.getText()", "Item text", item.getText());
        assertEquals("item.getMnemonic()", 69, item.getMnemonic());
        assertEquals("textField1.getText()", "textField1", textField1.getText());
        assertEquals("textField2.getText()", "textField2", textField2.getText());
        assertEquals("textField2.getBackground()", new Color(0, 0, 0),
                textField2.getBackground());
        Color parentBackground = new Color(0x55, 0x00, 0x00);
        Color parentForeground = new Color(0x00, 0x66, 0x00);
        Color childForeground = new Color(0x00, 0x00, 0x77);
        Color childBackground = new Color(0x00, 0x00, 0x00);
        assertEquals("parentPanel.getBackground()", parentBackground,
                parentPanel.getBackground());
        assertEquals("parentPanel.getForeground()", parentForeground,
                parentPanel.getForeground());
        assertEquals("childPanel.getBackground()", childBackground,
                childPanel.getBackground());
        assertEquals("childPanel.getForeground()", childForeground,
                childPanel.getForeground());
        assertEquals("mnemonic", mnemonicLabel1.getText());
        assertEquals("Save As", mnemonicLabel2.getText());
        assertEquals("Exit", button.getText());
        assertEquals(0, mnemonicLabel1.getDisplayedMnemonicIndex());
        assertEquals(5, mnemonicLabel2.getDisplayedMnemonicIndex());
        assertEquals(1, button.getDisplayedMnemonicIndex());
        assertEquals(KeyEvent.VK_M, mnemonicLabel1.getDisplayedMnemonic());
        assertEquals(KeyEvent.VK_A, mnemonicLabel2.getDisplayedMnemonic());
        assertEquals(KeyEvent.VK_X, button.getMnemonic());
    }

    private static class TestResourceAnnotation {
        @Resource
        private String stringField;
        @Resource
        protected int intField;
        @Resource
        public Color colorField;
        @Resource
        boolean booleanField;
        @Resource
        static String staticField;
        @Resource(key = "F1")
        private String stringF1Field;
        @Resource(key = "TestResourceAnnotation.objectField")
        Object objectField;
        @Resource
        int[] numbers = new int[12];
        @Resource
        Icon[] icons = new Icon[2];
        @Resource
        KeyStroke shortcutX;
        @Resource
        KeyStroke shortcutShiftX;
    }

    public void testResourceAnnotation() {
        ResourceMap rm = basicResourceMap();
        TestResourceAnnotation target = new TestResourceAnnotation();
        for (int i = 0; i < target.numbers.length; i++) {
            target.numbers[i] = -1;
        }
        rm.injectFields(target);
        assertEquals("@Resource private String stringField;", "stringField",
                target.stringField);
        assertEquals("@Resource protected int intField;", 123, target.intField);
        assertEquals("@Resource public Color colorField;", new Color(0, 1, 2),
                target.colorField);
        assertTrue("@Resource boolean booleanField;", target.booleanField);
        assertEquals("@Resource static String staticField;", "staticField",
                TestResourceAnnotation.staticField);
        assertEquals("@Resource(key=\"F1\") private String stringF1Field;",
                "stringF1Field", target.stringF1Field);
        assertEquals("@Resource(key=\"TestResourceAnnotation.objectField\")",
                "objectField", target.objectField);
        for (int i = 0; i < target.numbers.length; i++) {
            if ((i == 2) || (i == 3) || (i == 11)) {
                assertEquals("@Resource int[] numbers[" + i + "]", i,
                        target.numbers[i]);
            } else {
                assertEquals("@Resource int[] numbers[" + i + "]", -1,
                        target.numbers[i]);
            }
        }
        checkBlack1x1Icon("@Resource icons[0]", target.icons[0]);
        checkBlack1x1Icon("@Resrouce icons[1]", target.icons[1]);
        int k = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        String shortcut = (k == Event.META_MASK) ? "meta" : "control";
        assertEquals(KeyStroke.getKeyStroke(shortcut + " X"), target.shortcutX);
        assertEquals(KeyStroke.getKeyStroke(shortcut + " shift X"),
                target.shortcutShiftX);

    }

    private ResourceMap expressionEvaluationResourceMap() {
        String bundleBaseName = getClass().getPackage().getName()
                + ".resources.ExprEval";
        ResourceBundle.getBundle(bundleBaseName);
        ClassLoader classLoader = getClass().getClassLoader();
        ResourceMap resourceMap = new ResourceMap(null, classLoader,
                bundleBaseName);
        return resourceMap;
    }

    public void testExpressionEvaluation() {
        ResourceMap rm = expressionEvaluationResourceMap();
        assertEquals("rm.getString(hello)", "Hello", rm.getString("hello"));
        assertEquals("rm.getString(world)", "World", rm.getString("world"));
        assertEquals("rm.getString(place)", "World", rm.getString("place"));
        String hws = "Hello World";
        assertEquals("rm.getString(helloworld0)", hws,
                rm.getString("helloworld0"));
        assertEquals("rm.getString(helloworld1)", hws,
                rm.getString("helloworld1"));
        assertEquals("rm.getString(helloworld2)", hws,
                rm.getString("helloworld2"));
        assertEquals("rm.getString(helloworld3)", hws,
                rm.getString("helloworld3"));
        assertEquals("rm.getString(escHelloWorld)", "${hello} ${world}",
                rm.getString("escHelloWorld"));
        assertEquals("rm.getString(escOnly)", "${", rm.getString("escOnly"));
        try {
            rm.getString("noSuchVariableKey");
            fail("rm.getString(\"noSuchVariableKey\") expected throw");
        } catch (ResourceMap.LookupException e) {
        }
        try {
            rm.getString("noClosingBrace");
            fail("rm.getString(\"noClosingBrace\") expected throw");
        } catch (ResourceMap.LookupException e) {
        }
        assertNull("getString(\"justNull\")", rm.getString("justNull"));
        assertTrue("containsKey(\"justNull\")", rm.containsKey("justNull"));
    }

    public void testResourceMapSubclass() {
        final HashMap<String, Object> myMap = new HashMap<String, Object>();
        myMap.put("hello", "hello");
        myMap.put("world", "world");
        myMap.put("hello world", "${hello} ${world}");
        myMap.put("HelloX", "Hello %s");
        ResourceMap rm = new ResourceMap(null, getClass().getClassLoader(),
                "no bundles") {
            @Override
            protected Set<String> getResourceKeySet() {
                return myMap.keySet();
            }

            @Override
            protected boolean containsResourceKey(final String key) {
                return myMap.containsKey(key);
            }

            @Override
            protected Object getResource(final String key) {
                return myMap.get(key);
            }

            @Override
            protected void putResource(final String key, final Object value) {
                myMap.put(key, value);
            }
        };
        assertTrue(rm.containsKey("hello"));
        assertTrue(rm.containsKey("world"));
        assertTrue(rm.containsKey("hello world"));
        assertEquals("hello", rm.getString("hello"));
        assertEquals("world", rm.getString("world"));
        assertEquals("hello world", rm.getString("hello world"));
        assertEquals("Hello World", rm.getString("HelloX", "World"));
    }

    private ResourceMap platformResourceMap() { // see basicResourceMap
        String bundleBaseName = getClass().getPackage().getName()
                + ".resources.Basic";
        String osBundleName = bundleBaseName + "_WindowsXP";
        /*
         * If the ResourceBundle can't be found, getBundle() will throw an
         * exception. ResourceMap isn't supposed to complain if it can't find a
         * ResourceBundle however the tests that follow expect Basic
         * ResourceBundle to exist.
         */
        ResourceBundle.getBundle(bundleBaseName);
        ResourceBundle.getBundle(osBundleName);
        ClassLoader classLoader = getClass().getClassLoader();
        ResourceMap resourceMap = new ResourceMap(null, classLoader,
                osBundleName, bundleBaseName);
        return resourceMap;
    }

    public void testPlatformResourceMap() {
        ResourceMap rm = platformResourceMap();
        checkBasicResourceMap(rm);
        assertEquals("notPlatformSpecific", rm.getString("notPlatformSpecific"));
        assertEquals("platformSpecific", rm.getString("platformSpecific"));
    }

    public void testLocaleResource() {
        Locale oldLocale = Locale.getDefault();
        Locale.setDefault(new Locale("zz"));
        ResourceMap rm = platformResourceMap();
        assertEquals("notLocalized", rm.getString("notLocalized"));
        assertEquals("zzLocalized", rm.getString("zzLocalized"));
        assertEquals("zzAndPlatformSpecific",
                rm.getString("zzAndPlatformSpecific"));
        Locale.setDefault(oldLocale);
    }

    /**
     * Test for Issue #27 - Resource Injection fails on cached primitive types
     * https://appframework.dev.java.net/issues/show_bug.cgi?id=27
     */
    public void testCachedPrimitiveResource() {
        ResourceMap rm = basicResourceMap();
        String integer123 = "integer123";
        String short123 = "short123";
        String long123 = "long123";
        String byte123 = "byte123";
        String float123 = "float123";
        String double123 = "double123";
        String booleanTrue = "booleanTrue";

        assertEquals(rm.getInteger(integer123).intValue(), 123);
        assertEquals(rm.getObject(integer123, int.class), new Integer(123));

        assertEquals(rm.getShort(short123).shortValue(), 123);
        assertEquals(rm.getObject(short123, short.class),
                new Short((short) 123));

        assertEquals(rm.getLong(long123).longValue(), 123L);
        assertEquals(rm.getObject(long123, long.class), new Long(123));

        assertEquals(rm.getByte(byte123).byteValue(), 123);
        assertEquals(rm.getObject(byte123, byte.class), new Byte((byte) 123));

        assertEquals(rm.getFloat(float123).floatValue(), 123.0f);
        assertEquals(rm.getObject(float123, float.class), new Float(123.0f));

        assertEquals(rm.getDouble(double123).doubleValue(), 123.0);
        assertEquals(rm.getObject(double123, double.class), new Double(123.0));

        assertTrue(rm.getBoolean(booleanTrue));
        assertEquals(Boolean.TRUE, rm.getObject(booleanTrue, boolean.class));
    }

    private ResourceMap localeChangeTestResourceMap() {
        String bundleBaseName = getClass().getPackage().getName()
                + ".resources.LocaleChangeTest";
        /*
         * If the ResourceBundle can't be found, getBundle() will throw an
         * exception. ResourceMap isn't supposed to complain if it can't find a
         * ResourceBundle however the tests that follow expect Basic
         * ResourceBundle to exist.
         */
        ResourceBundle.getBundle(bundleBaseName);
        ClassLoader classLoader = getClass().getClassLoader();
        ResourceMap resourceMap = new ResourceMap(null, classLoader,
                bundleBaseName);
        return resourceMap;
    }

    /**
     * Test for Issue #10: If the default locale changes, ResourceMap cache
     * entries should be cleared
     */
    public void testDefaultLocaleChange() {
        Locale.setDefault(Locale.ENGLISH);
        ResourceMap rm = localeChangeTestResourceMap();
        assertEquals("English string", "Hello", rm.getString("hello"));
        assertEquals("English variable", "Hello World", rm.getString("welcome"));
        Locale.setDefault(Locale.GERMAN);
        assertEquals("German string", "Hallo", rm.getString("hello"));
        assertEquals("German variable", "Hallo Welt", rm.getString("welcome"));
    }

}
