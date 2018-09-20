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

