package at.mep.editor.tree;

import at.mep.installer.Install;
import at.mep.util.FileUtils;
import com.mathworks.widgets.text.mcode.MTree;
import org.apache.commons.lang.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;


// ALL jar files are loaded: (700 ish)
// ALL dll files are loaded: (1700 ish)
// ALL windows dll files are loaded: (15k ish
//
// still not working, still missing some .dll
//


public class MFileTest{
    File testLocation;
    File classExampleFile;
    File classExampleFileNoAttributes;

    MTree mTree_classExampleFile;
    MTree mTree_classExampleFileNoAttributes;

    public static void runTest() throws Exception {
        MFileTest mFileTest = new MFileTest();
        mFileTest.setUp();

        mFileTest.test_ClassDef_construct();

        mFileTest.tearDown();

        System.out.println("Tests of: " + MFileTest.class.getSimpleName() + " successful");
    }

    private void setUp() throws Exception {
        String testLocationString = Install.getJarFile().getParent();
        testLocation = new File(testLocationString);

        {
            classExampleFile = new File(testLocationString + "/ClassExample.m");
            FileUtils.exportResource("/ClassExample.m", classExampleFile);
            InputStream stream = new FileInputStream(classExampleFile);
            String string = FileUtils.readInputStreamToString(stream);
            mTree_classExampleFile = MTree.parse(string);
        }

        {
            classExampleFileNoAttributes = new File(testLocationString + "/ClassExampleNoAttributes.m");
            FileUtils.exportResource("/ClassExampleNoAttributes.m", classExampleFileNoAttributes);
            InputStream stream = new FileInputStream(classExampleFileNoAttributes);
            String string = FileUtils.readInputStreamToString(stream);
            mTree_classExampleFileNoAttributes = MTree.parse(string);
        }
    }

    private void tearDown() throws Exception {

    }

    private void test_ClassDef_construct() {

        // Test ClassExampleNoAttributes first
        {
            MFile mFile = MFile.construct(mTree_classExampleFileNoAttributes);
            List<MFile.ClassDef> classDefList = mFile.getClassDefs();
            Validate.isTrue(classDefList.size() == 1, "ClassExampleNoAttributes: classdef has not been found (size() must be 1)");
            Validate.isTrue(classDefList.get(0).getName().getText().equals("ClassExampleNoAttributes"), "ClassExampleNoAttributes name is not parsed correctly");
            Validate.isTrue(classDefList.get(0).getSuperclasses().size() == 1
                    && classDefList.get(0).getSuperclasses().get(0).getType() == MTree.NodeType.JAVA_NULL_NODE, "ClassExampleNoAttributes should'nt have superclasses");
        }

        // Test ClassExample
        {
            MFile mFile = MFile.construct(mTree_classExampleFile);
            List<MFile.ClassDef> classDefList = mFile.getClassDefs();
            Validate.isTrue(classDefList.size() == 1, "ClassExample: classdef has not been found (size() must be 1)");
            Validate.isTrue(mFile.getFunctions().size() == 6, "ClassExample: mFile should have 6 functions");
            Validate.isTrue(mFile.getCellTitles().size() == 3, "ClassExample: mFile should have 6 cell title");
            Validate.isTrue(classDefList.size() == 1, "ClassExample: classdef has not been found (size() must be 1)");

            MFile.ClassDef classdef = classDefList.get(0);
            // check class definition
            {
                Validate.isTrue(classdef.getName().getText().equals("ClassExample"), "ClassExample name is not parsed correctly");
                Validate.isTrue(classdef.getSuperclasses().size() == 3, "ClassExampleNoAttributes should have 3 superclasses");

                Validate.isTrue(classdef.getAttributes().size() == 1, "ClassExample: classdef has no ATTRIBUTES (size() must be 1)");

                // attribute check
                List<MFile.Attributes.Attribute> attributeList = classdef.getAttributes().get(0).getAttributeList();
                Validate.isTrue(attributeList.size() == 3, "ClassExample: size() attributes of class not 3");

                // properties check
                {
                    Validate.isTrue(classdef.getProperties().size() == 2, "ClassExample: properties PROPERTIES (size() must be 2)");
                    MFile.ClassDef.Properties properties = classdef.getProperties().get(0);

                    // check attributes of property
                    Validate.isTrue(properties.getAttributes().size() == 1, "ClassExample: attributes of properties not 1 PROPERTIES1.ATTRIBUTES");
                    Validate.isTrue(properties.getAttributes().get(0).getAttributeList().size() == 2, "ClassExample: attributes of properties not 2 PROPERTIES1.ATTRIBUTES.ATTR");

                    // check property names
                    Validate.isTrue(properties.getPropertyList().size() == 5,  "ClassExample: property definition not 5 PROPERTIES1.EQUALS");
                    List<MFile.ClassDef.Properties.Property> propertyList = properties.getPropertyList();

                    Validate.isTrue(propertyList.get(0).getName().getText().equals("var1"), "ClassExample: property name is not parsed correctly: var1 PROPERTIES1");
                    Validate.isTrue(propertyList.get(1).getName().getText().equals("var2"), "ClassExample: property name is not parsed correctly: var2 PROPERTIES1");
                    Validate.isTrue(propertyList.get(2).getName().getText().equals("var3"), "ClassExample: property name is not parsed correctly: var3 PROPERTIES1");
                    Validate.isTrue(propertyList.get(3).getName().getText().equals("var4"), "ClassExample: property name is not parsed correctly: var4 PROPERTIES1");
                    Validate.isTrue(propertyList.get(4).getName().getText().equals("var5"), "ClassExample: property name is not parsed correctly: var5 PROPERTIES1");

                    properties = classdef.getProperties().get(1);
                    // check attributes of property
                    Validate.isTrue(properties.getAttributes().size() == 1, "ClassExample: attributes of properties not 1 PROPERTIES2.ATTRIBUTES");
                    Validate.isTrue(properties.getAttributes().get(0).getAttributeList().size() == 1, "ClassExample: attributes of properties not 1 PROPERTIES2.ATTRIBUTES.ATTR");
                    MFile.Attributes.Attribute attribute = properties.getAttributes().get(0).getAttributeList().get(0);

                    Validate.isTrue(attribute.getAttribute().getText().equals("Access"), "ClassExample: properties needs Access attribute PROPERTIES2.ATTRIBUTES.ATTR");
                    Validate.isTrue(attribute.getValue().get(0).getText().equals("someOtherClass1"), "ClassExample: properties Access must be {?someOtherClass1} PROPERTIES2.ATTRIBUTES.ATTR");

                    Validate.isTrue(properties.getPropertyList().size() == 1,  "ClassExample: property definition not 1 PROPERTIES2.EQUALS");
                    Validate.isTrue(properties.getPropertyList().get(0).getName().getText().equals("var6"), "ClassExample: property name is not parsed correctly: var6 PROPERTIES2");

                }

                // methods check
                {
                    // function check
                    {

                    }
                }
            }

            // Cell Title check
            {

            }

            throw new IllegalStateException("TODO: add tests");
        }
    }

}