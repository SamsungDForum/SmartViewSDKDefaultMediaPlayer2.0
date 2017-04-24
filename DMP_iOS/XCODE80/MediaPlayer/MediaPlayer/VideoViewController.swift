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

private let reuseIdentifier = "videoCell"

class VideoViewController : UIViewController, UICollectionViewDataSource, UICollectionViewDelegate, UICollectionViewDelegateFlowLayout, UIPopoverPresentationControllerDelegate
{
    @IBOutlet var videoController: UICollectionView!
    
    var playStatus:Bool = false
    var deviceMediaCollection = [Media]()

    override func viewDidLoad()
    {
        super.viewDidLoad()
        self.videoController.delegate = self
        self.videoController.dataSource = self
        NotificationCenter.default.addObserver(self, selector: #selector(VideoViewController.OnApplicationSuspend(_:)), name: NSNotification.Name(rawValue: "videoApplicationSuspend"), object: nil)
        initmediaItems()
    }
    fileprivate func initmediaItems()
    {
        let inputFile = Bundle.main.path(forResource: "MediaItems", ofType: "plist")
        let items = NSDictionary(contentsOfFile: inputFile!)?.object(forKey: "Video") as! NSArray ;
        
        for inputItem in items
        {
            let mediaItem : Media = Media(url: "")
            mediaItem.mediaTitle = (inputItem as AnyObject).object(forKey: "title") as! String
            mediaItem.mediaimageUrl = (inputItem as AnyObject).object(forKey: "thumbUrl") as! String
            mediaItem.mediaUrl = (inputItem as AnyObject).object(forKey: "url") as! String
            
            deviceMediaCollection.append(mediaItem)
        }
        self.videoController.reloadData()
    }
    @IBAction func addMediaItem(_ sender: AnyObject)
    {
        
        if MediaShareController.sharedInstance.isConnected == true
        {
            
            if MediaShareController.sharedInstance.playType == "video"
            {
                let tag = sender.tag
                var item = [String: AnyObject]()
                var list = [Dictionary<String, AnyObject>]()
           
                let uri = "uri"
                let title = "title"
                let albumName = "albumName"
                let thumbnailUrl = "thumbnailUrl"
            
                item[uri] = deviceMediaCollection[tag!].mediaUrl as AnyObject?
                item[title] = deviceMediaCollection[tag!].mediaTitle as AnyObject?
                item[albumName] = deviceMediaCollection[tag!].mediaAlbumName as AnyObject?
                item[thumbnailUrl] = deviceMediaCollection[tag!].mediaimageUrl as AnyObject?
                list.append(item)
            
                MediaShareController.sharedInstance.videoplayer?.addToList(list)
            }
            else
            {
                let alertController = UIAlertController(title: "", message: "Video Player is not Ready", preferredStyle: .alert)
                let defaultAction = UIAlertAction(title: "OK", style: .default, handler: nil)
                alertController.addAction(defaultAction)
                
                present(alertController, animated: true, completion: nil)
            }
            
        }
        else
        {
            let alertController = UIAlertController(title: "", message: "Please connect to a TV", preferredStyle: .alert)
            let defaultAction = UIAlertAction(title: "OK", style: .default, handler: nil)
            alertController.addAction(defaultAction)
            
            present(alertController, animated: true, completion: nil)
        }
        
    }
    
    override func viewWillAppear(_ animated: Bool)
    {
        
    }
    
    override func viewDidAppear(_ animated: Bool)
    {
        super.viewDidAppear(animated)
    }

    override func viewWillDisappear(_ animated: Bool)
    {
        
    }
    
    func numberOfSections(in collectionView: UICollectionView) -> Int
    {
        let sections:Int = 1
        return sections
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int
    {
       return deviceMediaCollection.count
    }
    
    
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell
    {
        let item : Media = self.deviceMediaCollection[(indexPath as NSIndexPath).row]
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath) as!MediaCell
        cell.frame = CGRect(x: 0, y: cell.frame.origin.y, width: collectionView.frame.size.width, height: cell.frame.height)
        cell.addMediaBtn.tag = (indexPath as NSIndexPath).row
        cell.titleTxt.text = item.mediaTitle
        cell.mImageView.image = nil
        cell.indicator.hidesWhenStopped = true
        ImageCacheHelper.downloadImageAtIndexPath(indexPath , mediaCollection: deviceMediaCollection, completionBlock: { (result: UIImage) in
            DispatchQueue.main.async
            {
                cell.mImageView.image = result
               /* let cell1 =  collectionView.cellForItem(at: indexPath) as? MediaCell
                if let cell2 = cell1
                {
                    cell2.mImageView.image = result
                    cell.indicator.hidesWhenStopped = true

                }*/
            }
            
        })
        cell.addMediaBtn.frame = CGRect(x: cell.frame.size.width - 20 - cell.addMediaBtn.frame.size.width, y: cell.addMediaBtn.frame.origin.y, width: cell.addMediaBtn.frame.size.width, height: cell.addMediaBtn.frame.size.height)
        cell.mImageView.image = UIImage(named:"video_Placeholder.jpeg")!
        return cell;
    }

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath)
    {
        if MediaShareController.sharedInstance.isConnected == true
        {
          NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifySliderShow"), object: self, userInfo: nil)
        }
        if MediaShareController.sharedInstance.playType != "video"
        {
            if MediaShareController.sharedInstance.playType == "audio"
            {
                MediaShareController.sharedInstance.audioplayer?.clearList()
            }
            else
            {
                MediaShareController.sharedInstance.photoplayer?.clearList()
            }

            MediaShareController.sharedInstance.mediaType = "video"
        }
        let item : Media = self.deviceMediaCollection[(indexPath as NSIndexPath).row]
    
     //   NSNotificationCenter.defaultCenter().postNotificationName("onThumbnailChange", object: self, userInfo: ["url": item.mediaimageUrl!])
        if MediaShareController.sharedInstance.isConnected == true
        {

             title = nil
            MediaShareController.sharedInstance.videoplayer?.playContent(URL(string :item.mediaUrl), title: item.mediaTitle ,thumbnailURL: URL(string :item.mediaimageUrl))
            
        }
        else
        {
            let alertController = UIAlertController(title: "", message: "Please connect to a TV", preferredStyle: .alert)
            let defaultAction = UIAlertAction(title: "OK", style: .default, handler: nil)
            alertController.addAction(defaultAction)
            
            present(alertController, animated: true, completion: nil)
        }
      
   }
    
    func adaptivePresentationStyle(for controller: UIPresentationController) -> UIModalPresentationStyle
    {
        return .none
    }
    func OnApplicationSuspend(_ notification:Notification)
    {
        
        let alert = UIAlertController(title: "", message: "  \"DEFAULT MEDIA PLAYER\"\nHAS GONE IN BACKGROUND,\n    ON YOUR TV.", preferredStyle: UIAlertControllerStyle.alert)
        self.present(alert, animated: true, completion: nil)
        
        let okAction = UIAlertAction(title: "BRING TO FORGROUND", style: UIAlertActionStyle.default) { (action) -> Void in
            NotificationCenter.default.post(name: Notification.Name(rawValue: "videoApplicationResume"), object: self, userInfo: nil)
        }
        alert.addAction(okAction)
        
    }

}
