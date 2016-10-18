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

class VideoPlayerController: NSObject, VideoPlayerDelegate
{
    var totalDuration:Int = 0
    
    override init ()
    {
        super.init()
        NotificationCenter.default.addObserver(self, selector: #selector(VideoPlayerController.OnAppResume), name: NSNotification.Name(rawValue: "videoApplicationResume"), object: nil)
    }
    
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
        if  MediaShareController.sharedInstance.mediaType == "audio"
        {
            MediaShareController.sharedInstance.playType = "audio"
        }
        else if  MediaShareController.sharedInstance.mediaType == "photo"
        {
            MediaShareController.sharedInstance.playType = "photo"
        }
        else if  MediaShareController.sharedInstance.mediaType == "video"
        {
            MediaShareController.sharedInstance.playType = "video"
        }
        
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
        MediaShareController.sharedInstance.videoplayer?.getList()
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
        if  MediaShareController.sharedInstance.mediaType == "audio"
        {
            MediaShareController.sharedInstance.playType = "audio"
        }
        else if  MediaShareController.sharedInstance.mediaType == "photo"
        {
            MediaShareController.sharedInstance.playType = "photo"
        }
        else if  MediaShareController.sharedInstance.mediaType == "video"
        {
            MediaShareController.sharedInstance.playType = "video"
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
    
    func OnAppResume()
    {
        MediaShareController.sharedInstance.videoplayer?.resumeApplicationInForeground()
    }
    
}
