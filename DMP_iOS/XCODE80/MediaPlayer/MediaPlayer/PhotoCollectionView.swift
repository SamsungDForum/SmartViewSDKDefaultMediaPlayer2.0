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
        NotificationCenter.default.addObserver(self, selector: #selector(PhotoCollectionView.OnApplicationSuspend(_:)), name: NSNotification.Name(rawValue: "photoApplicationSuspend"), object: nil)
        initmediaItems()
    }
    
    fileprivate func initmediaItems()
    {
        let inputFile = Bundle.main.path(forResource: "MediaItems", ofType: "plist")
        let items = NSDictionary(contentsOfFile: inputFile!)?.object(forKey: "Photo") as! NSArray ;
        
        for inputItem in items
        {
            let mediaItem : Media = Media(url: "")
            mediaItem.mediaTitle = (inputItem as AnyObject).object(forKey: "title") as! String
            mediaItem.mediaimageUrl = (inputItem as AnyObject).object(forKey: "url") as! String
            mediaItem.mediaimageUrl_HD = (inputItem as AnyObject).object(forKey: "url_HD") as! String
            deviceMediaCollection.append(mediaItem)
        }
        self.photoCollection.reloadData()
    }
    
    @IBAction func addMediaItem(_ sender: AnyObject)
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
            
                item[uri] = deviceMediaCollection[tag!].mediaimageUrl_HD as AnyObject?
                item[title] = deviceMediaCollection[tag!].mediaTitle as AnyObject?
                item[albumName] = "" as AnyObject?
                item[albumArt] = "" as AnyObject?
                list.append(item)
                
                MediaShareController.sharedInstance.photoplayer?.addToList(list)
            }
            else
            {
                let alertController = UIAlertController(title: "", message: "Image Player is not Ready", preferredStyle: .alert)
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
    func collectionView(_ collectionView: UICollectionView, layout collectionViewLayout: UICollectionViewLayout, sizeForItemAt indexPath: IndexPath) -> CGSize
    {
        let screenRect = UIScreen.main.bounds
        let screenWidth = screenRect.size.width
        let imagesPerRow: CGFloat = 3.0
            
        return CGSize(width: screenWidth/imagesPerRow - 2, height: 115)
    }
    func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell
    {
        let item : Media = self.deviceMediaCollection[(indexPath as NSIndexPath).row]
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath) as!MediaCell
        cell.addMediaBtn.tag = (indexPath as NSIndexPath).row
        cell.titleTxt.text = item.mediaTitle
        cell.mImageView.image = nil
        cell.indicator.hidesWhenStopped = true
        let queue:DispatchQueue = DispatchQueue.main
    
        ImageCacheHelper.downloadImageAtIndexPath(indexPath , mediaCollection: self.deviceMediaCollection, completionBlock: { (result: UIImage) in
        queue.async
            {
                cell.mImageView.image = result

                /*let cell1 =  collectionView.cellForItem(at: indexPath) as? MediaCell
                if let cell2 = cell1
                {
                    cell.mImageView.image = result
                    cell.indicator.hidesWhenStopped = true
                }*/
           }
        })
       
    
        if cell.mImageView == nil
        {
            cell.mImageView.image = UIImage(named:"image_Placeholder.png")!
        }
        return cell;
    }
    

    func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath)
    {
        NotificationCenter.default.post(name: Notification.Name(rawValue: "NotifySliderHide"), object: self, userInfo: nil)
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
        
        let item : Media = self.deviceMediaCollection[(indexPath as NSIndexPath).row]
        let url = URL(string: item.mediaimageUrl_HD)
        
        NotificationCenter.default.post(name: Notification.Name(rawValue: "onThumbnailChange"), object: self, userInfo: ["url": item.mediaimageUrl!])
        if MediaShareController.sharedInstance.isConnected == true
        {
          MediaShareController.sharedInstance.photoplayer?.playContent(url,title: item.mediaTitle, completionHandler: nil)
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
            NotificationCenter.default.post(name: Notification.Name(rawValue: "photoApplicationResume"), object: self, userInfo: nil)
        }
        alert.addAction(okAction)
        
    }
}
