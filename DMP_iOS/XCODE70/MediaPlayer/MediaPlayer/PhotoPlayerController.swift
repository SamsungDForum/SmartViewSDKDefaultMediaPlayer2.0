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

    
    override init ()
    {
        super.init()
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "OnAppResume", name: "photoApplicationResume", object: nil)
    }
    func onPlayerInitialized(){
    
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
        
       MediaShareController.sharedInstance.photoplayer?.setBackgroundMusic(NSURL(string: "https://www.samsungdforum.com/smartview/sample/audio/Beverly_-_01_-_You_Said_It.mp3")!)    }
    
    func onPlayerChange(playerType: String)
    {
        MediaShareController.sharedInstance.photoplayer?.setBackgroundMusic(NSURL(string :"https://www.samsungdforum.com/smartview/sample/audio/Beverly_-_01_-_You_Said_It.mp3")!)
    }
    
    func onPlay()
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyPlay", object: self, userInfo: nil)
    }
    
    func onPause()
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyPause", object: self, userInfo: nil)
    }
    
    func onStop()
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyStop", object: self, userInfo: nil)
    }
    
    func onMute()
    {
       
    }
    func onUnMute()
    {
        
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
        MediaShareController.sharedInstance.photoplayer?.getList()
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
                if MediaShareController.sharedInstance.playType == "photo"
                {
                    mediaItem.mediaimageUrl = uri as! String
                    if  mediaItem.mediaimageUrl.containsString("samsungdforum") == true && mediaItem.mediaimageUrl.containsString("_small") == false
                    {
                      mediaItem.mediaimageUrl.insertContentsOf("_small".characters, at: mediaItem.mediaimageUrl.endIndex.advancedBy(-4))
                    }
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
        NSNotificationCenter.defaultCenter().postNotificationName("NotifyPlay", object: self, userInfo: nil)
        
    }

    
    func onCurrentPlaying(currentItem: [String: AnyObject])
    {
        if let uri = currentItem["uri"]
        {
            let mediaImageUrl = uri as! String
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
        
    }
    
    func onError(error: NSError)
    {
        
    }
    
    func onApplicationResume()->Void
    {
        
    }
    
    func onApplicationSuspend()->Void
    {
        NSNotificationCenter.defaultCenter().postNotificationName("photoApplicationSuspend", object: self, userInfo: nil)
    }
    
    func OnAppResume()
    {
        MediaShareController.sharedInstance.photoplayer?.resumeApplicationInForeground()
    }
    
}