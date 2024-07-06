# PSI Cookbook

<link-summary rel="excerpt"/>
<p id="excerpt">
本页面提供了与 PSI（Program Structure Interface）一起使用的最常见操作的技巧。
</p>

与[开发自定义语言插件](custom_language_support.md)不同，这是关于使用现有语言（如Java）的 PSI。

> 请参阅还有[](psi_performance.md)部分。
>

## 通用

### 如果我知道文件名但不知道路径，如何查找文件？

[`FilenameIndex.getFilesByName()`](%gh-ic%/platform/indexing-api/src/com/intellij/psi/search/FilenameIndex.java)

### 如何查找特定 PSI 元素的使用位置？

[`ReferencesSearch.search()`](%gh-ic%/platform/indexing-api/src/com/intellij/psi/search/searches/ReferencesSearch.java)

### 如何重命名一个 PSI 元素？

[`RefactoringFactory.createRename()`](%gh-ic%/platform/lang-api/src/com/intellij/refactoring/RefactoringFactory.java)

### 如何使虚拟文件的 PSI 重新构建？

[`FileContentUtil.reparseFiles()`](%gh-ic%/platform/analysis-api/src/com/intellij/util/FileContentUtil.java)

## Java 特定

> 如果您的插件依赖于 Java 功能并针对 2019.2 或更高版本，请参阅[](plugin_compatibility.md#java)。
> 如果您的插件支持其他 JVM 语言，也可以考虑使用[UAST](uast.md)。
>
{style="note"}

### 如何查找一个类的所有子类？

[`ClassInheritorsSearch.search()`](%gh-ic%/java/java-indexing-api/src/com/intellij/psi/search/searches/ClassInheritorsSearch.java)

### 如何按限定名查找类？

[`JavaPsiFacade.findClass()`](%gh-ic%/java/java-psi-api/src/com/intellij/psi/JavaPsiFacade.java)

### 如何按短名查找类？

[`PsiShortNamesCache.getClassesByName()`](%gh-ic%/java/java-indexing-api/src/com/intellij/psi/search/PsiShortNamesCache.java)

### 如何查找 Java 类的超类？

[`PsiClass.getSuperClass()`](%gh-ic%/java/java-psi-api/src/com/intellij/psi/PsiClass.java)

### 如何获取 Java 类所在包的引用？

```java
PsiJavaFile javaFile = (PsiJavaFile) psiClass.getContainingFile();
PsiPackage psiPackage = JavaPsiFacade.getInstance(project)
    .findPackage(javaFile.getPackageName());
```

或者

[`PsiUtil.getPackageName()`](%gh-ic%/java/java-psi-api/src/com/intellij/psi/util/PsiUtil.java)

### 如何查找覆盖特定方法的方法？

[`OverridingMethodsSearch.search()`](%gh-ic%/java/java-indexing-api/src/com/intellij/psi/search/searches/OverridingMethodsSearch.java)

### 如何检查 JVM 库的存在？

_2023.2_

使用[`JavaLibraryUtil`](%gh-ic%/java/openapi/src/com/intellij/java/library/JavaLibraryUtil.java)中的专用（且高度缓存的）方法：

- `hasLibraryClass()` 通过已知库类的完全限定名检查存在性
- `hasLibraryJar()` 使用 Maven 坐标（例如 `io.micronaut:micronaut-core`）检查存在性。

