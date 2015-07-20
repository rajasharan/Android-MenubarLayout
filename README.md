# Android Menubar Layout
An android layout to easily add menubar style navigation

## Demo
![](/screencast.gif)

## Layout Usage
Add layout files using `<include>` tag and give it a `app:menu_name`
([menubar_layout.xml](/demo/src/main/res/layout/menubar_layout.xml))
```xml

<com.rajasharan.layout.MenubarLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >

    <include layout="@layout/activity_main"
        app:menu_name="First"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />

    <include layout="@layout/single_image"
        app:menu_name="Image"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />

</com.rajasharan.layout.MenubarLayout>
```

## [License](/LICENSE)
    The MIT License (MIT)
