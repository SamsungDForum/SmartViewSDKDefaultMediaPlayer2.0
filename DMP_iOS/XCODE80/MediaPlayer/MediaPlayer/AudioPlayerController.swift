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

class AudioPlayerController: NSObject, AudioPlayerDelegate,UIPopoverPresentationControllerDelegate {

    private var isPlayerAlreadyInit: Bool = true
    private var isTVListVisible :Bool = false
    var totalDuration :Int = 0
    override init () {
        super.init()
        NotificationCenter.default.addObserver(self, selector: #selector(AudioPlayerController.OnAppResume), name: NSNotification.Name(rawValue: "audioApplicationResume"), object: nil)
    }
    func onBufferingStart()
    {
        MediaShareController.sharedInstance.playType = "audio"
    }
    
    func onBufferingComplete()
    {
        
    }
    
    func onBufferingProgress(_ progress: Int)
    {
        
    }
    
    func onCurrentPlayTime(_ progress: Int)
    {
        if totalDuration != 0
        {
            NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyTimeProgress"), object: self, userInfo: ["progress":progress, "totalDuration":totalDuration])
        }
       
    }
    
    func onStreamingStarted(_ duration: Int)
    {
        totalDuration = duration
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyPlay"), object: self, userInfo: nil)
    }
    
    func onStreamCompleted()
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyStop"), object: self, userInfo: nil)
    }
    
    func onPlayerInitialized()
    {
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
    }
    
    func onPlayerChange(_ playerType: String)
    {
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
    
    func onStop()
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyStop"), object: self, userInfo: nil)
    }
    
    func onMute()
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyMute"), object: self, userInfo: nil)
    }
    
    func onUnMute()
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyUnMute"), object: self, userInfo: nil)
    }
    func onControlStatus(_ volLevel: Int, muteStatus: Bool, shuffleStatus: Bool, mode: String)
    {
        //print("volume  \(volLevel)  shuffleStatus  \(shuffleStatus)  and modevalue  \(mode)")
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
        let mediaurl = dequeuedItem["uri"] as! String
        if let index = IndexOfMediaItem(mediaurl)
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
        //MediaShareController.sharedInstance.tvQueueMediaCollection.removeAll()
        var itemfoundInMediaCollec_Flag = false
        let items = queueList["data"] as! NSArray
        for inputItem in items {
            let mediaItem : Media = Media(url: "")
            
            if let uri = (inputItem as AnyObject).object(forKey: "uri")
            {
                mediaItem.mediaUrl = uri as! String
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
            if IndexOfMediaItem(mediaItem.mediaUrl) == nil
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
        return  MediaShareController.sharedInstance.tvQueueMediaCollection.index(where: { $0.mediaUrl == mediaUrl })
    }
    
    func onShuffle(_ status: Bool)
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyShuffle"), object: self, userInfo: ["shuffleStatus": status])
    }
    
    func onRepeat(_ mode: String)
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifyRepeat"), object: self, userInfo: ["repeatMode": mode])
    }
    
    func onCurrentPlaying(_ currentItem: [String: AnyObject])
    {
        if  isPlayerAlreadyInit == true
        {
            isPlayerAlreadyInit = false
            MediaShareController.sharedInstance.playType = "audio"
            NotificationCenter.default.post(name: Notification.Name(rawValue: "onPlay"), object: self, userInfo: nil)
        }
        if let url = currentItem["albumArt"]
        {
            let mediaImageUrl = url as! String
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
       NotificationCenter.default.post(name: Notification.Name(rawValue: "audioApplicationSuspend"), object: self, userInfo: nil)
    }
    
    func OnAppResume()
    {
        MediaShareController.sharedInstance.audioplayer?.resumeApplicationInForeground()
    }
    
}
