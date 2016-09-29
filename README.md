#DefaultMediaPlayer2.0
This application demonstrates how to send a **video,audio,photo content** URL to a default media player app on TV and how to control the playback and make a list of the content from mobile.


*  Refer to SmartviewSDK Guideline:
  * [DMP2.0 sender guide](https://www.samsungdforum.com/TizenGuide/tizen4941/index.html)
  * [DMP2.0 UX guide](https://www.samsungdforum.com/TizenUxGuide/07/07_Smart_View_enabled_Apps_(for_Samsung_Smart_View_SDK).html#ux-10)
  * [DMP2.0 Media Spec of 2016 TV](https://www.samsungdforum.com/Tizen/Spec#2016TVSpec)

##Sender (mobile)

### Discover devices

1.Push discover button in ActionBar, then Smart View SDK searches devices around your mobile.

![](/DMP_ScreenShot/SmartViewSDK_Screenshot_00.png)

###  Videos

If you want cast to DMP video content, refer to Videos tabs

![](/DMP_ScreenShot/SmartViewSDK_Screenshot_01.png)

also you can make 'playing video content list' using queueing API)

![](/DMP_ScreenShot/SmartViewSDK_Screenshot_01-1.png)

DMP2.0 is launched and play your video content.

![](/DMP_ScreenShot/SmartViewSDK_Screenshot_01-2_TV.png)

###  Audios

If you want cast to DMP audio content, refer to Audios tabs

![](/DMP_ScreenShot/SmartViewSDK_Screenshot_02.png)

also you can make 'playing audio content list' using queueing API)

![](/DMP_ScreenShot/SmartViewSDK_Screenshot_02-2.png)

DMP2.0 is launched and play your audio content.

![](/DMP_ScreenShot/SmartViewSDK_Screenshot_02-2_TV.png)

###  Photos

If you want cast to DMP image content, refer to Photos tabs

![](/DMP_ScreenShot/SmartViewSDK_Screenshot_03.png)

also you can make 'playing images content list' using queueing API)

![](/DMP_ScreenShot/SmartViewSDK_Screenshot_03-2.png)

DMP2.0 is launched and play your image content.

![](/DMP_ScreenShot/SmartViewSDK_Screenshot_03-2_TV.png)

You can set backgorund music, see **setBackgroundMusic(Uri uri)**

### Support Multitasking 

If player is sent to background by any other process or otherwise, android library receives an event – **onApplicationSuspend()**. 

To bring the player to foreground (from suspended to active state), API - **resumeApplicationInForeground()** can be used.

Event **onApplicationResume()** is received when application is successfully brought to foreground.


![](/DMP_ScreenShot/SmartViewSDK_Screenshot_04.png)


## Disconnect

 ![](/DMP_ScreenShot/SmartViewSDK_Screenshot_05.png)


