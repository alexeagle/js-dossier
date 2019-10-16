load("@npm//google-closure-compiler:index.bzl", "google_closure_compiler")

def closure_js_library(name, deps = [], **kwargs):
    native.filegroup(
        name = name,
        srcs = deps,
        visibility = kwargs["visibility"]
    )

def closure_js_binary(name, **kwargs):
  srcs = kwargs.pop("deps", [])
  entry_points = kwargs.pop("entry_points", [])
  args = [
      "--js=$(location %s)" % s for s in srcs
  ] + kwargs.pop("defs", [])
  if "compilation_level" in kwargs:
      args.append("--compilation_level=" + kwargs.pop("compilation_level"))
  dependency_mode = kwargs.pop("dependency_mode", "")
  language = kwargs.pop("language", "")
  output_wrapper = kwargs.pop("output_wrapper", "")

  google_closure_compiler(
      name = name,
      data = srcs,
      args = args + [
          "--js_output_file=$(location %s.compiled.js)" % name,
      ],
      outs = ["%s.compiled.js" % name],
      **kwargs
  )


def closure_js_binary_set(name, **kwargs):
  """Defines two closure_js_binary targets for name.
  
  The first, :name, builds with ADVANCED optimizations.
  The second, :name_simple, builds with SIMPLE optimizations and pretty printed code (for easier
  debugging).
  """
  closure_js_binary(
      name = name,
      compilation_level = "ADVANCED",
      **kwargs)

  defs = kwargs.get("defs", [])
  defs.append("--formatting=PRETTY_PRINT")

  closure_js_binary(
      name = name + "_simple",
      compilation_level = "SIMPLE",
      **kwargs)
