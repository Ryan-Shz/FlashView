# FlashView

[ ![Download](https://api.bintray.com/packages/ryan-shz/Ryan/flashview/images/download.svg) ](https://bintray.com/ryan-shz/Ryan/flashview/_latestVersion)![](https://img.shields.io/badge/license-MIT-green)

100%还原今日头条Loading Flash效果，可以引入自己的项目中，换张类似的图就OK。

## 效果图
![](sample.gif)

## Quick Start

### 导入

```
implementation 'com.ryan.github:flashview:1.0.0'
```

### 使用

```
FlashView flashView = findViewById(R.id.flash_view);
// 设置加载图片
flashView.setImage(R.drawable.flash);
// 开始加载
flashView.start();
// 停止加载
flashView.stop();
```