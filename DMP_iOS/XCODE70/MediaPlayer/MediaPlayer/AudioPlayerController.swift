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

    var totalDuration:Int = 0
    override init () {
        super.init()
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "OnAppResume", name: "audioApplicationResume", object: nil)
    }
    func onBufferingStart()
    {
        MediaShareController.sharedInstance.playType = "audio"
    }
    
    func onBufferingComplete()
    {
        
    }
    
    func onBufferingProgress(progress: Int)
    {
        
    }
    
    func onCurrentPlayTime(progress: Int)
    {
        if totalDuration != 0
        {
            NSNotificationCenter.defaultCenter().postNotificationName("NotifyTimeProgress", object: self, userInfo: ["progress":progress, "totalDuration":totalDuration])
        }
       
    }
    
    func onStreamingStarted(duration: Int)
    {
        totalDuration = duration
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyPlay", object: self, userInfo: nil)
    }
    
    func onStreamCompleted()
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyStop", object: self, userInfo: nil)
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
    
    func onPlayerChange(playerType: String)
    {
        MediaShareController.sharedInstance.playType = "audio"
    }
    
    func onPlay()
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyPlay", object: self, userInfo: nil)
    }
    
    func onPause()
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyPause", object: self, userInfo: nil)
    }
    
    func onForward()
    {

    }
    
    func onRewind()
    {
        
    }
    
    func onStop()
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyStop", object: self, userInfo: nil)
    }
    
    func onMute()
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyMute", object: self, userInfo: nil)
    }
    
    func onUnMute()
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyUnMute", object: self, userInfo: nil)
    }
    
    func onNext()
    {
        print("onNext")
    }
    
    func onPrevious()
    {
        print("onPrevious")
    }
    
    func onGetVolume(volLevel: Int)
    {
        
    }
    
    func onVolumeChange(volLevel: Int)
    {
        
    }
    
    func onAddToList(enqueuedItem: [String: AnyObject])
    {
        
    }
    
    func onRemoveFromList(dequeuedItem: [String: AnyObject])
    {
        MediaShareController.sharedInstance.audioplayer?.getList()
    }
    
    func onClearList()
    {
        
    }
    
    func onGetList(queueList: [String: AnyObject])
    {
        MediaShareController.sharedInstance.tvQueueMediaCollection.removeAll()
        
        let items = queueList["data"] as! NSArray
        for inputItem in items {
            let mediaItem : Media = Media(url: "")
            
            if let uri = inputItem.objectForKey("uri")
            {
                if MediaShareController.sharedInstance.playType == "photo" {
                    mediaItem.mediaimageUrl = uri as! String
                }
                else
                {
                    mediaItem.mediaUrl = uri as! String
                }
            }
            if let title = inputItem.objectForKey("title")
            {
                mediaItem.mediaTitle = title as! String
            }
            if let albumName = inputItem.objectForKey("albumName")
            {
                mediaItem.mediaAlbumName = albumName as! String
            }
            if let albumArt = inputItem.objectForKey("albumArt")
            {
                mediaItem.mediaimageUrl = albumArt as! String
            }
            if let albumArt = inputItem.objectForKey("thumbnailUrl")
            {
                mediaItem.mediaimageUrl = albumArt as! String
            }
            if MediaShareController.sharedInstance.tvQueueMediaCollection.contains(mediaItem) == false
            {
                MediaShareController.sharedInstance.tvQueueMediaCollection.append(mediaItem)
            }
        }
        NSNotificationCenter.defaultCenter().postNotificationName("TvlistRecieved", object: self, userInfo: nil)
        
    }
    
    func onShuffle(status: Bool)
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyShuffle", object: self, userInfo: ["shuffleStatus": status])
    }
    
    func onRepeat(mode: String)
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyRepeat", object: self, userInfo: ["repeatMode": mode])
    }
    
    func onCurrentPlaying(currentItem: [String: AnyObject])
    {
        
        if let url = currentItem["albumArt"]
        {
            let mediaImageUrl = url as! String
            NSNotificationCenter.defaultCenter().postNotificationName("onThumbnailChange", object: self, userInfo: ["url": mediaImageUrl])
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
        NSNotificationCenter.defaultCenter().postNotificationName("onPlay", object: self, userInfo: nil)
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyPlay", object: self, userInfo: nil)
        
    }
    
    func onError(error: NSError)
    {
        
    }
    
    func onApplicationResume()->Void
    {
        
    }
    
    func onApplicationSuspend()->Void
    {
       NSNotificationCenter.defaultCenter().postNotificationName("audioApplicationSuspend", object: self, userInfo: nil)
    }
    
    func OnAppResume()
    {
        MediaShareController.sharedInstance.audioplayer?.resumeApplicationInForeground()
    }
    
}