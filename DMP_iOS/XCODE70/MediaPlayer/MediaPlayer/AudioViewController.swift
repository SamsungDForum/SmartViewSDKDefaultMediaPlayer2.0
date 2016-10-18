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
import UIKit


private let reuseIdentifier = "audioCell"

class AudioViewController : UIViewController, UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout,UIPopoverPresentationControllerDelegate
{
    
    @IBOutlet var audioCollection: UICollectionView!
    
    var playStatus:Bool = false
    var deviceMediaCollection = [Media]()
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        self.audioCollection.delegate = self
        self.audioCollection.dataSource = self
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "OnApplicationSuspend:", name: "audioApplicationSuspend", object: nil)
        initmediaItems()
    }
    private func initmediaItems()
    {
        let inputFile = NSBundle.mainBundle().pathForResource("MediaItems", ofType: "plist")
        let items = NSDictionary(contentsOfFile: inputFile!)?.objectForKey("Audio") as! NSArray ;
        
        for inputItem in items {
            let mediaItem : Media = Media(url: "")
            mediaItem.mediaTitle = inputItem.objectForKey("title") as! String
            mediaItem.mediaimageUrl = inputItem.objectForKey("thumbUrl") as! String
            mediaItem.mediaUrl = inputItem.objectForKey("url") as! String
            mediaItem.mediaAlbumName = inputItem.objectForKey("albumName") as! String
            deviceMediaCollection.append(mediaItem)
        }
         self.audioCollection.reloadData()
    }
    @IBAction func addMediaItem(sender: AnyObject)
    {
        
        if MediaShareController.sharedInstance.isConnected == true {
        if MediaShareController.sharedInstance.playType == "audio"
          {
             let tag = sender.tag
             var item = [String: AnyObject]()
             var list = [Dictionary<String, AnyObject>]()
            
             let uri = "uri"
             let title = "title"
             let albumName = "albumName"
             let albumArt = "albumArt"
            
             item[uri] = deviceMediaCollection[tag].mediaUrl
             item[title] = deviceMediaCollection[tag].mediaTitle
             item[albumName] = deviceMediaCollection[tag].mediaAlbumName
             item[albumArt] = deviceMediaCollection[tag].mediaimageUrl
             list.append(item)
                
             MediaShareController.sharedInstance.audioplayer?.addToList(list)
          }
        else{
              let alertController = UIAlertController(title: "", message: "Audio Player is not Ready", preferredStyle: .Alert)
              let defaultAction = UIAlertAction(title: "OK", style: .Default, handler: nil)
              alertController.addAction(defaultAction)
            
              presentViewController(alertController, animated: true, completion: nil)
            }

        }
            
        else {
            
            let alertController = UIAlertController(title: "", message: "Please connect to a TV", preferredStyle: .Alert)
            let defaultAction = UIAlertAction(title: "OK", style: .Default, handler: nil)
            alertController.addAction(defaultAction)
            
            presentViewController(alertController, animated: true, completion: nil)
        }

    }
    override func viewWillAppear(animated: Bool)
    {
        
    }
    
    override func viewDidAppear(animated: Bool)
    {
        super.viewDidAppear(animated)
        
    }
    
    override func viewWillDisappear(animated: Bool)
    {
        
    }
    
    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int
    {
        let sections:Int = 1
        return sections
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int
    {
        return deviceMediaCollection.count
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell
    {
        let item : Media = self.deviceMediaCollection[indexPath.row]
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(reuseIdentifier, forIndexPath: indexPath) as!MediaCell
        cell.frame = CGRectMake(0, cell.frame.origin.y, collectionView.frame.size.width, cell.frame.height)
        cell.addMediaBtn.tag = indexPath.row
        cell.titleTxt.text = item.mediaTitle
        cell.albumName.text = item.mediaAlbumName
        cell.mImageView.image = nil
        cell.indicator.hidesWhenStopped = true
        
        ImageCacheHelper.downloadImageAtIndexPath(indexPath , mediaCollection: deviceMediaCollection, completionBlock: { (result: UIImage) in
            dispatch_async(dispatch_get_main_queue())
                {
                
                    let cell1 =  collectionView.cellForItemAtIndexPath(indexPath) as? MediaCell
                    if let cell2 = cell1
                    {
                        cell2.mImageView.image = result
                        cell.indicator.hidesWhenStopped = true
                    }
                }
        })
        cell.addMediaBtn.frame = CGRectMake(cell.frame.size.width - cell.addMediaBtn.frame.size.width - 20, cell.addMediaBtn.frame.origin.y, cell.addMediaBtn.frame.size.width, cell.addMediaBtn.frame.size.height)
        
        cell.mImageView.image = UIImage(named:"Music_Placeholder.png")!
        return cell;
    }
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath)
    {
        if MediaShareController.sharedInstance.isConnected == true
        {
            NSNotificationCenter.defaultCenter().postNotificationName("NotifySliderShow", object: self, userInfo: nil)
        }
        if MediaShareController.sharedInstance.playType != "audio"
        {
            
            if MediaShareController.sharedInstance.playType == "video"{
                
                MediaShareController.sharedInstance.videoplayer?.clearList()
            }
            else{
                MediaShareController.sharedInstance.photoplayer?.clearList()
            }
            
            MediaShareController.sharedInstance.mediaType = "audio"
            
        }
        
        let item : Media = self.deviceMediaCollection[indexPath.row]
        
        // NSNotificationCenter.defaultCenter().postNotificationName("onThumbnailChange", object: self, userInfo: ["url": item.mediaimageUrl!])
        if MediaShareController.sharedInstance.isConnected == true
        {
             MediaShareController.sharedInstance.audioplayer?.playContent(NSURL(string :item.mediaUrl)!,title: item.mediaTitle,albumName: item.mediaAlbumName,albumArtUrl: NSURL(string :item.mediaimageUrl)!)
        }
        else
        {
            let alertController = UIAlertController(title: "", message: "Please connect to a TV", preferredStyle: .Alert)
            let defaultAction = UIAlertAction(title: "OK", style: .Default, handler: nil)
            alertController.addAction(defaultAction)
            
            presentViewController(alertController, animated: true, completion: nil)
        }

    }
    

    func adaptivePresentationStyleForPresentationController(controller: UIPresentationController) -> UIModalPresentationStyle
    {
        return .None
    }
    func OnApplicationSuspend(notification:NSNotification)
    {

        let alert = UIAlertController(title: "", message: "  \"DEFAULT MEDIA PLAYER\"\nHAS GONE IN BACKGROUND,\n    ON YOUR TV.", preferredStyle: UIAlertControllerStyle.Alert)
        self.presentViewController(alert, animated: true, completion: nil)
        
        let okAction = UIAlertAction(title: "BRING TO FORGROUND", style: UIAlertActionStyle.Default) { (action) -> Void in
           NSNotificationCenter.defaultCenter().postNotificationName("audioApplicationResume", object: self, userInfo: nil)
        }
        alert.addAction(okAction)
        
    }
    
  }