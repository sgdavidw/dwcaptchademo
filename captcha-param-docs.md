# TencentCaptchaParam Class Documentation

## Overview
The `TencentCaptchaParam` class provides configuration options for customizing the Tencent Captcha behavior and appearance. This document outlines all available parameters and their usage.

## Builder Methods

### setBizState
```java
Builder setBizState(Object)
```
Sets custom pass-through parameters. This field allows businesses to pass small amounts of data that will be included in the callback object.
- **Parameter**: Object
- **Returns**: Builder

### setEnableDarkMode
```java
Builder setEnableDarkMode(boolean)
```
Enables or disables adaptive night mode.
- **Parameter**: boolean (true to enable)
- **Returns**: Builder

```java
Builder setEnableDarkMode(String)
```
Sets forced night mode.
- **Parameter**: String ("force")
- **Returns**: Builder

### setNeedFeedBack
```java
Builder setNeedFeedBack(boolean)
```
Controls the visibility of the help button.
- **Parameter**: boolean (false to hide)
- **Returns**: Builder

```java
Builder setNeedFeedBack(String)
```
Sets a custom help link.
- **Parameter**: String (URL)
- **Returns**: Builder

### setUserLanguage
```java
Builder setUserLanguage(String)
```
Specifies the language for Captcha prompt text. Takes precedence over console configuration.
- **Parameter**: String (navigator.language value, case-insensitive)
- **Returns**: Builder
- **Documentation**: See [language documentation](https://www.tencentcloud.com/zh/document/product/1159/49680?lang=zh&pg=#userLanguage)

### setEnableAged
```java
Builder setEnableAged(boolean)
```
Controls aging adaptation.
- **Parameter**: boolean (true to enable, false to disable)
- **Returns**: Builder

### setType
```java
Builder setType(String)
```
Defines the Captcha display method.
- **Parameter**: String
  - `"popup"`: (default) Displays Captcha in a centered floating layer
  - `"full"`: Full screen display
- **Returns**: Builder

### setLoading
```java
Builder setLoading(boolean)
```
Controls the visibility of the loading box during Captcha loading.
- **Parameter**: boolean
  - `true`: show loading box (default)
  - `false`: hide loading box
- **Returns**: Builder

### setAidEncrypted
```java
Builder setAidEncrypted(String)
```
Sets the CaptchaAppId encrypted verification string.
- **Parameter**: String
- **Returns**: Builder
- **Note**: See encryption verification capability access guide for details

### setOptionsCallback
```java
Builder setOptionsCallback(OptionsCallback)
```
Sets callback for internal Captcha information.
- **Parameter**: OptionsCallback
- **Returns**: Builder

## OptionsCallback Interface

The `OptionsCallback` interface provides information about Captcha events and dimensions.

### onCallback Method
```java
void onCallback(String optFuncName, String data)
```

#### Parameters
- `optFuncName`: String - The name of the callback function
- `data`: String - The callback data

#### Callback Types

1. **Ready Callback** (`optFuncName = "ready"`)
   - Returns actual Captcha dimensions in pixels
   ```json
   {
     "sdkView": {
       "width": number,
       "height": number
     }
   }
   ```
   - **Note**: These dimensions are for viewing only; do not use them directly for dimension setting

2. **Show Function Callback** (`optFuncName = "showFn"`)
   - Returns duration, rendering time, and sid information

## Usage Notes
- All builder methods return the Builder instance for method chaining
- Some parameters have multiple overloads (e.g., setEnableDarkMode, setNeedFeedBack)
- The loading box is shown by default if not explicitly configured
- Dimension information from the ready callback should not be used directly for setting dimensions
