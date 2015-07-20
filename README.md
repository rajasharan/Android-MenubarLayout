# Android Menubar Layout
An android layout to easily add menubar style navigation

## Demo
![](/screencast.gif)

## Layout Usage
Add your layout files using `<include>` tag and give it an `app:menu_name`. Each of them will be picked up by the `MenubarLayout` and shown in the menu selector.
([menubar_layout.xml](/demo/src/main/res/layout/menubar_layout.xml))
```xml

<com.rajasharan.layout.MenubarLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    >

    <include layout="@layout/profile"
        app:menu_name="Profile"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />

    <include layout="@layout/address"
        app:menu_name="Address"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />

    <include layout="@layout/office"
        app:menu_name="Workplace"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        />

</com.rajasharan.layout.MenubarLayout>
```

## [License](/LICENSE)
    The MIT License (MIT)
