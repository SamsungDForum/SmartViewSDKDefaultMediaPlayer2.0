/*

Copyright (c) 2014 Samsung Electronics

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

*/

import Foundation
import AssetsLibrary
import SmartView

class PhotoPlayerController: NSObject, PhotoPlayerDelegate {

    private var isPlayerAlreadyInit = true
    override init ()
    {
        super.init()
        NotificationCenter.default.addObserver(self, selector: #selector(PhotoPlayerController.OnAppResume), name: NSNotification.Name(rawValue: "photoApplicationResume"), object: nil)
    }
    func onPlayerInitialized(){
    
        isPlayerAlreadyInit = true
        
        print("onPlayerInitialized")
        if MediaShareController.sharedInstance.settingsValue.showStandbyScreen
        {
            MediaShareController.sharedInstance.videoplayer?.setPlayerWatermark(URL(string:MediaShareController.sharedInstance.settingsValue.watermarkURL)!)
        }
        
        if MediaShareController.sharedInstance.playType != nil
        {
           MediaShareController.sharedInstance.tvQueueMediaCollection.removeAll()
           NotificationCenter.default.post(name: Notification.Name(rawValue: "clearTvQueue"), object: self, userInfo: nil)
        }
        
        if MediaShareController.sharedInstance.playType == "photo" && MediaShareController.sharedInstance.settingsValue.audioURL != ""
        {
            MediaShareController.sharedInstance.photoplayer?.setBackgroundMusic(URL(string: MediaShareController.sharedInstance.settingsValue.audioURL)!)
        }
    
    }
    
    func onPlayerChange(_ playerType: String)
    {
         MediaShareController.sharedInstance.photoplayer?.setBackgroundMusic(URL(string :"https://www.samsungdforum.com/smartview/sample/audio/Beverly_-_01_-_You_Said_It.mp3")!)
    }
    
    func onPlay()
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyPlay"), object: self, userInfo: nil)
    }
    
    func onPause()
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyPause"), object: self, userInfo: nil)
    }
    
    func onStop()
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyStop"), object: self, userInfo: nil)
    }
    
    func onMute()
    {
       
    }
    func onUnMute()
    {
        
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
        let mediaurl_HD = dequeuedItem["uri"] as! String
        if let index = IndexOfMediaItem(mediaurl_HD)
        {
            MediaShareController.sharedInstance.tvQueueMediaCollection.remove(at: index)
            NotificationCenter.default.post(name: Notification.Name(rawValue: "removeItemFromTVQueue"), object: self, userInfo:nil)
        }
    }
    
    func onClearList()
    {

    }
    
    func onGetList(_ queueList: [String: AnyObject])
    {
      //  MediaShareController.sharedInstance.tvQueueMediaCollection.removeAll()
        let items = queueList["data"] as! NSArray
        var itemfoundInMediaCollec_Flag = false
        for inputItem in items {
            
            let mediaItem : Media = Media(url: "")
            if let uri = (inputItem as AnyObject).object(forKey: "uri")
            {
                mediaItem.mediaimageUrl = uri as? String
                if  mediaItem.mediaimageUrl.contains("developer.samsung.com") == true && mediaItem.mediaimageUrl?.contains("_small") == false
                {
                    mediaItem.mediaimageUrl_HD = uri as? String
                    mediaItem.mediaimageUrl.insert(contentsOf: "_small".characters, at: (mediaItem.mediaimageUrl?.index((mediaItem.mediaimageUrl?.endIndex)!, offsetBy: -4))!)
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
                mediaItem.mediaimageUrl = albumArt as? String
            }
            if let albumArt = (inputItem as AnyObject).object(forKey: "thumbnailUrl")
            {
                mediaItem.mediaimageUrl = albumArt as? String
            }
            if IndexOfMediaItem(mediaItem.mediaimageUrl_HD) == nil
            {
                itemfoundInMediaCollec_Flag = true
                MediaShareController.sharedInstance.tvQueueMediaCollection.append(mediaItem)
            }
        }
        if itemfoundInMediaCollec_Flag == true
        {
            NotificationCenter.default.post(name: Notification.Name(rawValue: "addItemToTVQueue"), object: self, userInfo: nil)
        }
    }
    
    func IndexOfMediaItem(_ mediaUrl :String) -> Int?
    {
        return  MediaShareController.sharedInstance.tvQueueMediaCollection.index(where: { $0.mediaimageUrl_HD == mediaUrl })
    }

    func onCurrentPlaying(_ currentItem: [String: AnyObject])
    {
        if isPlayerAlreadyInit == true
        {
            isPlayerAlreadyInit = false
            MediaShareController.sharedInstance.playType = "photo"
            NotificationCenter.default.post(name: Notification.Name(rawValue: "onPlay"), object: self, userInfo: nil)
        }
        if let uri = currentItem["uri"]
        {
            let mediaImageUrl = uri as! String
             NotificationCenter.default.post(name: Notification.Name(rawValue: "onThumbnailChange"), object: self, userInfo: ["url": mediaImageUrl])
        }
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
        NotificationCenter.default.post(name: Notification.Name(rawValue: "photoApplicationSuspend"), object: self, userInfo: nil)
    }
    
    func OnAppResume()
    {
        MediaShareController.sharedInstance.photoplayer?.resumeApplicationInForeground()
    }
    
}
