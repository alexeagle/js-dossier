workspace(name = "dossier")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive", "http_file")

RULES_JVM_EXTERNAL_TAG = "2.7"

RULES_JVM_EXTERNAL_SHA = "f04b1466a00a2845106801e0c5cec96841f49ea4e7d1df88dc8e4bf31523df74"

http_archive(
    name = "rules_jvm_external",
    sha256 = RULES_JVM_EXTERNAL_SHA,
    strip_prefix = "rules_jvm_external-%s" % RULES_JVM_EXTERNAL_TAG,
    url = "https://github.com/bazelbuild/rules_jvm_external/archive/%s.zip" % RULES_JVM_EXTERNAL_TAG,
)

http_archive(
    name = "build_bazel_rules_nodejs",
    sha256 = "0942d188f4d0de6ddb743b9f6642a26ce1ad89f09c0035a9a5ca5ba9615c96aa",
    urls = ["https://github.com/bazelbuild/rules_nodejs/releases/download/0.38.1/rules_nodejs-0.38.1.tar.gz"],
)

http_archive(
    name = "com_google_protobuf",
    sha256 = "758249b537abba2f21ebc2d02555bf080917f0f2f88f4cbe2903e0e28c4187ed",
    strip_prefix = "protobuf-3.10.0",
    urls = ["https://github.com/protocolbuffers/protobuf/archive/v3.10.0.tar.gz"],
)

http_file(
    name = "com_google_common_html_types_html_proto",
    sha256 = "6ece202f11574e37d0c31d9cf2e9e11a0dbc9218766d50d211059ebd495b49c3",
    urls = [
        "https://mirror.bazel.build/raw.githubusercontent.com/google/safe-html-types/release-1.0.5/proto/src/main/protobuf/webutil/html/types/proto/html.proto",
        "https://raw.githubusercontent.com/google/safe-html-types/release-1.0.5/proto/src/main/protobuf/webutil/html/types/proto/html.proto",
    ],
)

http_archive(
    name = "com_google_javascript_closure_library",
    urls = [
        #"https://mirror.bazel.build/github.com/google/closure-library/archive/v20190729.tar.gz",
        "https://github.com/google/closure-library/archive/v20190729.tar.gz",
    ],
    sha256 = "8e8a57146510d27f63f750533d274a5d7654df155491629d6585233a631f5590",
    strip_prefix = "closure-library-20190729",
    build_file = "//third_party/java/closure_library:closure_library.BUILD",
)

load("@build_bazel_rules_nodejs//:index.bzl", "npm_install")

npm_install(
    name = "npm",
    package_json = "//:package.json",
    package_lock_json = "//:package-lock.json",
)

load("@rules_jvm_external//:defs.bzl", "maven_install")

maven_install(
    artifacts = [
        "args4j:args4j:jar:2.0.26",
        "com.atlassian.commonmark:commonmark:0.12.1",
        "com.google.auto.factory:auto-factory:1.0-beta6",
        "com.google.auto.service:auto-service:1.0-rc4",
        "com.google.auto.value:auto-value:1.6.3",
        "com.google.auto.value:auto-value-annotations:1.6.3",
        "com.google.code.findbugs:jsr305:1.3.9",
        "com.google.guava:guava:27.0.1-jre",
        "com.google.inject:guice:4.1.0",
        "com.google.javascript:closure-compiler-externs:v20190819",
        "com.google.jimfs:jimfs:1.0",
        "com.google.template:soy:2019-08-22",
        "com.google.truth:truth:0.42",
        "com.google.truth.extensions:truth-proto-extension:0.42",
        "com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer:r239",
        "org.jsoup:jsoup:1.8.3",
    ],
    repositories = [
        "https://jcenter.bintray.com/",
        "https://maven.google.com",
        "https://repo1.maven.org/maven2",
    ],
)

maven_jar(
    name = "com_google_javascript_closure_compiler_unshaded",
    artifact = "com.google.javascript:closure-compiler-unshaded:v20190819",
    sha1 = "49ffac557a908252a37e8806f5897a274dbbc198",
)

load("@com_google_protobuf//:protobuf_deps.bzl", "protobuf_deps")

protobuf_deps()
