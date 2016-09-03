goog.addDependency('../../../soy/soyutils_usegoog.js', ['soy', 'soy.StringBuilder', 'soy.asserts', 'soy.esc', 'soydata', 'soydata.SanitizedHtml', 'soydata.SanitizedHtmlAttribute', 'soydata.SanitizedJs', 'soydata.SanitizedJsStrChars', 'soydata.SanitizedTrustedResourceUri', 'soydata.SanitizedUri', 'soydata.VERY_UNSAFE'], ['goog.array', 'goog.asserts', 'goog.debug', 'goog.dom.DomHelper', 'goog.format', 'goog.html.SafeHtml', 'goog.html.SafeStyle', 'goog.html.SafeUrl', 'goog.html.TrustedResourceUrl', 'goog.html.uncheckedconversions', 'goog.i18n.BidiFormatter', 'goog.i18n.bidi', 'goog.object', 'goog.soy', 'goog.soy.data.SanitizedContent', 'goog.soy.data.SanitizedContentKind', 'goog.soy.data.SanitizedCss', 'goog.soy.data.UnsanitizedText', 'goog.string', 'goog.string.Const', 'goog.string.StringBuffer']);
goog.addDependency('../../../../../bazel-genfiles/src/js/types.soy.js', ['dossier.soy.type'], ['soy', 'soydata', 'goog.asserts', 'soy.asserts', 'dossier.expression.NamedType', 'dossier.expression.TypeExpression', 'dossier.expression.TypeLink'], {'module': 'goog'});
goog.addDependency('../../../../../bazel-genfiles/src/js/proto.dossier.js', ['proto.dossier', 'dossier.Visibility', 'dossier.SourceFile', 'dossier.Resources', 'dossier.Link', 'dossier.SourceLink', 'dossier.Comment', 'dossier.Comment.Token', 'dossier.Tags', 'dossier.BaseProperty', 'dossier.Property', 'dossier.Function', 'dossier.Function.Detail', 'dossier.Enumeration', 'dossier.Enumeration.Value', 'dossier.Index', 'dossier.Index.Entry', 'dossier.JsType', 'dossier.JsType.TypeSummary', 'dossier.JsType.NestedTypes', 'dossier.JsType.ParentLink', 'dossier.PageData', 'dossier.PageData.Markdown', 'dossier.PageData.TypeCollection', 'dossier.expression.FunctionType', 'dossier.expression.TypeLink', 'dossier.expression.NamedType', 'dossier.expression.UnionType', 'dossier.expression.RecordType', 'dossier.expression.RecordType.Entry', 'dossier.expression.TypeExpression'], ['soydata.SanitizedHtml', 'soydata.SanitizedUri', 'soydata.VERY_UNSAFE'], {'lang': 'es6'});
goog.addDependency('../../../../../bazel-genfiles/src/js/nav.soy.js', ['dossier.soy.nav'], ['soy', 'soydata', 'goog.asserts', 'soy.asserts', 'dossier.Index.Entry', 'dossier.soy.type'], {'module': 'goog'});
goog.addDependency('../../../../../src/js/main.js', ['dossier.main'], ['dossier.Index', 'dossier.app', 'dossier.nav', 'dossier.page', 'dossier.search', 'goog.labs.userAgent.browser', 'goog.labs.userAgent.engine'], {'lang': 'es6', 'module': 'goog'});
goog.addDependency('../../../../../test/js/nav_test.js', ['dossier.nav.test'], ['dossier.Index', 'dossier.expression.NamedType', 'dossier.nav', 'goog.testing.jsunit'], {'lang': 'es6-impl'});
goog.addDependency('../../../../../src/js/app.js', ['dossier.app'], ['dossier.page', 'dossier.search', 'goog.Promise', 'goog.array', 'goog.dom', 'goog.events', 'goog.events.KeyCodes', 'goog.style', 'goog.userAgent'], {'lang': 'es6', 'module': 'goog'});
goog.addDependency('../../../../../src/js/nav.js', ['dossier.nav'], ['dossier.Index', 'dossier.page', 'dossier.soy.nav', 'goog.array', 'goog.asserts', 'goog.dom', 'goog.events', 'goog.events.KeyCodes', 'goog.labs.userAgent.browser', 'goog.labs.userAgent.device', 'goog.soy', 'soydata.SanitizedHtml'], {'lang': 'es6', 'module': 'goog'});
goog.addDependency('../../../../../bazel-genfiles/src/js/dossier.soy.js', ['dossier.soy'], ['soy', 'soydata', 'goog.asserts', 'soy.asserts', 'dossier.BaseProperty', 'dossier.Comment', 'dossier.Enumeration', 'dossier.Function', 'dossier.JsType', 'dossier.PageData', 'dossier.Property', 'dossier.SourceFile', 'dossier.SourceLink', 'dossier.Tags', 'dossier.soy.type'], {'lang': 'es5', 'module': 'goog'});
goog.addDependency('../../../../../src/js/page.js', ['dossier.page'], ['goog.array'], {'lang': 'es6', 'module': 'goog'});
goog.addDependency('../../../../../src/js/search.js', ['dossier.search'], ['dossier.Index', 'dossier.Index.Entry', 'dossier.page', 'goog.events', 'goog.events.EventTarget', 'goog.events.EventType', 'goog.ui.ac.ArrayMatcher', 'goog.ui.ac.AutoComplete', 'goog.ui.ac.InputHandler', 'goog.ui.ac.Renderer', 'goog.userAgent'], {'lang': 'es6', 'module': 'goog'});
