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
import SmartView


private let reuseIdentifier = "photoCell"

class PhotoCollectionView : UIViewController, UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout, UIPopoverPresentationControllerDelegate
{
    
    @IBOutlet var photoCollection: UICollectionView!

    var playStatus:Bool = false
    var deviceMediaCollection = [Media]()
   
    override func viewDidLoad()
    {
        super.viewDidLoad()
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "OnApplicationSuspend:", name: "photoApplicationSuspend", object: nil)
        initmediaItems()
    }
    
    private func initmediaItems()
    {
        let inputFile = NSBundle.mainBundle().pathForResource("MediaItems", ofType: "plist")
        let items = NSDictionary(contentsOfFile: inputFile!)?.objectForKey("Photo") as! NSArray ;
        
        for inputItem in items
        {
            let mediaItem : Media = Media(url: "")
            mediaItem.mediaTitle = inputItem.objectForKey("title") as! String
            mediaItem.mediaimageUrl = inputItem.objectForKey("url") as! String
            mediaItem.mediaimageUrl_HD = inputItem.objectForKey("url_HD") as! String
            deviceMediaCollection.append(mediaItem)
        }
        self.photoCollection.reloadData()
    }
    
    @IBAction func addMediaItem(sender: AnyObject)
    {
        
        if MediaShareController.sharedInstance.isConnected == true
        {
            
            if MediaShareController.sharedInstance.playType == "photo"
            {
                let tag = sender.tag
                var item = [String: AnyObject]()
                var list = [Dictionary<String, AnyObject>]()
            
                let uri = "uri"
                let title = "title"
                let albumName = "albumName"
                let albumArt = "albumArt"
            
                item[uri] = deviceMediaCollection[tag].mediaimageUrl_HD
                item[title] = deviceMediaCollection[tag].mediaTitle
                item[albumName] = ""
                item[albumArt] = ""
                list.append(item)
                
                MediaShareController.sharedInstance.photoplayer?.addToList(list)
            }
            else
            {
                let alertController = UIAlertController(title: "", message: "Image Player is not Ready", preferredStyle: .Alert)
                let defaultAction = UIAlertAction(title: "OK", style: .Default, handler: nil)
                alertController.addAction(defaultAction)
                
                presentViewController(alertController, animated: true, completion: nil)
            }
        }
        else
        {
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
    func collectionView(collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAtIndexPath indexPath: NSIndexPath) -> CGSize
    {
        let screenRect = UIScreen.mainScreen().bounds
        let screenWidth = screenRect.size.width
        let imagesPerRow: CGFloat = 3.0
            
        return CGSizeMake(screenWidth/imagesPerRow - 2, 115)
    }
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell
    {
        let item : Media = self.deviceMediaCollection[indexPath.row]
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(reuseIdentifier, forIndexPath: indexPath) as!MediaCell
        cell.addMediaBtn.tag = indexPath.row
        cell.titleTxt.text = item.mediaTitle
        cell.mImageView.image = nil
        cell.indicator.hidesWhenStopped = true
        let queue:dispatch_queue_t = dispatch_get_main_queue()
    
        ImageCacheHelper.downloadImageAtIndexPath(indexPath , mediaCollection: deviceMediaCollection, completionBlock: { (result: UIImage) in
        dispatch_async(queue)
            {

                let cell1 =  collectionView.cellForItemAtIndexPath(indexPath) as? MediaCell
        
                if let cell2 = cell1
                {
                    cell2.mImageView.image = result
                    cell.indicator.hidesWhenStopped = true
                }
            }
        })
    
        if cell.mImageView == nil
        {
            cell.mImageView.image = UIImage(named:"image_Placeholder.png")!
        }
        return cell;
    }
    

    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath)
    {
        NSNotificationCenter.defaultCenter().postNotificationName("NotifySliderHide", object: self, userInfo: nil)
        if MediaShareController.sharedInstance.playType != "photo"
        {
            if MediaShareController.sharedInstance.playType == "video"
            {
                MediaShareController.sharedInstance.videoplayer?.clearList()
            }
            else
            {
                MediaShareController.sharedInstance.audioplayer?.clearList()
            }
            MediaShareController.sharedInstance.mediaType = "photo"
            
        }
        
        let item : Media = self.deviceMediaCollection[indexPath.row]
        let url = NSURL(string: item.mediaimageUrl_HD)
        
        NSNotificationCenter.defaultCenter().postNotificationName("onThumbnailChange", object: self, userInfo: ["url": item.mediaimageUrl!])
        if MediaShareController.sharedInstance.isConnected == true
        {
          MediaShareController.sharedInstance.photoplayer?.playContent(url!,title: item.mediaTitle, completionHandler: nil)
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
            NSNotificationCenter.defaultCenter().postNotificationName("photoApplicationResume", object: self, userInfo: nil)
        }
        alert.addAction(okAction)
        
    }
}