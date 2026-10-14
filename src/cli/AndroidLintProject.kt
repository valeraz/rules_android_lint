package com.rules.android.lint.cli

import java.io.StringWriter
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.io.path.absolutePathString
import kotlin.io.path.pathString

internal fun createProjectXMLString(
  moduleName: String,
  rootDir: String,
  library: Boolean = false,
  srcs: List<Path>,
  resources: List<Path>,
  testSrcs: List<Path> = emptyList(),
  androidManifest: Path?,
  mergedManifest: Path? = null,
  classpathJars: List<Path>,
  classpathAars: List<Path>,
  classpathExtractedAarDirectories: List<Pair<Path, Path>>,
  customLintChecks: List<Path>,
): String {
  val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()

  val projectElement = document.createElement("project")
  document.appendChild(projectElement)

  document.createElement("root").also {
    it.setAttribute("dir", rootDir)
    projectElement.appendChild(it)
  }

  val moduleElement =
    document.createElement("module").also {
      it.setAttribute("name", moduleName)
      it.setAttribute("android", if (androidManifest != null) "true" else "false")
      it.setAttribute("library", if (library) "true" else "false")
      // it.setAttribute("compile-sdk-version", "get-actual-value-here")
      projectElement.appendChild(it)
    }

  customLintChecks.forEach { jar ->
    document.createElement("lint-checks").also {
      it.setAttribute("jar", jar.absolutePathString())
      projectElement.appendChild(it)
    }
  }

  srcs.forEach { src ->
    val element = document.createElement("src")
    element.setAttribute("file", src.pathString)
    moduleElement.appendChild(element)
  }

  testSrcs.forEach { src ->
    val element = document.createElement("src")
    element.setAttribute("file", src.pathString)
    element.setAttribute("test", "true")
    moduleElement.appendChild(element)
  }

  resources.forEach { res ->
    val element = document.createElement("resource")
    element.setAttribute("file", res.pathString)
    moduleElement.appendChild(element)
  }

  if (androidManifest != null) {
    val element = document.createElement("manifest")
    element.setAttribute("file", androidManifest.pathString)
    moduleElement.appendChild(element)
  }

  // Without a merged manifest lint merges the dependency AAR manifests into the module's own, and
  // ManifestMerger2 rejects that for libraries ("Cannot add library dependencies manifests when
  // creating a library"), so a library falls back to its own manifest.
  val effectiveMergedManifest = mergedManifest ?: androidManifest.takeIf { library }
  if (effectiveMergedManifest != null) {
    val element = document.createElement("merged-manifest")
    element.setAttribute("file", effectiveMergedManifest.pathString)
    moduleElement.appendChild(element)
  }

  classpathJars.forEach { jar ->
    val element = document.createElement("classpath")
    element.setAttribute("jar", jar.absolutePathString())
    moduleElement.appendChild(element)
  }

  classpathAars.forEach { aar ->
    val element = document.createElement("aar")
    element.setAttribute("file", aar.absolutePathString())
    moduleElement.appendChild(element)
  }

  classpathExtractedAarDirectories.forEach { (aar, unzippedDir) ->
    val element = document.createElement("aar")
    element.setAttribute("file", aar.absolutePathString())
    element.setAttribute("extracted", unzippedDir.absolutePathString())
    moduleElement.appendChild(element)
  }

  return StringWriter()
    .apply {
      val transformer = TransformerFactory.newInstance().newTransformer()
      transformer.setOutputProperty(OutputKeys.INDENT, "yes")
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
      transformer.transform(DOMSource(document), StreamResult(this))
    }.buffer
    .toString()
}
