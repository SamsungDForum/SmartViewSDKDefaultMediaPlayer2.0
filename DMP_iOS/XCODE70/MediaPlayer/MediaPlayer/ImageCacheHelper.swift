//
//  ImageCacheHelper.swift
//  MediaPlayer
//
//  Created by Amit Khoth on 8/2/16.
//  Copyright © 2016 samsung. All rights reserved.
//

import Foundation
import UIKit

class ImageCacheHelper:NSObject{
    
    static var cache = NSCache()
    static var isNotRunningDispatch:Bool = true
    
    static let fileManager = NSFileManager.defaultManager()
    static let diskPaths = NSSearchPathForDirectoriesInDomains(NSSearchPathDirectory.CachesDirectory, NSSearchPathDomainMask.UserDomainMask, true)
    static var cacheDirectory: NSString {
        get{
            return diskPaths[0] as NSString
        }
        
    }
    
    class func setObjectForKey(imageData:NSData,imageKey:String){
        
        ImageCacheHelper.cache.setObject(imageData, forKey: imageKey)
        
    }
    
     class func getObjectForKey(imageKey:String)->NSData?{
        
        return ImageCacheHelper.cache.objectForKey(imageKey) as? NSData
        
    }
    
     class func getImage(imageUrl:String,completionHandler:(NSData)->())
     {
   
            dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), {
               if let imgUrl = NSURL(string:imageUrl)
               {
                    if let imageData = NSData(contentsOfURL: imgUrl)
                    {
                        self.setObjectForKey(imageData, imageKey: "\(imageUrl.hashValue)")
                        completionHandler(imageData)
                    }
               }
            })
     }
  
   class func downloadImageAtIndexPath(indexPath : NSIndexPath, mediaCollection: [Media],completionBlock: (result: UIImage) -> Void)
   {
        let item : Media = mediaCollection[indexPath.row]
        let  url  = item.mediaimageUrl
        let diskPath = ImageCacheHelper.cacheDirectory.stringByAppendingPathComponent("\(url.hashValue)")
        if url != ""
        {
            if ImageCacheHelper.fileManager.fileExistsAtPath(diskPath)
            {
                
                dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_HIGH, 0), {
                    let image =  UIImage(contentsOfFile: diskPath)
                    completionBlock(result: image!)
                    ImageCacheHelper.setObjectForKey(UIImageJPEGRepresentation(image!, 1.0)!, imageKey: "\(url.hashValue)")
                })
                                
            }
            else
            {
                ImageCacheHelper.getImage(url,completionHandler: { imageData in
                imageData.writeToFile(diskPath, atomically: true)
                let userImage =  UIImage(data: imageData)
                completionBlock(result: userImage!)
                })
            }
        }
    }
    
    class func getImageforThumbnail(url : String,completionBlock: (result: UIImage) -> Void)
    {
        
        let diskPath = ImageCacheHelper.cacheDirectory.stringByAppendingPathComponent("\(url.hashValue)")
        if url != ""
        {
            if ImageCacheHelper.fileManager.fileExistsAtPath(diskPath)
            {
                let image =  UIImage(contentsOfFile: diskPath)
                completionBlock(result: image!)
                ImageCacheHelper.setObjectForKey(UIImageJPEGRepresentation(image!, 1.0)!, imageKey: "\(url.hashValue)")
                
            }
        }
    }

}