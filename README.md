# MarkdownView
[![](https://jitpack.io/v/SWRevo/MarkdownView.svg)](https://jitpack.io/#SWRevo/MarkdownView)
## Gradle

Add the following to your `build.gradle` to use:
```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
	  implementation 'com.github.SWRevo:MarkdownView:Tag'
}
```


## Usage

Define in xml:

```xml
<id.indosw.markdownviewlib.MarkdownView
        android:id="@+id/mark_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:escapeHtml="false" />
```

Code:

```java
    private final InternalStyleSheet mStyle = new Github();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Cria o bean.
        MyBean myBean = new MyBean();
        myBean.setHello("Olá");
        myBean.setDiasDaSemana(MyBean.DiasDaSemana.DOMINGO);

        MarkdownView mMarkdownView = findViewById(R.id.mark_view);
        mMarkdownView.addStyleSheet(mStyle);
        //http://stackoverflow.com/questions/6370690/media-queries-how-to-target-desktop-tablet-and-mobile
        mStyle.addMedia("screen and (min-width: 320px)");
        mStyle.addRule("h1", "color: green");
        mStyle.endMedia();
        mStyle.addMedia("screen and (min-width: 481px)");
        mStyle.addRule("h1", "color: red");
        mStyle.endMedia();
        mStyle.addMedia("screen and (min-width: 641px)");
        mStyle.addRule("h1", "color: blue");
        mStyle.endMedia();
        mStyle.addMedia("screen and (min-width: 961px)");
        mStyle.addRule("h1", "color: yellow");
        mStyle.endMedia();
        mStyle.addMedia("screen and (min-width: 1025px)");
        mStyle.addRule("h1", "color: gray");
        mStyle.endMedia();
        mStyle.addMedia("screen and (min-width: 1281px)");
        mStyle.addRule("h1", "color: orange");
        mStyle.endMedia();
        mMarkdownView.setBean(myBean);
        mMarkdownView.loadMarkdownFromAsset("markdown1.md");
    }

```
