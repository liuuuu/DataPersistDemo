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