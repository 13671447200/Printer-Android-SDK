# Printer-Android-SDK

[![](https://jitpack.io/v/13671447200/Printer-Android-SDK.svg)](https://jitpack.io/#13671447200/Printer-Android-SDK)

简介说明：

①：封装了Android的原生蓝牙，让开发者可以一键搜索、一键配对、封装了蓝牙的写入与读取、封装了蓝牙写入之后马上读取的方法。同时，处理多线程的问题。

②：当前SDK基于蓝牙 封装了TSPL、ESC指令的打印方式。其中TSPL指令采取小图打印方式，在小图打印中，处理了打印方向，图片旋转等坐标运算，开发者可单单传入参数，剩下的交给本SDK来处理。同时，小图打印的速度可比全图打印快三倍以上。ESC指令封装了字体大小、粗体、图片打印、多张图片打印等等功能。

③：打印图片更是封装了普通的二值化与基于Floyd Steinberg的错误抖动算法，让你可以一键打印出迷惑你的眼睛的黑白图片。

④：当前SDK，还支持得到打印机的信息。连接蓝牙之后，可一键得到打印机的序列号、型号、纸张类型、纸张状态、指令类型。

## 如何使用？
  第一步：在项目更目录的gradle文件，增加jitpack 的依赖 <br>
  ```java
	  allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	  }
  ```
  
  第二步：在项目需要的地方，添加下面依赖<br>
   ```java
	  dependencies {
		implementation 'com.github.13671447200:Printer-Android-SDK:V1.1.4'
	  }
  ```
  
  详细文档说明下载地址：https://www.mhtclouding.com/MHT/MHTFile/other/PrinterAndroidSDK.pdf

