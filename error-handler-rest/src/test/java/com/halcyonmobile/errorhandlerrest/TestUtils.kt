package com.halcyonmobile.errorhandlerrest

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

/**
 * Reads the content of a .json file into a string.
 *
 * @param fileName Name of the file from where the content will be read.
 */
fun Any.readJsonResourceFileToString(fileName: String): String =
    Files.lines(Paths.get(this::class.java.classLoader!!.getResource(fileName)!!.toURI().path))
        .collect(Collectors.joining())