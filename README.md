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

## 以资源的形式使用文件

应用程序需要使用不能被 **Android** 编译为资源 **ID** 的资源文件

应用程序可以在 **assets** 目录中保存需要读取的文件，如本地 **HTML** 文件、都好分隔值(**CSV**)或专有数据。**assets** 目录是 **Android** 应用程序的一个受保护文件目录。这个目录下的文件会和**APK**一起打包，并不会被处理或编译。和其他应用程序资源一样， **assets** 中的文件都是只读的。

虽然可以使用 **assets** 直接向小部件(如 **WebView**、**MediaPlayer**)中加载内容，但是，很多情况下，使用 **InputStream** 也能够很好的访问 **assets**。

例如有一文件 data.csv 存放在 assets 目录中，可用：

```
// 访问应用程序的 assets 目录
AssetManager manager = getAssets();
// 打开数据文件
InputStream mInput = manager.open("data.csv");
```
获得输入流来读取其中的数据。

**解析 CSV 数据**
逗号分隔值（**Comma-Separated Values，CSV**，有时也称为字符分隔值，因为分隔字符也可以不是逗号），其文件以纯文本形式存储表格数据（数字和文本）。解析这种文件是会获取整个文件并读入一个字节数组中，然后转换为单独的字符串进行处理。这种方式在需要处理数据量很大时并不是最省内存的，但是处理小文件还是比较合适的。

## 管理数据库

通过 SQLiteOpenHelper 的帮助创建一个 SQLiteDatabase 以管理数据存储。
SQLiteOpenHelper 可以管理数据库的创建和修改。这里还是创建数据库后设置初始值和默认值得最佳场所。
数据是以 **ContentValues** 对象的形式插入数据库中的。**SQLiteDatabase.insert()** 需要的参数为**数据库的表名**、**空字段填充**和代表插入记录的 **ContentValues**。
空字段填充是当传递给 **inset()** 的 **ContentValues** 是空的时，系统将会从这个参数中获取数据填充默认值。避免引用字段的内容是 **NULL**。

**数据库升级**
如果设备上的数据库版本和当前版本（即构造函数中传入的版本号）不一致，`onUpgrade()`就会被调用。**版本升级时，最好不要删除字段**。
以下为简单示例：

```
// 在 v1 的基础进行升级。添加电话号码字段
if (oldVersion <= 1) {
    db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN phone_number INTEGER;");
}
// 在 v2 的基础上进行升级。添加日期输入字段
if (oldVersion <= 2) {
    db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN entry_date DATE");
}
```
示例中，如果用户现在为版本1，两条语句都会执行。如果是版本2，那就只会执行后一条语句。这两种情况下，都会保留应用程序数据库中现有的数据。

## 查询数据库

使用`SQLiteDatabase.query()`查询，这个方法有好几种重载形式。以下面最复杂形式为例：

```
public Cursor query(String table, // 查询数据库表
        String[] columns, // 要访问的每条记录的列
        String selection, // 给定查询 SQL WHERE 子句
        String[] selectionArgs, // 如果 selection 语句中有问号，就用这里的元素逐个填充
        String groupBy, // 给定查询的 SQL GROUP BY 子句
        String having, // 给定查询的 SQL HAVING 子句
        String orderBy, // 给定查询的 SQL ORDER BY 子句
        String limit) // 查询返回结果的数量上限
```
以下为设置参数的例子：

* 返回其中的值能匹配给定参数的所有行：

```
String[] COLUMNS = new String[]{COL_NAME, COL_DATE};
String selection = COL_NAME + " = ?";
String[] args = new String[]{"NAME_TO_MATCH"};
Cursor result = db.query(TABLE_NAME, COLUMNS, selection, args, 
    null, null, null, null);
```

* 返回最近插入数据库的 10 行记录：

```
String[] COLUMNS = new String[]{COL_NAME, COL_DATE};
String orderBy = "_id DESC";
String limit = "10";
Cursor result = db.query(TABLE_NAME, COLUMNS, null, null, 
    null, null, orderBy, limit);
```

* 查询日期字段在指定范围内的行

```
String[] COLUMNS = new String[]{COL_NAME, COL_DATE};
String selection = "datetime(" + COL_DATE + ") > datetime(?)" +
    " AND datetime(" + COL_DATE + ") < datetime(?)";
String[] args = new String[]{"2000-1-1 00:00:00", "2018-09-25 23:59:59"};
Cursor result = db.query(TABLE_NAME, COLUMNS, selection, args,
    null, null, null, null);
```

在 **SQLite** 中创建表时可以声明 **DATE** 类型，但实际上 **SQLite** 并没有专门的日期类型数据。可以用标准的 **SQL** 日期和时间函数，以 **TEXT**、**INTEGER** 或 **REAL** 的形式表示日期和时间。这里是将数据库中的值和已经格式化的开始和结束时间字符串传递给 **datetime()**，然后比较返回的值。

