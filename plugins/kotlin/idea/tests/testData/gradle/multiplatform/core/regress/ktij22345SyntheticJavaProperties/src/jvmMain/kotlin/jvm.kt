//region Test configuration
// - hidden: line markers
//endregion
@OptIn(kotlin.ExperimentalMultiplatform::class)
@AllowDifferentMembersInActual
actual open class MyFile1 actual constructor(userPath: String): MyJavaFile(userPath)

@OptIn(kotlin.ExperimentalMultiplatform::class)
@AllowDifferentMembersInActual
actual typealias MyFile2 = java.io.File

fun testFile1(file: KotlinFile1) {
    file.getAbsolutePath()
    file.absolutePath
    file.isAbsolute()
    file.isAbsolute
    file.getAbsoluteFile()
    file.absoluteFile
}

fun testFile2(file: KotlinFile2) {
    file.getAbsolutePath()
    file.absolutePath
    file.isAbsolute()
    file.isAbsolute
    file.getAbsoluteFile()
    file.absoluteFile
}
