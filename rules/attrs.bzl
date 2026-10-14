"""Generic attributes used by the Android Lint rules
"""

load(
    ":collect_aar_outputs_aspect.bzl",
    _collect_aar_outputs_aspect = "collect_aar_outputs_aspect",
)

ATTRS = dict(
    _lint_wrapper = attr.label(
        default = Label("//src/cli"),
        executable = True,
        cfg = "exec",
    ),
    srcs = attr.label_list(
        mandatory = True,
        allow_files = [".java", ".kt", ".kts"],
        allow_empty = True,
        doc = "Sources to run Android Lint against.",
    ),
    test_srcs = attr.label_list(
        mandatory = False,
        allow_files = [".java", ".kt", ".kts"],
        allow_empty = True,
        default = [],
        doc = """Test sources to run Android Lint against. Lint analyzes them as test code (e.g. RestrictedApi
        allows test-only APIs there), the same way Gradle's checkTestSources does.""",
    ),
    resource_files = attr.label_list(
        mandatory = False,
        allow_files = [".xml"],
        allow_empty = True,
        default = [],
        doc = "Android resource files to run Android Lint against.",
    ),
    lib = attr.label(
        mandatory = True,
        doc = "The target being linted. This is needed to get the compiled R files.",
    ),
    manifest = attr.label(
        mandatory = False,
        allow_single_file = True,
        doc = "Android manifest to run Android Lint against.",
    ),
    merged_manifest = attr.label(
        mandatory = False,
        allow_single_file = True,
        doc = """Merged manifest lint reads minSdkVersion/targetSdkVersion and components from, like the one
        Gradle hands lint. Without it lint merges the dependency manifests into `manifest` itself, which it
        cannot do for libraries, so a library falls back to its own `manifest`.""",
    ),
    deps = attr.label_list(
        mandatory = False,
        allow_empty = True,
        default = [],
        aspects = [_collect_aar_outputs_aspect],
        doc = "Dependencies that should be on the classpath during execution.",
    ),
    android_lint_config = attr.label(
        mandatory = False,
        allow_single_file = True,
        doc = "Lint Android Lint configuration file.",
    ),
    is_test_sources = attr.bool(
        default = False,
        doc = "True when linting test sources, otherwise false.",
    ),
    autofix = attr.bool(
        default = False,
        doc = "Enables lint autofix. This is a no-op right now.",
    ),
    warnings_as_errors = attr.bool(
        default = False,
        doc = "When true, lint will treat warnings as errors.",
    ),
    check_test_sources = attr.bool(
        default = False,
        doc = """When true, every check runs on test_srcs too, like Gradle's lint.checkTestSources. By default
        only checks that explicitly target test sources run on them.""",
    ),
    report_warnings = attr.bool(
        default = False,
        doc = """When true, warnings are written to the report alongside errors; only errors fail the test,
        matching Gradle's abortOnError. By default warnings are not reported (--nowarn). Has no effect when
        warnings_as_errors is set.""",
    ),
    disable_checks = attr.string_list(
        mandatory = False,
        allow_empty = True,
        default = [],
        doc = "List of checks to disable.",
    ),
    enable_checks = attr.string_list(
        mandatory = False,
        allow_empty = True,
        default = [],
        doc = "List of checks to enable.",
    ),
    custom_rules = attr.label_list(
        mandatory = False,
        allow_empty = True,
        allow_files = True,
        default = [],
        doc = "Custom lint rules to run.",
    ),
    _javabase = attr.label(
        default = "@bazel_tools//tools/jdk:current_java_runtime",
    ),
    _use_auto_exec_groups = attr.bool(default = True),
)
