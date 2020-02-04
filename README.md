<p align="center">
  <img src="https://raw.githubusercontent.com/lyrebirdstudio/filebox/master/art/icon_filebox.png">
</p>


## What is FileBox

FileBox is a async file downloader library for Android. 

Before we develop filebox, we though that URL content changes very rarely. So basically this library downloads given URL, and if it is already downloaded, provides downloaded content directly.

FileBox;

* Shares ongoing download request to the observers. So It reduces Data Usage.
* Has TTL(Time To Live) duration. Filebox re-download the URL if TTL is expired.
* Supports both Cache and External directory.
* Supports the File Encryption for sensitive URL(images, videos, any file).
* Allows you to create custom folder destination.
* Clears unreliable data automagically. 
* Does Etag Check. Filebox don't download the file again If file's TLL is up but the file has not changes.
* Supports Multiple Download. If you have N file and want to get notified when all completed.
* Runs on application scope. There is no pause/resume continuation support.

## Modules
<p align="center">
  <img src="https://raw.githubusercontent.com/lyrebirdstudio/filebox/master/art/filebox_modules.png">
</p>


## Flow Diagram
<p align="center">
  <img src="https://raw.githubusercontent.com/lyrebirdstudio/filebox/master/art/diagram_filebox.png">
</p>

## Basic Usage
```kotlin
val fileBoxRequest = FileBoxRequest("https://url1.png")

FileBoxProvider.newInstance(applicationContext, FileBoxConfig.createDefault())
    .get(fileBoxRequest)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe { fileBoxResponse ->
        when (fileBoxResponse) {
            is FileBoxResponse.Downloading -> {
                val progress: Float = fileBoxResponse.progress
                val ongoingRecord: Record = fileBoxResponse.record
            }
            is FileBoxResponse.Complete -> {
                val savedRecord: Record = fileBoxResponse.record
                val savedPath = fileBoxResponse.record.getReadableFilePath()
            }
            is FileBoxResponse.Error -> {
                val savedRecord: Record = fileBoxResponse.record
                val error = fileBoxResponse.throwable
            }
        }
    }
```

## Customize Config

```kotlin
val fileBoxConfig = FileBoxConfig.FileBoxConfigBuilder()
        .setCryptoType(CryptoType.CONCEAL)
        .setTTLInMillis(TimeUnit.DAYS.toMillis(7))
        .setDirectory(DirectoryType.CACHE)
        .setFolderName("MyPhotos")
        .build()
```

## Multiple Download Request

```kotlin
val fileBoxMultipleRequest = FileBoxMultiRequest(
            arrayListOf(
                FileBoxRequest("https://url1.png"),
                FileBoxRequest("https://url2.zip"),
                FileBoxRequest("https://url3.json"),
                FileBoxRequest("https://url4.mp4")
            )
        )

FileBoxProvider.newInstance(applicationContext, FileBoxConfig.createDefault())
    .get(fileBoxMultipleRequest)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe { fileBoxResponse ->
        when (fileBoxResponse) {
            is FileBoxMultiResponse.Downloading -> {
                val progress = fileBoxResponse.progress
                val ongoingFileResponseList = fileBoxResponse.fileBoxResponseList
            }
            is FileBoxMultiResponse.Complete -> {
                val ongoingFileResponseList = fileBoxResponse.fileBoxResponseList
                ongoingFileResponseList.forEach { it.record.getReadableFilePath() }
            }
            is FileBoxMultiResponse.Error -> {
                val error = fileBoxResponse.throwable
                val ongoingFileResponseList = fileBoxResponse.fileBoxResponseList
            }
        }
    }
```

## Destroy

```kotlin
filebox.destroy()
```

# Setup
```groovy
allprojects {
     repositories {
	...
	maven { url 'https://jitpack.io' }
     }
}
```
```groovy
dependencies {
      implementation 'com.github.lyrebirdstudio:filebox:0.1'
}
```

License
--------


    Copyright 2020 Lyrebird Studio.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.








