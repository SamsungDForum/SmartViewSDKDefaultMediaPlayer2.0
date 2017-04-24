# SmartViewSDK Default Media Player 2.0 Sample App #

##Prerequisite



###1. Library
1. Download library manually [SmartView SDK iOS framework](http://developer.samsung.com/tv/develop/tools/extension-libraries/smart-view-sdk-download):  iOS Package(Mobile)

	add smartview.framework
2. Download and install by [Cocoapods](https://cocoapods.org/pods/smart-view-sdk) (recommended)

		pod 'smart-view-sdk'
	**Note** Application project should be unlocked before fetching pod. To unlock open .xcodeproj in Xcode and click unlock and close. Refer [Cocoapods guide](https://cocoapods.org) for more.

###2. Build Environment
1. This sample app is developed using swift language.
2. Use 'smart-view-sdk' cocoapods. More information at "Install By Cocoapods" http://developer.samsung.com/tv/develop/extension-libraries/smart-view-sdk/sender-apps/ios-sender-app


###3. Recommendation for  iOS framework
1. This sample app includes Podfile with 'smart-view-sdk' as pod item
2. iphoneos+iphonesimulator library: works on devices and simulator( + i386,x86_64)

 **Note**: Apple App Store will reject your app  when you register your app with iphoneos+iphonesimulator framework.
 You need to run shell 'remove\_unused\_archs.sh' to remove unused architectures from the final binary. This script is in the iphoneos+iphonesimulator folder.
refer to: [Stripping Unwanted Architectures From Dynamic Libraries In Xcode](http://ikennd.ac/blog/2015/02/stripping-unwanted-architectures-from-dynamic-libraries-in-xcode/)

###4. Discover : Search devices around your mobile.
1. Pressing 'Cast' button in ActionBar, must start search API [search.start()].
2. Populate device list by overriding onFound() & onLost() listeners.
3. Stop device discovery, by calling stop search API [search.stop()].

### 5.Code Snippet with Examples:

```swift

	// Inside MediaShareController.swift file
	

         let search = Service.search()
         var services = [Service]()

		/* Start TV Discovery */
		 
            func searchServices() {
                 search.start()
             }

		/*
		 * Method to update (add) new service (tv).
         * event recieved when service(tv) found on Network.
		 */

        @objc func onServiceFound(_ service: Service) {
                services.append(service)
            }

        /*
        * Method to remove lost service (tv).
        * event recieved when service(tv) lost from metwork.
        */

        @objc func onServiceLost(_ service: Service) {
                removeObject(&services,object: service)
            }
		/* Stop TV Discovery */
		public void stopDiscovery() {
			if (null != search)
			{ 
               search.stop()
			}
		}

```

## Create [Video|Audio|Photo]Player object and launch a TV application.

1. Get 'service' instance from devices list.
2. Create an application instance using [Video|Audio|Photo]Player.
3. Now, content on the TV can be launched by calling player's respective API - PlayContent(Parameters).
Note: All players support PlayContent API but with their respective argument set. Below example shows use of Video Player APIs.

``` swift

        // Inside MediaShareController.swift file
	
        var videoplayer: VideoPlayer? = nil
        var videoPlaycontroller: VideoPlayerController? = nil

        /* create an video pLayer instance */

        func connect(_ service: Service) {
          search.stop()

          videoplayer = service.createVideoPlayer(appName)
          videoplayer?.connectionDelegate = self
        }
       
        /* Event recieved When a TV application connects to the channel */
        func onConnect(_ error: NSError?)
        {
            if (error != nil) {
            search.start()
            }
            isPlayerConnected = true
        }

        /* Event recieved When a TV application DisConnects from channel */
        func onDisconnect(_ error: NSError?)
        {
            if (isPlayerConnected)
            {
                NotificationCenter.default.post(name: Notification.Name(rawValue: "onDisconnect"), object: self, userInfo: nil)
                search.start()
                isPlayerConnected = false
            }

        }
        /* Share Content on TV */
         videoplayer?.playContent(URL(string :item.mediaUrl), title: item.mediaTitle ,thumbnailURL: URL(string :item.mediaimageUrl))

```

##Event Handling
1. Events for successful player's & client's connection/disconnection can be handled via overloading respective listeners.
2. To handle player events from TV following events are recieved at application end.

```swift

   //Inside VideoPlayerController.swift file


    func onBufferingStart()
    {
       MediaShareController.sharedInstance.playType = "video"
    }

    func onBufferingComplete()
    {

    }

    func onBufferingProgress(_ progress: Int)
    {

    }

    func onCurrentPlayTime(_ progress: Int)
    {
         if totalDuration != 0{
            NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyTimeProgress"), object: self, userInfo: ["progress":progress, "totalDuration":totalDuration])
        }
    }

    func onStreamingStarted(_ duration: Int)
    {
         totalDuration = duration
    }

    func onStreamCompleted()
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyStop"), object: self, userInfo: nil)
    }

    func onPlayerInitialized()
    {
        MediaShareController.sharedInstance.playType = "video"
    }

    func onPlayerChange(_ playerType: String)
    {
       MediaShareController.sharedInstance.playType = "video"
    }

    func onPlay()
    {
       NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyPlay"), object: self, userInfo: nil)
    }

    func onPause()
    {
       NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyPause"), object: self, userInfo: nil)
    }

    func onForward()
    {

    }

    func onRewind()
    {

    }

    func onMute()
    {

    }

    func onUnMute()
    {

    }

    func onStop()
    {
       NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyStop"), object: self, userInfo: nil)
    }

    func onGetVolume(_ volLevel: Int)
    {

    }

    func onVolumeChange(_ volLevel: Int)
    {

    }

    func onAddToList(_ enqueuedItem: [String: AnyObject])
    {

    }

    func onRemoveFromList(_ dequeuedItem: [String: AnyObject])
    {
       videoplayer?.getList()
    }

    func onClearList()
    {

    }

    func onGetList(_ queueList: [String: AnyObject])
    {
        MediaShareController.sharedInstance.tvQueueMediaCollection.removeAll()

        let items = queueList["data"] as! NSArray

        for inputItem in items {
        let mediaItem : Media = Media(url: "")

        if let uri = (inputItem as AnyObject).object(forKey: "uri")
        {
        if MediaShareController.sharedInstance.playType == "photo" {
        mediaItem.mediaimageUrl = uri as! String
        }
        else
        {
        mediaItem.mediaUrl = uri as! String
        }
        }
        if let title = (inputItem as AnyObject).object(forKey: "title")
        {
        mediaItem.mediaTitle = title as! String
        }
        if let albumName = (inputItem as AnyObject).object(forKey: "albumName")
        {
        mediaItem.mediaAlbumName = albumName as! String
        }
        if let albumArt = (inputItem as AnyObject).object(forKey: "albumArt")
        {
        mediaItem.mediaimageUrl = albumArt as! String
        }
        if let albumArt = (inputItem as AnyObject).object(forKey: "thumbnailUrl")
        {
        mediaItem.mediaimageUrl = albumArt as! String
        }
        if MediaShareController.sharedInstance.tvQueueMediaCollection.contains(mediaItem) == false
        {
        MediaShareController.sharedInstance.tvQueueMediaCollection.append(mediaItem)
        }
        }
        NotificationCenter.default.post(name: Notification.Name(rawValue: "TvlistRecieved"), object: self, userInfo: nil)

    }

    func onRepeat(_ mode: String)
    {
       NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyRepeat"), object: self, userInfo: ["repeatMode": mode])
    }

    func onCurrentPlaying(_ currentItem: [String: AnyObject])
    {
        if let url = currentItem["thumbnailUrl"]
        {
         let mediaImageUrl = url as! String
         NotificationCenter.default.post(name: Notification.Name(rawValue: "onThumbnailChange"), object: self, userInfo: ["url": mediaImageUrl])
        }

        NotificationCenter.default.post(name: Notification.Name(rawValue: "onPlay"), object: self, userInfo: nil)
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyPlay"), object: self, userInfo: nil)
    }

    func onError(_ error: NSError)
    {

    }

    func onApplicationResume()->Void
    {

    }

    func onApplicationSuspend()->Void
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "videoApplicationSuspend"), object: self, userInfo: nil)
    }

```
        
##API usage
1. Use respective player's APIs to send commands to TV(like play, pause, stop, mute, etc.).
2. List is maintained by TV of playing/to be played content. This list can be curated via APIs like addToList(), removeFromList, etc.

   //Inside MediaPlayViewController.swift file

```swift

    func previousAction(_ gestureRecognizer:UITapGestureRecognizer)
    {
       videoplayer?.previous()
    }

    func rewindAction(_ gestureRecognizer:UITapGestureRecognizer)
    {
      videoplayer?.rewind()
    }

    func playPauseAction(_ gestureRecognizer:UITapGestureRecognizer)
    {
        if(mediaPlay)
        {
          videoplayer?.pause()
        }
        mediaPlay = false
        mediaPause = true
        else if(mediaPause)
        {
           videoplayer?.play()
        }
        mediaPlay = true
        mediaPause = false
    
    }

    func forwardAction(_ gestureRecognizer:UITapGestureRecognizer)
    {
       videoplayer?.forward()
    }

    func nextAction(_ gestureRecognizer:UITapGestureRecognizer)
    {

      videoplayer?.next()
    }

    func muteUnmuteAction(_ gestureRecognizer:UITapGestureRecognizer)
    {
        if(mute)
        {
          videoplayer?.unMute()
        }

        else
        {
           videoplayer?.mute()
          mute = true
        }
    }

    func stopAction(_ gestureRecognizer:UITapGestureRecognizer)
    {
       videoplayer?.stop()
    }

    func repeatAction(_ gestureRecognizer:UITapGestureRecognizer)
    {
        videoplayer?.repeatQueue()
    }

    @IBAction func timeSliderValueChanged(_ sender: AnyObject)
    {
        let currentValue = sender.value as Float
        let currenTimeinSec = ((currentValue/100.0)*Float(totalMediaDuration))/1000.0

        videoplayer?.seek(Double(currenTimeinSec))
    }


    @IBAction func volumeSliderValueChanged(_ sender: AnyObject)
    {
        let currentValue = sender.value as Float
        let currenVolume = currentValue * 100

       videoplayer?.setVolume(UInt8(currenVolume))
    }
    func OnAppResume()
    {
       videoplayer?.resumeApplicationInForeground()
    }

```
		
##Handling Player Process State
Launching application(s) on TV (while default media player[DMP] is running on TV) may put DMP in suspended state. To resume DMP as foreground process use API resumeApplicationInForeground().


