//
//  MediaListController.swift
//  MediaPlayer
//
//  Created by Amit Khoth on 8/11/16.
//  Copyright © 2016 samsung. All rights reserved.
//

import UIKit

private let reuseIdentifier = "mediaCell"
class MediaListController: UIViewController
{
    @IBOutlet var mediaCollection: UICollectionView!

    override func viewDidLoad()
    {
        super.viewDidLoad()
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
    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()

    }
    func numberOfSectionsInCollectionView(_ collectionView: UICollectionView) -> Int
    {
        let sections:Int = 1
        return sections
    }
    
    func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int
    {
        return MediaShareController.sharedInstance.tvQueueMediaCollection.count
    }

    func collectionView(_ collectionView: UICollectionView, cellForItemAtIndexPath indexPath: IndexPath) -> UICollectionViewCell
    {
        
        let item : Media = MediaShareController.sharedInstance.tvQueueMediaCollection[(indexPath as NSIndexPath).row]
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: reuseIdentifier, for: indexPath) as!MediaCell
        cell.frame = CGRect(x: 0, y: cell.frame.origin.y, width: collectionView.frame.size.width, height: cell.frame.height)
        cell.delMediabtn.tag = (indexPath as NSIndexPath).row
        cell.titleTxt.text = item.mediaTitle
        cell.mImageView.image = nil
        cell.indicator.hidesWhenStopped = true
        ImageCacheHelper.downloadImageAtIndexPath(indexPath , mediaCollection: MediaShareController.sharedInstance.tvQueueMediaCollection, completionBlock: { (result: UIImage) in
            DispatchQueue.main.async
            {
                let cell1 =  collectionView.cellForItem(at: indexPath) as? MediaCell
                if let cell2 = cell1
                {
                    cell2.mImageView.image = result
                    cell.indicator.hidesWhenStopped = true
                }
            }
        })
        if MediaShareController.sharedInstance.playType == "audio"{
            cell.mImageView.image = UIImage(named:"Music_Placeholder.png")!
        }
        else if MediaShareController.sharedInstance.playType == "video"{
            cell.mImageView.image = UIImage(named:"video_Placeholder.jpeg")!
        }
        else if MediaShareController.sharedInstance.playType == "photo"{
            cell.mImageView.image = UIImage(named:"image_Placeholder.png")!
        }
        return cell;
    }
    @IBAction func removeItemFromList(_ sender: AnyObject)
    {
        
        if MediaShareController.sharedInstance.playType == "photo"
        {
            let tag = sender.tag
            let imageUrl = MediaShareController.sharedInstance.tvQueueMediaCollection[tag!].mediaimageUrl
            if var url = imageUrl
            {
                if url.contains("_small") == true
                {
                    let range  = url.index((url.endIndex), offsetBy: -10)..<url.index((url.endIndex), offsetBy: -4)
                    url.removeSubrange(range)
                }
                MediaShareController.sharedInstance.photoplayer?.removeFromList(URL(string: imageUrl!)!)
            }
        }
       if  MediaShareController.sharedInstance.playType == "audio"
       {
            let tag = sender.tag
            let audioUrl = MediaShareController.sharedInstance.tvQueueMediaCollection[tag!].mediaUrl
            MediaShareController.sharedInstance.audioplayer?.removeFromList(URL(string: audioUrl!)!)
        }
       if MediaShareController.sharedInstance.playType == "video"
       {
            let tag = sender.tag
            let videoUrl = MediaShareController.sharedInstance.tvQueueMediaCollection[tag!].mediaUrl
            MediaShareController.sharedInstance.videoplayer?.removeFromList(URL(string: videoUrl!)!)
        }
    }
}
