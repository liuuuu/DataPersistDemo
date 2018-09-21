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

