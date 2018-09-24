# 数据持久化项目

## 制作首选项界面

通过`PreferenceActivity`和`XML Preference`层次结构就可以一次性解决用户界面、键/值组合以及数据持久化的问题。
在`XML`内部，可以借用`PreferenceScreen`、`PreferenceCategory`及相关`Preference`元素显示和按类别分组这些设置，而`Activity`只需要很少的代码就可以加载这个 XML 文件并呈现给用户。

Preference 资源文件一般放在 xml 文件目录中。每个 Preference 对象都提供了 android:key 属性，并没有 android:id 。另外 PreferenceActivity 有一个`findPreference()`方法，可以通过 Java 代码获得一个指向已经填充的 Preference 的引用，它比`findViewById()`效率更高，它的参数也是键。

PreferenceActivity 中只需调用`addPreferencesFromResource(R.xml.settings)`就会填充XML并管理列表中的内容。还有，也可以提供含有`ListView`的自定义布局，同时设置好`ListView`的`android:id="@android:id/list"`属性，这样PreferenceActivity也能从中加载首选项条目。
内嵌PreferenceScreen包含一个`android:dependency`属性，它根据另一个首选项的状态决定是否需要启动这个首选项界面。
如果需要在应中预先加载默认的设置值，可以调用:
`PreferenceManager.setDefaultValues(Context context, int resId, boolean readAgain);`
它或将首选项保存到默认的共享首选项对象中，而共享首选项对象可以通过
`PreferenceManager.getDefaultSharedPreferences(Context context);`
来访问

从 API 11 开始以**PreferenceFragment**的形式引入了一种新的创建首选项界面的方式。用它来替代**PreferenceActivity**。

## 自定义首选项

框架提供的 **Preference** 元素灵活度不够，需要自定义UI来修改值。
扩展 **Preference** 或它的一个子类，框架中的首选项如 **CheckBoxPreference** 只是在每次点击时切换持久化状态。其他首选项如 **EditTextPreference** 或 **ListPreference** 是 **DialogPreference** 的子类，它们使用点击事件显示对话框，为更新给定的设置提供更加复杂的UI。

在自定义 Preference 中，可以执行的第二组重写操作处理数据的检索和持久化：
> **onGetDefaultValue():** 从首选项 **XML** 定义中读取 **android:defaultValue** 属性。使用对首选项值有意义的类型化方法来接收存储该属性的 **TypedArray** 以及获得该值所需的索引。
> **onSetInitialValue():** 在本地设置次首选项实例的值。**restorePersistedValue** 标志表明该值是应该来自 **SharedPreferences**，还是来自默认值。默认值参数是从 **onGetDefaultValue()** 返回的值。

首选项需要**读取**保存在 **sharedPreferences** 中的当前值时，可以调用某个类型化的 **getPersistedXxx()** 方法，如: **getPersistedInt()**，返回首选项正在持久化的值类型(integer、boolean、string 等)。相反，要**保存**新值时，可以使用类型持久化的 **persistXxx()** 方法，更新 **SharedPreferences**。

## 简单数据存储

以简单低开销的方式持久存储一些基本数据。通过 **SharedPreferences** 对象，应用可以快速创建一个或多个存储位置，稍后可以在这些位置保存或查询数据。实际，这些对象以 XML 文件的形式存储在应用程序的用户数据区中。
最好创建多个 **SharedPreferences** 对象，如用户退出时需要删除的数据，保存在单独创建的首选项对象中，删除只需要调用 `SharedPreferences.Editor.clear()`。

**实现**
当创建 **Activity** 时，通过 `Activity.getPreferences()` 获得一个 **SharedPreferences** 对象，多有持久化数据都会保存在这里。因为所有操作都是在私有的首选项对象中进行的，所以清空这些数据，并不会影响以其他方式保存的用户设置。

**创建通用的 SharedPreferences**
前面是单独的 Activity 中使用单个的 **SharedPreferences** 对象，该对象通过 `Activity.getPreferences()` 得到。实际上，这个方法只是 `Context.getSharedPreferences()` 的一个为方便使用而实现的封装方法，使用 **Activity** 的名称作为首选项的保存名称。为实现多个 **Activity** 实例共享，需要调用 `getSharedPreferences()` 并传入响应名称。

**关于模式的说明**
`Context.getSharedPreferences()`回接受一个模式参数。**MODE_PRIVAT** 代表默认行为，只允许创建这个 **SharedPreferences** 的应用程序对该 **SharedPreferences** 进行读写。还有其他模式参数：**MODE_WORLD_READABLE** 和 **MODE_WORLD_WRITEABLE**。这两个模式允许其他应用程序访问首选项。不过，外部应用程序也需要有一个指向创建首选项文件的包的有效 **Context** 来访问文件。

