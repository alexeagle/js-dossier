package com.google.javascript.jscomp;

import static com.github.jleyba.dossier.CompilerUtil.createSourceFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import com.github.jleyba.dossier.CompilerUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.javascript.rhino.JSDocInfo;
import com.google.javascript.rhino.Node;
import com.google.javascript.rhino.Token;
import com.google.javascript.rhino.jstype.JSType;
import com.google.javascript.rhino.jstype.ObjectType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Tests for {@link DossierProcessCommonJsModules}.
 */
@RunWith(JUnit4.class)
public class DossierProcessCommonJsModulesTest {

  @Test
  public void doesNotModifySourceIfFileIsNotACommonJsModule() {
    CompilerUtil compiler = createCompiler();

    compiler.compile(path("foo/bar.js"), "var x = 123;");
    assertEquals("var x = 123;", compiler.toSource().trim());
  }

  @Test
  public void setsUpCommonJsModulePrimitives_emptyModule() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"), "");
    assertEquals(
        "var dossier$$module__foo$bar = {exports:{}};",
        compiler.toSource().trim());
  }

  @Test
  public void setsUpCommonJsModulePrimitives_moduleExportsReference() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"), "module.exports.x = 123;");
    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {exports:{}};",
            "dossier$$module__foo$bar.exports.x = 123;"),
        compiler.toSource().trim());
  }

  @Test
  public void setsUpCommonJsModulePrimitives_hasExportsReference() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"), "exports.x = 123;");
    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {exports:{}};",
            "dossier$$module__foo$bar.exports.x = 123;"),
        compiler.toSource().trim());
  }

  @Test
  public void hasExportsReferenceAndAnotherScriptDefinesExportsInTheGlobalScope() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(
        createSourceFile(path("base.js"), "var exports = {};"),
        createSourceFile(path("foo/bar.js"), "exports.x = 123;"));
    assertEquals(
        lines(
            "var exports = {};",
            "var dossier$$module__foo$bar = {exports:{}};",
            "dossier$$module__foo$bar.exports.x = 123;"),
        compiler.toSource().trim());
  }

  @Test
  public void doesNotDefineExportsObjectLiteralIfFirstModuleExportsReferenceIsAssignment() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"), "module.exports = 123;");
    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {};",
            "dossier$$module__foo$bar.exports = 123;"),
        compiler.toSource().trim());
  }

  @Test
  public void moduleRebindsExportsVariable() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"), "exports = 123;");
    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {exports:{}};",
            "var exports$$_dossier$$module__foo$bar = dossier$$module__foo$bar.exports;",
            "exports$$_dossier$$module__foo$bar = 123;"),
        compiler.toSource().trim());
  }

  @Test
  public void rebindsModuleExports() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"),
        "module.exports = {};",
        "module.exports.x = 123;");
    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {};",
            "dossier$$module__foo$bar.exports = {};",
            "dossier$$module__foo$bar.exports.x = 123;"),
        compiler.toSource().trim());
  }

  @Test
  public void renamesModuleGlobalVars() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"), "var x = 123;");
    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {exports:{}};",
            "var x$$_dossier$$module__foo$bar = 123;"),
        compiler.toSource().trim());
  }

  @Test
  public void doesNotRenameNonGlobalVars() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"), "function x() { var x = 123; }");
    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {exports:{}};",
            "function x$$_dossier$$module__foo$bar() {",
            "  var x = 123;",
            "}",
            ";"),
        compiler.toSource().trim());
  }

  @Test
  public void renamesModuleGlobalFunctionDeclarations() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"), "function foo(){}");
    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {exports:{}};",
            "function foo$$_dossier$$module__foo$bar() {",
            "}",
            ";"),
        compiler.toSource().trim());
  }

  @Test
  public void renamesModuleGlobalFunctionExpressions() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"), "var foo = function(){}");
    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {exports:{}};",
            "var foo$$_dossier$$module__foo$bar = function() {",
            "};"),
        compiler.toSource().trim());
  }

  @Test
  public void sortsSingleModuleDep() {
    CompilerUtil compiler = createCompiler(path("foo/leaf.js"), path("foo/root.js"));

    SourceFile root = createSourceFile(path("foo/root.js"), "");
    SourceFile leaf = createSourceFile(path("foo/leaf.js"), "require('./root');");

    compiler.compile(leaf, root);  // Should reorder since leaf depends on root.

    assertEquals(
        lines(
            "var dossier$$module__foo$root = {exports:{}};",
            "var dossier$$module__foo$leaf = {exports:{}};",
            "dossier$$module__foo$root.exports;"),
        compiler.toSource().trim());
  }

  @Test
  public void sortsWithTwoModuleDeps() {
    CompilerUtil compiler = createCompiler(
        path("foo/one.js"), path("foo/two.js"), path("foo/three.js"));

    SourceFile one = createSourceFile(path("foo/one.js"), "");
    SourceFile two = createSourceFile(path("foo/two.js"),
        "require('./one');",
        "require('./three');");
    SourceFile three = createSourceFile(path("foo/three.js"));

    compiler.compile(two, one, three);  // Should properly reorder inputs.

    assertEquals(
        lines(
            "var dossier$$module__foo$one = {exports:{}};",
            "var dossier$$module__foo$three = {exports:{}};",
            "var dossier$$module__foo$two = {exports:{}};",
            "dossier$$module__foo$one.exports;",
            "dossier$$module__foo$three.exports;"),
        compiler.toSource().trim());
  }

  @Test
  public void rewritesRequireStatementToDirectlyReferenceExportsObject() {
    CompilerUtil compiler = createCompiler(path("foo/leaf.js"), path("foo/root.js"));

    compiler.compile(
        createSourceFile(path("foo/root.js"), ""),
        createSourceFile(path("foo/leaf.js"),
            "var foo = require('./root');",
            "var bar = require('./root').bar"));

    assertEquals(
        lines(
            "var dossier$$module__foo$root = {exports:{}};",
            "var dossier$$module__foo$leaf = {exports:{}};",
            "var foo$$_dossier$$module__foo$leaf = dossier$$module__foo$root.exports;",
            "var bar$$_dossier$$module__foo$leaf = dossier$$module__foo$root.exports.bar;"),
        compiler.toSource().trim());
  }

  @Test
  public void rewritesRequireStatementToDirectlyReferenceExportsObject_compoundStatement() {
    CompilerUtil compiler = createCompiler(path("foo/leaf.js"), path("foo/root.js"));

    compiler.compile(
        createSourceFile(path("foo/root.js"), ""),
        createSourceFile(path("foo/leaf.js"),
            "var foo = require('./root'),",
            "    bar = require('./root').bar"));

    assertEquals(
        lines(
            "var dossier$$module__foo$root = {exports:{}};",
            "var dossier$$module__foo$leaf = {exports:{}};",
            "var foo$$_dossier$$module__foo$leaf = dossier$$module__foo$root.exports, " +
                "bar$$_dossier$$module__foo$leaf = dossier$$module__foo$root.exports.bar;"),
        compiler.toSource().trim());
  }

  @Test
  public void handlesRequiringModulesFromASubDirectory() {
    CompilerUtil compiler = createCompiler(path("foo/one.js"), path("foo/bar/two.js"));

    compiler.compile(
        createSourceFile(path("foo/one.js"), "require('./bar/two');"),
        createSourceFile(path("foo/bar/two.js"), ""));

    assertEquals(
        lines(
            "var dossier$$module__foo$bar$two = {exports:{}};",
            "var dossier$$module__foo$one = {exports:{}};",
            "dossier$$module__foo$bar$two.exports;"),
        compiler.toSource().trim());
  }

  @Test
  public void handlesRequiringModulesFromAParentDirectory() {
    CompilerUtil compiler = createCompiler(path("foo/one.js"), path("foo/bar/two.js"));

    compiler.compile(
        createSourceFile(path("foo/one.js"), ""),
        createSourceFile(path("foo/bar/two.js"), "require('../one');"));

    assertEquals(
        lines(
            "var dossier$$module__foo$one = {exports:{}};",
            "var dossier$$module__foo$bar$two = {exports:{}};",
            "dossier$$module__foo$one.exports;"),
        compiler.toSource().trim());
  }

  @Test
  public void handlesRequiringModulesFromAParentsSibling() {
    CompilerUtil compiler = createCompiler(
        path("foo/baz/one.js"), path("foo/bar/two.js"));

    compiler.compile(
        createSourceFile(path("foo/baz/one.js"), ""),
        createSourceFile(path("foo/bar/two.js"), "require('../baz/one');"));

    assertEquals(
        lines(
            "var dossier$$module__foo$baz$one = {exports:{}};",
            "var dossier$$module__foo$bar$two = {exports:{}};",
            "dossier$$module__foo$baz$one.exports;"),
        compiler.toSource().trim());
  }

  @Test
  public void handlesRequiringAbsoluteModule() {
    CompilerUtil compiler = createCompiler(
        path("/absolute/foo/baz/one.js"), path("foo/bar/two.js"));

    compiler.compile(
        createSourceFile(path("/absolute/foo/baz/one.js"), ""),
        createSourceFile(path("foo/bar/two.js"), "require('/absolute/foo/baz/one');"));

    assertEquals(
        lines(
            "var dossier$$module__$absolute$foo$baz$one = {exports:{}};",
            "var dossier$$module__foo$bar$two = {exports:{}};",
            "dossier$$module__$absolute$foo$baz$one.exports;"),
        compiler.toSource().trim());
  }

  @Test
  public void nonGlobalRequireCallsAreNotRegisteredAsInputRequirements() {
    CompilerUtil compiler = createCompiler(
        path("foo/one.js"), path("foo/two.js"), path("foo/three.js"));

    compiler.compile(
        createSourceFile(path("foo/one.js"),
            "var x = require('./two');"),
        createSourceFile(path("foo/two.js"),
            "var go = function() {",
            "  var x = require('./three');",
            "};"),
        createSourceFile(path("foo/three.js"),
            "var x = require('./one');"));

    assertEquals(
        lines(
            "var dossier$$module__foo$two = {exports:{}};",
            "var go$$_dossier$$module__foo$two = function() {",
            "  var x = dossier$$module__foo$three.exports;",
            "};",
            "var dossier$$module__foo$one = {exports:{}};",
            "var x$$_dossier$$module__foo$one = dossier$$module__foo$two.exports;",
            "var dossier$$module__foo$three = {exports:{}};",
            "var x$$_dossier$$module__foo$three = dossier$$module__foo$one.exports;"),
        compiler.toSource().trim());
  }

  @Test
  public void maintainsInternalTypeCheckingConsistency() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"),
        "/** @constructor */",
        "var Bar = function() {};",
        "",
        "/** @constructor */",
        "Bar.Baz = function() {};",
        "",
        "/** @type {!Bar} */",
        "var x = new Bar();",
        "",
        "/** @type {!Bar.Baz} */",
        "var y = new Bar.Baz();",
        "");

    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {exports:{}};",
            "var Bar$$_dossier$$module__foo$bar = function() {",
            "};",
            "Bar$$_dossier$$module__foo$bar.Baz = function() {",
            "};",
            "var x$$_dossier$$module__foo$bar = new Bar$$_dossier$$module__foo$bar;",
            "var y$$_dossier$$module__foo$bar = new Bar$$_dossier$$module__foo$bar.Baz;"),
        compiler.toSource().trim());
  }

  @Test
  public void canReferenceInternalTypes() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"),
        "/** @constructor */",
        "var One = function() {};",
        "",
        "/**",
        " * @constructor",
        " * @extends {One}",
        " */",
        "exports.Two = function() {};",
        // Assignment tests.
        "/** @type {!One} */",
        "var testOne = new One();",
        "testOne = new exports.Two();",
        "");
    // OK if compiles without error.
  }

  @Test
  public void canReferenceTypesDefinedOnOwnModuleExports() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"),
        "/** @constructor */",
        "var One = function() {};",
        "",
        "/**",
        " * @constructor",
        " * @extends {One}",
        " */",
        "exports.Two = function() {};",
        "",
        "/**",
        " * @constructor",
        " * @extends {exports.Two}",
        " */",
        "exports.Three = function() {};",
        "",
        // Assignment tests.
        "/** @type {!One} */",
        "var testOne = new One();",
        "testOne = new exports.Two();",
        "testOne = new exports.Three();",
        "",
        "/** @type {!exports.Two} */",
        "var testTwo = new exports.Two();",
        "testTwo = new exports.Three();",
        "",
        "/** @type {!exports.Three} */",
        "var testThree = new exports.Three();",
        "");
    // OK if compiles without error.
  }

  @Test
  public void canReferenceTypesDefinedOnModuleExports() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(path("foo/bar.js"),
        "/** @constructor */",
        "var One = function() {};",
        "",
        "/**",
        " * @constructor",
        " * @extends {One}",
        " */",
        "exports.Two = function() {};",
        "",
        "/**",
        " * @constructor",
        " * @extends {module.exports.Two}",
        " */",
        "exports.Three = function() {};",
        "",
        // Assignment tests.
        "/** @type {!One} */",
        "var testOne = new One();",
        "testOne = new exports.Two();",
        "testOne = new exports.Three();",
        "",
        "/** @type {!module.exports.Two} */",
        "var testTwo = new exports.Two();",
        "testTwo = new exports.Three();",
        "",
        "/** @type {!module.exports.Three} */",
        "var testThree = new exports.Three();",
        "");
    // OK if compiles without error.
  }

  @Test
  public void canReferenceRequiredModuleTypesUsingImportAlias() {
    CompilerUtil compiler = createCompiler(
        path("foo/bar.js"), path("foo/baz.js"));

    compiler.compile(
        createSourceFile(path("foo/bar.js"),
            "/** @constructor */",
            "exports.Bar = function(){};"),
        createSourceFile(path("foo/baz.js"),
            "var bar = require('./bar');",
            "var Bar = require('./bar').Bar;",
            "var x = {y: {}};",
            "x.Bar = require('./bar').Bar;",
            "x.y.Bar = require('./bar').Bar;",
            "",
            "/** @type {!bar.Bar} */",
            "var one = new bar.Bar();",
            "",
            "/** @type {!Bar} */",
            "var two = new Bar();",
            "",
            "/** @type {!x.Bar} */",
            "var three = new x.Bar();",
            "",
            "/** @type {!x.y.Bar} */",
            "var four = new x.y.Bar();",
            ""));
    // OK if compiles without error.
  }

  @Test
  public void canReferenceCastedTypeThroughModuleImportAlias() {
    CompilerUtil compiler = createCompiler(
        path("foo/bar.js"), path("foo/baz.js"));

    compiler.compile(
        createSourceFile(path("index.js"),
            "/**",
            " * @param {number} a .",
            " * @constructor",
            " */",
            "function NotACommonJsModuleCtor(a) {};"),
        createSourceFile(path("foo/bar.js"),
            "/** @constructor */",
            "exports.NotACommonJsModuleCtor = NotACommonJsModuleCtor;",
            "/** @constructor */",
            "exports.Bar = NotACommonJsModuleCtor;"),
        createSourceFile(path("foo/baz.js"),
            "var bar = require('./bar');",
            "",
            "/** @type {!bar.NotACommonJsModuleCtor} */",
            "var one = new bar.NotACommonJsModuleCtor(1);",
            "",
            "/** @type {!bar.Bar} */",
            "var two = new bar.Bar(2);",
            ""));
    // OK if compiles without error.
  }

  @Test
  public void canReferenceTypeExportedAsAlias() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"), path("foo/baz.js"));

    compiler.compile(
        createSourceFile(path("foo/bar.js"),
            "/**",
            " * @param {number} a .",
            " * @constructor",
            " */",
            "var Greeter = function(a) {};",
            "",
            "/** @constructor */",
            "exports.Bar = Greeter;"),
        createSourceFile(path("foo/baz.js"),
            "var bar = require('./bar');",
            "",
            "/** @type {!bar.Bar} */",
            "var b = new bar.Bar(1);",
            ""));
    // OK if compiles without error.
  }

  @Test
  public void exportedInternalVarInheritsJsDocInfo() {
    CompilerUtil compiler = createCompiler(path("foo.js"));

    compiler.compile(
        createSourceFile(path("foo.js"),
            "/**",
            " * @constructor",
            " */",
            "var Greeter = function(){};",
            "/**",
            " * @param {string} name .",
            " * @return {string} .",
            " */",
            "Greeter.prototype.sayHi = function(name) {",
            "  return 'Hello, ' + name;",
            "};",
            "",
            "exports.Greeter = Greeter"));

    JSType greeterType = compiler.getCompiler()
        .getTypeRegistry()
        .getType("Greeter$$_dossier$$module__foo");
    assertNotNull(greeterType);

    JSDocInfo greeterInfo = greeterType.getJSDocInfo();
    assertNotNull(greeterInfo);

    ObjectType exportsObj = compiler.getCompiler().getTopScope()
        .getVar("dossier$$module__foo")
        .getType()
        .toObjectType()
        .getPropertyType("exports")
        .toObjectType();
    assertNotNull(exportsObj);

    JSType exportedGreeter = exportsObj.getPropertyType("Greeter");
    assertTrue(exportedGreeter.isConstructor());
    assertEquals(greeterType, exportedGreeter.toObjectType().getTypeOfThis());
    assertEquals(greeterInfo, exportedGreeter.getJSDocInfo());
  }

  @Test
  public void savesOriginalTypeNameInJsDoc() {
    CompilerUtil compiler = createCompiler(path("foo.js"));

    compiler.compile(
        createSourceFile(path("foo.js"),
            "/** @constructor */",
            "var Builder = function(){};",
            "/** @return {!Builder} . */",
            "Builder.prototype.returnThis = function() { return this; };",
            "exports.Builder = Builder"));

    Scope scope = compiler.getCompiler().getTopScope();
    Scope.Var var = scope.getVar("dossier$$module__foo");
    ObjectType module = var.getType().toObjectType();
    ObjectType exports = module.getPropertyType("exports").toObjectType();

    JSType type = exports.getPropertyType("Builder");
    assertTrue(type.isConstructor());

    type = type.toObjectType().getTypeOfThis();
    assertEquals("Builder$$_dossier$$module__foo", type.toString());

    type = type.toObjectType().getPropertyType("returnThis");
    assertTrue(type.toString(), type.isFunctionType());

    JSDocInfo info = type.getJSDocInfo();
    assertNotNull(info);

    Node node = Iterables.getOnlyElement(info.getTypeNodes());
    assertEquals(Token.BANG, node.getType());

    node = node.getFirstChild();
    assertTrue(node.isString());
    assertEquals("Builder$$_dossier$$module__foo", node.getString());
    assertEquals("Builder", node.getProp(Node.ORIGINALNAME_PROP));
  }

  @Test
  public void savesOriginalVarNameOnNameNode() {
    CompilerUtil compiler = createCompiler(path("foo.js"));

    compiler.compile(
        createSourceFile(path("foo.js"),
            "var Foo = 1;"));

    Scope scope = compiler.getCompiler().getTopScope();
    Scope.Var var = scope.getVar("Foo$$_dossier$$module__foo");
    Node nameNode = var.getNameNode();
    assertEquals("Foo", nameNode.getProp(Node.ORIGINALNAME_PROP));
  }

  @Test
  public void canReferenceConstructorExportedByAnotherModule() {
    CompilerUtil compiler = createCompiler(path("x/foo.js"), path("x/bar.js"));

    compiler.compile(
        createSourceFile(path("x/foo.js"),
            "/** @constructor */",
            "exports.Foo = function(){};"),
        createSourceFile(path("x/bar.js"),
            "var foo = require('./foo');",
            "/** @type {function(new: foo.Foo)} */",
            "exports.Foo = foo.Foo;"));

    Scope scope = compiler.getCompiler().getTopScope();
    Scope.Var var = scope.getVar("dossier$$module__x$bar");
    ObjectType module = var.getType().toObjectType();
    ObjectType exports = module.getPropertyType("exports").toObjectType();

    JSType type = exports.getPropertyType("Foo");
    assertTrue(type.isConstructor());

    type = type.toObjectType().getTypeOfThis();
    assertEquals("dossier$$module__x$foo.exports.Foo", type.toString());
  }

  @Test
  public void canUseModuleInternalTypedefsInJsDoc() {
    CompilerUtil compiler = createCompiler(path("foo.js"));

    compiler.compile(
        createSourceFile(path("foo.js"),
            "/** @typedef {{x: number}} */",
            "var Variable;",
            "",
            "/**",
            " * @param {Variable} a .",
            " * @param {Variable} b .",
            " * @return {Variable} .",
            " */",
            "exports.add = function(a, b) {",
            "  return {x: a.x + b.x};",
            "};"));

    Scope scope = compiler.getCompiler().getTopScope();
    Scope.Var var = scope.getVar("dossier$$module__foo");
    JSType type = var.getType().toObjectType()
        .getPropertyType("exports")
        .toObjectType()
        .getPropertyType("add");
    assertTrue(type.isFunctionType());

    JSDocInfo info = type.getJSDocInfo();
    Node node = info.getTypeNodes().iterator().next();
    assertTrue(node.isString());
    assertEquals("Variable$$_dossier$$module__foo", node.getString());
    assertEquals("Variable", node.getProp(Node.ORIGINALNAME_PROP));
  }

  @Test
  public void renamesExportsOnWhenUsedAsParameter() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(
        createSourceFile(path("foo/bar.js"),
            "function go(e) {}",
            "go(exports);",
            "go(module);",
            "go(module.exports);"));

    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {exports:{}};",
            "function go$$_dossier$$module__foo$bar(e) {",
            "}",
            "go$$_dossier$$module__foo$bar(dossier$$module__foo$bar.exports);",
            "go$$_dossier$$module__foo$bar(dossier$$module__foo$bar);",
            "go$$_dossier$$module__foo$bar(dossier$$module__foo$bar.exports);"),
        compiler.toSource().trim());
  }

  @Test
  public void rewritesNamespaceAssignments() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(
        createSourceFile(path("foo/bar.js"),
            "/** @type {foo.} */",
            "exports.foo = {};"),
        createSourceFile(path("foo.js"),
            "goog.provide('foo');"));

    assertEquals(
        lines(
            "var foo = {};",
            "var dossier$$module__foo$bar = {exports:{}};",
            "dossier$$module__foo$bar.exports.foo = foo;"),
        compiler.toSource().trim());
  }

  @Test
  public void rewritesNamespaceTypeDeclarations() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(
        createSourceFile(path("foo/bar.js"),
            "/** @type {foo.} */",
            "exports.bar;"),
        createSourceFile(path("foo.js"),
            "goog.provide('foo');"));

    assertEquals(
        lines(
            "var foo = {};",
            "var dossier$$module__foo$bar = {exports:{}};",
            "dossier$$module__foo$bar.exports.bar = foo;"),
        compiler.toSource().trim());
  }

  @Test
  public void rewritesNamespaceTypeGetters() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(
        createSourceFile(path("foo/bar.js"),
            "/** @type {foo.} */",
            "(exports.__defineGetter__('bar', function(){}));",
            "/** @type {foo.} */",
            "(exports.bar.__defineGetter__('baz', function(){}));"),
        createSourceFile(path("foo.js"),
            "goog.provide('foo');"));

    assertEquals(
        lines(
            "var foo = {};",
            "var dossier$$module__foo$bar = {exports:{}};",
            "dossier$$module__foo$bar.exports.bar = foo;",
            "dossier$$module__foo$bar.exports.bar.baz = foo;"),
        compiler.toSource().trim());
  }

  @Test
  public void rewritesCompoundVarDeclarations() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"));

    compiler.compile(
        createSourceFile(path("foo/bar.js"),
            "var x = 1,",
            "    y = 2;"));

    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {exports:{}};",
            "var x$$_dossier$$module__foo$bar = 1, y$$_dossier$$module__foo$bar = 2;"),
        compiler.toSource().trim());
  }

  @Test
  public void canReferenceExportedTypeReferences() {
    CompilerUtil compiler = createCompiler(path("foo/bar.js"), path("foo/baz.js"));

    compiler.compile(
        createSourceFile(path("foo/bar.js"),
            "/** @constructor */",
            "var foo = function(){}",
            "exports.foo = foo;",
            "exports.bar = exports.foo;"),
        createSourceFile(path("foo/baz.js"),
            "var bar = require('./bar');",
            "/** @type {!bar.foo} */",
            "var f = new bar.foo();",
            "/** @type {!bar.bar} */",
            "var b = new bar.bar();"));

    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {exports:{}};",
            "var foo$$_dossier$$module__foo$bar = function() {",
            "};",
            "dossier$$module__foo$bar.exports.foo = foo$$_dossier$$module__foo$bar;",
            "dossier$$module__foo$bar.exports.bar = dossier$$module__foo$bar.exports.foo;",
            "var dossier$$module__foo$baz = {exports:{}};",
            "var bar$$_dossier$$module__foo$baz = dossier$$module__foo$bar.exports;",
            "var f$$_dossier$$module__foo$baz = new bar$$_dossier$$module__foo$baz.foo;",
            "var b$$_dossier$$module__foo$baz = new bar$$_dossier$$module__foo$baz.bar;"),
        compiler.toSource().trim());

    Scope.Var f = compiler.getCompiler().getTopScope().getVar("f$$_dossier$$module__foo$baz");
    assertEquals("foo$$_dossier$$module__foo$bar", f.getType().getDisplayName());

    Scope.Var b = compiler.getCompiler().getTopScope().getVar("b$$_dossier$$module__foo$baz");
    assertEquals("foo$$_dossier$$module__foo$bar", b.getType().getDisplayName());
  }

  @Test
  public void handlesModulesThatOverrideModuleExports() {
    CompilerUtil util = createCompiler(path("foo/bar.js"));

    util.compile(path("foo/bar.js"),
        "/** @constructor */",
        "var Foo = function() {};",
        "module.exports = Foo;");

    assertEquals(
        lines(
            "var dossier$$module__foo$bar = {};",
            "var Foo$$_dossier$$module__foo$bar = function() {",
            "};",
            "dossier$$module__foo$bar.exports = Foo$$_dossier$$module__foo$bar;"),
        util.toSource().trim());

    Scope.Var internalFoo = util.getCompiler().getTopScope().getVar(
        "Foo$$_dossier$$module__foo$bar");
    JSType internalFooType = internalFoo.getType();
    assertTrue(internalFooType.toString(), internalFooType.isConstructor());

    Scope.Var module = util.getCompiler().getTopScope().getVar("dossier$$module__foo$bar");
    JSType exportsType = ObjectType.cast(module.getType()).getPropertyType("exports");

    assertSame(exportsType, internalFooType);
  }

  @Test
  public void doesNotNpeOnScriptThatAccessesPropertyOfReturnValue() {
    CompilerUtil util = createCompiler(path("foo/bar.js"));

    util.compile(path("foo/bar.js"),
        "function createCallback() {",
        " return function(y) { this.doIt().x = y; };",
        "}");
  }

  private static CompilerUtil createCompiler(final Path... commonJsModules) {
    CompilerOptions options = new CompilerOptions();
    options.setCodingConvention(new ClosureCodingConvention());
    options.setIdeMode(true);
    options.setClosurePass(true);
    options.setPrettyPrint(true);
    options.setCheckTypes(true);
    options.setCheckSymbols(true);
    options.setAggressiveVarCheck(CheckLevel.ERROR);
    CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
    CompilationLevel.ADVANCED_OPTIMIZATIONS.setTypeBasedOptimizationOptions(options);

    Compiler compiler = new DossierCompiler(System.err, ImmutableSet.copyOf(commonJsModules));

    return new CompilerUtil(compiler, options);
  }

  private static String lines(String... lines) {
    return Joiner.on('\n').join(lines);
  }

  private static Path path(String first, String... remaining) {
    return FileSystems.getDefault().getPath(first, remaining);
  }
}