* 返回整形字段在指定范围内的行

```
String[] COLUMNS = new String[]{COL_NAME, COL_AGE};
String selection = COL_AGE + " > ? AND " + COL_AGE + " < ?";
String[] args = new String[]{"7", "10"};
Cursor result = db.query(TABLE_NAME, COLUMNS, selection, args,
    null, null, null, null);
```

## 备份数据

设备外部存储器可以安全的保存数据库和其他文件的副本。因为外部存储器一般是物理可拆卸的，可以移动到另一台设备上进行恢复。或挂在到计算机上，进行数据传输。

**具体实现**
主要用到了文件复制方法

```
import java.nio.channels.FileChannel;

private void fileCopy(File source, File dest) throws IOException {
    FileChannel inChannel = new FileInputStream(source).getChannel();
    FileChannel outChannel = new FileOutputStream(dest).getChannel();
    try {
        inChannel.transferTo(0, inChannel.size(), outChannel);
    } finally {
        if (inChannel != null) {
            inChannel.close();
        }
        if (outChannel != null) {
            outChannel.close();
        }
    }
}
```

用到了 **Java** **NIO** 的 **transferTo()** 方法，该方法效率较文件流读取更高！更大文件差别更明显。

## 分享数据库

将应用程序的数据库内容提供给设备上的其他应用程序使用。

创建 **ContentProvider** 作为应用程序数据的外部接口。**ContentProvider** 可以通过与数据库接口雷瑟的 **query()**、**insert()**、**update()**和 **delete()**方法将特定的数据集暴露给外部请求。接口与实际数据模型之间的映射关系可以灵活定制。
要操作的数据集通常会编码为**Uri**，然后传递给 **ContentProvider**。例如，发送 **content://com.liuuuu.myprovider/friends** 这样的查询 **Uri** 就是告诉提供程序返回 **friends** 表中的数据，而 **content://com.liuuuu.myprovider/friends/15** 则意味着查询结果中返回 **id** 为 15 的记录。所以我们必须确保创建的 **ContentProvider** 符合这个规范。因为它本身并没有提供这种功能。
创建 FriendProvider，并完成清单声明

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <application ...>
        <provider
            android:name=".FriendProvider"
            android:authorities="com.examples.datapersistdemo.friendprovider"
            android:enabled="true"
            android:exported="true"/>
    </application>

</manifest>
```

**android:authorities**最好用包名加类名全小写，方便记忆。

在 **Provider** 类中:

* 定义 **Uri** 需要用到这个属性来访问它

```
public static final Uri CONTENT_URI =
    Uri.parse("content://com.examples.datapersistdemo.friendprovider/friends");
```
* 添加 **UriMatcher** 类会将 **Uri** 和设定好的一组模式进行比较，然后以整形形式返回匹配模式，如果没找到，就返回 **UriMatcher.NO_MATCH**。

```
/* 匹配 Uri */
private static final int FRIEND = 1;
private static final int FRIEND_ID = 2;

private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

static { // 添加匹配模式
    matcher.addURI(CONTENT_URI.getAuthority(), "friends", FRIEND);
    matcher.addURI(CONTENT_URI.getAuthority(), "friends/#", FRIEND_ID);
}
```
1. **UriMatcher.match()** 会将 Uri 与设定好的一组模式进行比较
2. **UriMatcher.addURI()** 会对一组模式进行初始化，这些工作最好在静态块中完成，这样在刚加载时就会初始化。
3. 在提供模式中可以使用**两个通配符**：用来匹配数字的(#)和用来匹配任意文本的(*)

##### 创建一个访问共享数据库的应用：

在 Activity 中实现`LoaderManager.LoaderCallbacks<Cursor>`接口，再在 onCreate()中调用`getLoaderManager().initLoader(id, null, this);`其中 ID 为任意值。

LoaderCallbacks接口三个方法实现：

```
// 在创建时直接传入查询所有的 Uri
// Uri: "content://com.liuuuu.datapersistdemo.friendprovider/friends"
// 从而获得相应的数据
@Override
public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String[] projection = new String[]{Columns._ID, Columns.FIRST};
    return new CursorLoader(this, CONTENT_URI, projection, null, null, null);
}

// 加载结束时，返回相关数据
@Override
public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    mAdapter.swapCursor(data);
}

@Override
public void onLoaderReset(Loader<Cursor> loader) {
    mAdapter.swapCursor(null);
}
```

传入 ID 对 friends 表进行查询如下：

```
// 对查询URI进行加工:
//  "content://com.liuuuu.datapersistdemo.friendprovider/friends/id"
Uri uri = Uri.withAppendedPath(CONTENT_URI, id);
// 设置要查询的列
String[] projection = new String[]{Columns.FIRST, Columns.LAST, Columns.PHONE};
// 得到全部记录
Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
// 可能有多条记录，将游标移植首位，获得第一条数据
cursor.moveToFirst();
```