```
Context ohterContext = createPackageContext("packageName", 0);

SharedPreferences externalPreferences = otherContext
                        .getSharedPreferences(PREF_NAME, 0);
```

## 读写文件

**Android** 支持所有标准 **Java** 文件 **I/O API**，用于执行创建、读取、更新和删除操作。应用程序可以在三个主要位置操作文件：
* **内部存储**：受保护的用于读写文件数据的目录空间。
* **外部存储**：外部挂载的用于读写文件数据的空间。**API Level 4** 以上需要 **WRITE_EXTERNAL_STORAGE** 权限。通常是设备 **SD** 卡。
* **Assets**：**APK** 包中只读的受保护空间，用于放置不能/不应该被编译的本地资源。

**实现机制**
传统 **Java** 类 **FileInputStream** 和 **FileOutputStream** 为主。

内部存储
在内部存储中中创建和修改文件的位置，可以使用 `Context.openFileInput()`和`Context.openFileOutput()`方法。*这两个方法需要文件名称作为参数，而不是文件的完整路径*。它们会引用应用程雪受保护目录空间中的文件，并不会关心设备的确切文件目录位置。

外部存储
内部与外部存储最大区别是外部存储器是可挂载的。所以用户可以将设备连接至电脑然后将设备的外部存储器挂载到计算机上。
需要注意添加：**android.permission.WRITE_EXTERNAL_STORAGE**权限
当外部存储器被挂载到其他设备上时，应用程序不可访问它。因此，最好经常使用 `Environment.getExternalStorageState()`先检查一下外部存储器是否可用，若该状态**不等于**，`Environment.MEDIA_MOUNTED`(未安装)就意味外部存储器不可写入。

写入文件时的注意事项
FileOutputStream 这样的 Java API 没有与内核中的原生文件描述符共享1:1的关系。通常，使用 write()将数据写入流时，数据会直接写入文件的内存缓冲区并异步写出磁盘。在大多数情况下，只要严格在Dalvik虚拟机中进行文件访问，不会出现刚刚写入的文件，并且读取时没有任何问题。然而，当移动设备处理移动存储设备(如SD卡)时，我们要保证文件数据沿着各种途径到达文件系统，之后再返回一些操作给用户，因为用户有能力物理的移除存储介质。以下为写入外部文件时使用的良好标准代码块：

```
// 写入数据
out.write();
// 清空流缓冲区
out.flush();
// 同步所有数据到文件系统
mOutput.getFD().sync();
// 关闭流
mOutput.close();
```

外部系统目录
Environment和Context的其他方法也能够访问外部存储器上其他一些标准的位置，在这些位置可以写入一些特殊文件。它们有一些特殊属性
* Environment.getExternalStoragePublicDirectory(String type)
    * API Level 8
    * 返回一个所有应用程序保存媒体文件的通用目录。该目录对用户和其他应用程序都可见。特别是，Gallery这样的应用程序，这里的媒体文件可能会被扫描加入到设备的MediaStore中。
    * type参数的有效值可以为**DIRECTORY_PICTURES**、**DIRECTORY_MUSIC**、**DIRECTORY_MOVIES**和**DIRECTORY_RINGTONES**。
* Context.getExternalFilesDir(String type)
    * API Level 8
    * 返回外部存储器上的媒体文件目录，但只使用与某个应用程序。这里的媒体文件不是公开的，并不会出现在 MediaStore 中。
    * 由于该目录在外部存储器上，因此其他用户和应用程序时可以看到并直接编译文件：并不是强制安全的。
    * 放在这里的文件在卸应用程序时会被删除，因此对于应用程序需要使用大内容文件而同时又不希望保存到内部存储器的场景，这里是绝佳去处。
    * type参数的有效值可以为**DIRECTORY_PICTURES**、**DIRECTORY_MUSIC**、**DIRECTORY_MOVIES**和**DIRECTORY_RINGTONES**。
* Context.getExternalCacheDir()
    * API Level 8
    * 返回一个外部存储器上供某一个应用程序特有临时文件使用的目录。这个目录的内容对用户和其他应用程序都是看见的。
    * 放在这里的文件在卸应用程序时会被删除，因此对于应用程序需要使用大内容文件而同时又不希望保存到内部存储器的场景，这里是绝佳去处。
* Context.getExternalFileDirs()和Context.getExternalCacheDirs()
    * API Level 19
    * 与前面描述的对应属性有同等的功能，但是返回设备上每个存储卷的路径列表(主卷和任意副卷)。
    * 例如，某个设备可采用一块内置闪存为主要外部存储器，同时使用移动 SD 卡作为次要外部存储器。
* Context.getExternalMediaDirs()
    * API Level 21
    * 放在这些卷中的文件将自动被扫描并添加到设备的介质存储，以便显示给其他应用程序。这些文件通常也通过核心应用程序(如 Gallery)对用户看见。
