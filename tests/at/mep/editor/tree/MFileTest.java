package at.mep.editor.tree;

import at.mep.Matlab;
import at.mep.installer.Install;
import at.mep.meta.EAccess;
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
    private File testLocation;
    private File classExampleFile;
    private File classExampleFileNoAttributes;

    private MTree mTree_classExampleFile;
    private MTree mTree_classExampleFileNoAttributes;

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
            Validate.isTrue(classDefList.get(0).getNode().getText().equals("ClassExampleNoAttributes"), "ClassExampleNoAttributes node is not parsed correctly");
            Validate.isTrue(classDefList.get(0).getSuperclasses().size() == 1
                    && classDefList.get(0).getSuperclasses().get(0).getType() == MTree.NodeType.JAVA_NULL_NODE, "ClassExampleNoAttributes should'nt have superclasses");
        }

        // Test ClassExample
        {
            MFile mFile = MFile.construct(mTree_classExampleFile);
            List<MFile.ClassDef> classDefList = mFile.getClassDefs();
            Validate.isTrue(classDefList.size() == 1, "ClassExample: classdef has not been found (size() must be 1)");

            MFile.ClassDef classdef = classDefList.get(0);
            // check class definition
            {
                Validate.isTrue(classdef.getNode().getText().equals("ClassExample"), "ClassExample node is not parsed correctly");
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

                    Validate.isTrue(properties.getAttributes().get(0).getAttributeList().get(0).getAttributeAsEAttribute() == EAttributes.ACCESS, "ClassExample: attribute 1 of properties not Access PROPERTIES1.ATTRIBUTES.ATTR1");
                    Validate.isTrue(properties.getAttributes().get(0).getAttributeList().get(1).getAttributeAsEAttribute() == EAttributes.TRANSIENT, "ClassExample: attribute 1 of properties not Transient PROPERTIES1.ATTRIBUTES.ATTR2");
                    Validate.isTrue(properties.getAttributes().get(0).getAttributeList().get(0).getAccessAsEAccess() == EAccess.PUBLIC, "ClassExample: Access not public PROPERTIES1.ATTRIBUTES.ATTR1");
                    Validate.isTrue(properties.getAttributes().get(0).getAttributeList().get(1).getAccessAsEAccess() == EAccess.TRUE, "ClassExample: Transient not true PROPERTIES1.ATTRIBUTES.ATTR2");

                    // check property names
                    List<MFile.ClassDef.Properties.Property> propertyList = properties.getPropertyList();
                    Validate.isTrue(propertyList.size() == 6,  "ClassExample: property definition not 5 PROPERTIES1.EQUALS");

                    Validate.isTrue(propertyList.get(0).getNode().getText().equals("var1"), "ClassExample: property node is not parsed correctly: var1 PROPERTIES1");
                    Validate.isTrue(propertyList.get(1).getNode().getText().equals("var2"), "ClassExample: property node is not parsed correctly: var2 PROPERTIES1");
                    Validate.isTrue(propertyList.get(2).getNode().getText().equals("var3"), "ClassExample: property node is not parsed correctly: var3 PROPERTIES1");
                    Validate.isTrue(propertyList.get(3).getNode().getText().equals("var4"), "ClassExample: property node is not parsed correctly: var4 PROPERTIES1");
                    Validate.isTrue(propertyList.get(4).getNode().getText().equals("var5"), "ClassExample: property node is not parsed correctly: var5 PROPERTIES1");
                    Validate.isTrue(propertyList.get(5).getNode().getText().equals("var6"), "ClassExample: property node is not parsed correctly: var6 PROPERTIES1");

                    // chek properties getter and setter functions
                    MFile.ClassDef.Properties.Property p = propertyList.get(0);
                    Validate.isTrue(p.getGetter().getNode().getText().equals("get.var1"));
                    Validate.isTrue(p.getSetter().getNode().getText().equals("set.var1"));

                    // chek property type definition
                    Validate.isTrue(properties.getPropertyList().get(0).getDefinition().getType() == MTree.NodeType.JAVA_NULL_NODE, "ClassExample: property type is not parsed correctly: var1 PROPERTIES1");
                    Validate.isTrue(properties.getPropertyList().get(1).getDefinition().getType() == MTree.NodeType.JAVA_NULL_NODE, "ClassExample: property type is not parsed correctly: var2 PROPERTIES1");
                    Validate.isTrue(properties.getPropertyList().get(2).getDefinition().getText().equals("double"), "ClassExample: property type is not parsed correctly: var3 PROPERTIES1");
                    Validate.isTrue(properties.getPropertyList().get(3).getDefinition().getText().equals("double"), "ClassExample: property type is not parsed correctly: var4 PROPERTIES1");
                    Validate.isTrue(properties.getPropertyList().get(4).getDefinition().getText().equals("double"), "ClassExample: property type is not parsed correctly: var5 PROPERTIES1");
                    Validate.isTrue(properties.getPropertyList().get(5).getDefinition().getText().equals("double"), "ClassExample: property type is not parsed correctly: var6 PROPERTIES1");

                    // check validators
                    if (!Matlab.verLessThan(Matlab.R2017a)) {
                        Validate.isTrue(properties.getPropertyList().get(5).getValidators().size() == 2, "ClassExample: property validators are not parsed correctly: var6 PROPERTIES1.PROPTYPEDECL");
                        Validate.isTrue(properties.getPropertyList().get(5).getValidators().get(0).getText().equals("mustBeReal"), "ClassExample: property validator is not parsed correctly expected mustBeReal: var6 PROPERTIES1.PROPTYPEDECL");
                        Validate.isTrue(properties.getPropertyList().get(5).getValidators().get(1).getText().equals("mustBeFinite"), "ClassExample: property validator is not parsed correctly expected mustBeFinite: var6 PROPERTIES1.PROPTYPEDECL");
                    }

                    properties = classdef.getProperties().get(1);
                    // check attributes of property
                    Validate.isTrue(properties.getAttributes().size() == 1, "ClassExample: attributes of properties not 1 PROPERTIES2.ATTRIBUTES");
                    Validate.isTrue(properties.getAttributes().get(0).getAttributeList().size() == 1, "ClassExample: attributes of properties not 1 PROPERTIES2.ATTRIBUTES.ATTR");
                    MFile.Attributes.Attribute attribute = properties.getAttributes().get(0).getAttributeList().get(0);

                    Validate.isTrue(attribute.getNode().getText().equals("Access"), "ClassExample: properties needs Access attribute PROPERTIES2.ATTRIBUTES.ATTR");
                    Validate.isTrue(attribute.getValue().get(0).getText().equals("someOtherClass1"), "ClassExample: properties Access must be {?someOtherClass1} PROPERTIES2.ATTRIBUTES.ATTR");

                    Validate.isTrue(properties.getPropertyList().size() == 1,  "ClassExample: property definition not 1 PROPERTIES2.EQUALS");
                    Validate.isTrue(properties.getPropertyList().get(0).getNode().getText().equals("var7"), "ClassExample: property node is not parsed correctly: var7 PROPERTIES2");

                    // check property type definition
                    Validate.isTrue(properties.getPropertyList().get(0).getDefinition().getType() == MTree.NodeType.JAVA_NULL_NODE, "ClassExample: property type is not parsed correctly: var6 PROPERTIES2");
                }

                // methods check
                {
                    List<MFile.ClassDef.Method> methods = classdef.getMethod();
                    Validate.isTrue(methods.size() == 3, "ClassExample: method definiton is not 3 METHODS");

                    // check attributes of methods
                    Validate.isTrue(methods.get(0).getAttributes().size() == 0, "ClassExample: method ATTRIBUTES is not 1 METHODS1.ATTRIBUTES");
                    Validate.isTrue(methods.get(1).getAttributes().size() == 1, "ClassExample: method ATTRIBUTES is not 1 METHODS2.ATTRIBUTES");
                    Validate.isTrue(methods.get(2).getAttributes().size() == 1, "ClassExample: method ATTRIBUTES is not 1 METHODS3.ATTRIBUTES");

                    Validate.isTrue(methods.get(1).getAttributes().get(0).getAttributeList().size() == 2, "ClassExample: method ATTRIBUTES is not 2 METHODS2.ATTRIBUTES.ATTR");
                    Validate.isTrue(methods.get(2).getAttributes().get(0).getAttributeList().size() == 2, "ClassExample: method ATTRIBUTES is not 2 METHODS3.ATTRIBUTES.ATTR");

                    MFile.Attributes attributes = methods.get(1).getAttributes().get(0);
                    Validate.isTrue(attributes.getAttributeList().get(0).getAttributeAsEAttribute() == EAttributes.STATIC, "ClassExample: method ATTR is not Static METHODS2.ATTRIBUTES.ATTR1");
                    Validate.isTrue(attributes.getAttributeList().get(0).getAccessAsEAccess() == EAccess.INVALID, "ClassExample: method ATTR is Static must not be set METHODS2.ATTRIBUTES.ATTR1");
                    Validate.isTrue(attributes.getAttributeList().get(1).getAttributeAsEAttribute() == EAttributes.HIDDEN, "ClassExample: method ATTR is not Static METHODS2.ATTRIBUTES.ATTR2");
                    Validate.isTrue(attributes.getAttributeList().get(1).getAccessAsEAccess() == EAccess.INVALID, "ClassExample: method ATTR is Hidden must not be set METHODS2.ATTRIBUTES.ATTR2");

                    attributes = methods.get(2).getAttributes().get(0);
                    Validate.isTrue(attributes.getAttributeList().get(0).getNode().getText().equals("Static"), "ClassExample: method ATTR is not Static METHODS3.ATTRIBUTES.ATTR1");
                    Validate.isTrue(attributes.getAttributeList().get(0).getValue().get(0).getText().equals("true"), "ClassExample: method ATTR is Static is not true METHODS3.ATTRIBUTES.ATTR1");
                    Validate.isTrue(attributes.getAttributeList().get(1).getNode().getText().equals("Hidden"), "ClassExample: method ATTR is not Static METHODS3.ATTRIBUTES.ATTR2");
                    Validate.isTrue(attributes.getAttributeList().get(1).getValue().get(0).getText().equals("false"), "ClassExample: method ATTR is Hidden is not false METHODS3.ATTRIBUTES.ATTR2");
                    // function check
                    {
                        Validate.isTrue(classdef.getMethod().get(0).getFunctionList().size() == 6, "ClassExample: method has not 6 functions METHODS1.EQUALS");
                        Validate.isTrue(classdef.getMethod().get(1).getFunctionList().size() == 1, "ClassExample: method has not 1 function METHODS2.EQUALS");
                        Validate.isTrue(classdef.getMethod().get(2).getFunctionList().size() == 1, "ClassExample: method has not 1 function METHODS3.EQUALS");

                        List<MFile.ClassDef.Method.Function> functions = classdef.getMethod().get(0).getFunctionList();
                        Validate.isTrue(functions.get(0).getNode().getText().equals("ClassExample"), "ClassExample: function node is not parsed correctly METHOD1.EQUALS1 ClassExample");
                        Validate.isTrue(functions.get(1).getNode().getText().equals("get.var1"), "ClassExample: function node is not parsed correctly METHOD1.EQUALS2 fNoATTR_InArg");
                        Validate.isTrue(functions.get(2).getNode().getText().equals("set.var1"), "ClassExample: function node is not parsed correctly METHOD1.EQUALS3 fNoATTR_InArg");
                        Validate.isTrue(functions.get(3).getNode().getText().equals("fNoATTR_InArg"), "ClassExample: function node is not parsed correctly METHOD1.EQUALS4 fNoATTR_InArg");
                        Validate.isTrue(functions.get(4).getNode().getText().equals("fNoATTR_InArgOutArg"), "ClassExample: function node is not parsed correctly METHOD1.EQUALS5 fNoATTR_InArgOutArg");
                        Validate.isTrue(functions.get(5).getNode().getText().equals("fNoATTR_InArgsOutArgs"), "ClassExample: function node is not parsed correctly METHOD1.EQUALS6 fNoATTR_InArgsOutArgs");

                        Validate.isTrue(functions.get(0).getInArgs().size() == 0, "ClassExample: function should have 0 input arguments METHOD1.EQUALS1");
                        Validate.isTrue(functions.get(1).getInArgs().size() == 1, "ClassExample: function should have 1 input arguments METHOD1.EQUALS2");
                        Validate.isTrue(functions.get(2).getInArgs().size() == 2, "ClassExample: function should have 2 input arguments METHOD1.EQUALS3");
                        Validate.isTrue(functions.get(3).getInArgs().size() == 1, "ClassExample: function should have 1 input arguments METHOD1.EQUALS4");
                        Validate.isTrue(functions.get(4).getInArgs().size() == 1, "ClassExample: function should have 1 input arguments METHOD1.EQUALS5");
                        Validate.isTrue(functions.get(5).getInArgs().size() == 3, "ClassExample: function should have 3 input arguments METHOD1.EQUALS6");

                        Validate.isTrue(functions.get(0).getOutArgs().size() == 1, "ClassExample: function should have 1 output arguments METHOD1.EQUALS1");
                        Validate.isTrue(functions.get(1).getOutArgs().size() == 1, "ClassExample: function should have 1 output arguments METHOD1.EQUALS2");
                        Validate.isTrue(functions.get(2).getOutArgs().size() == 0, "ClassExample: function should have 0 output arguments METHOD1.EQUALS3");
                        Validate.isTrue(functions.get(3).getOutArgs().size() == 0, "ClassExample: function should have 0 output arguments METHOD1.EQUALS4");
                        Validate.isTrue(functions.get(4).getOutArgs().size() == 1, "ClassExample: function should have 1 output arguments METHOD1.EQUALS5");
                        Validate.isTrue(functions.get(5).getOutArgs().size() == 2, "ClassExample: function should have 2 output arguments METHOD1.EQUALS6");
                    }
                }
            }

            // functions check
            {
                // since it is basically the same as in methods, just check numbers
                Validate.isTrue(mFile.getFunctions().size() == 8, "ClassExample: mFile should have 6 functions");
            }

            // Cell Title check
            {
                List<MFile.CellTitle> cellTitles = mFile.getCellTitles();
                Validate.isTrue(cellTitles.size() == 3, "ClassExample: mFile should have 3 cell title");
                Validate.isTrue(cellTitles.get(0).getNode().getText().equals("    %% CELL TITLE 1"), "ClassExample: CellTitle is not parsed correctly CELL_TITLE1");
                Validate.isTrue(cellTitles.get(1).getNode().getText().equals("    %% CELL TITLE 2"), "ClassExample: CellTitle is not parsed correctly CELL_TITLE2");
                Validate.isTrue(cellTitles.get(2).getNode().getText().equals("    %% CELL TITLE 3"), "ClassExample: CellTitle is not parsed correctly CELL_TITLE3");
            }
        }
    }
}
