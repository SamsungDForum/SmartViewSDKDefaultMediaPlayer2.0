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
    
    static var cache = NSCache<AnyObject, AnyObject>()
    static var isNotRunningDispatch:Bool = true
    
    static let fileManager = FileManager.default
    static let diskPaths = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.cachesDirectory, FileManager.SearchPathDomainMask.userDomainMask, true)
    static var cacheDirectory: NSString {
        get{
            return diskPaths[0] as NSString
        }
        
    }
    
    class func setObjectForKey(_ imageData:Data,imageKey:String){
        
        ImageCacheHelper.cache.setObject(imageData as AnyObject, forKey: imageKey as AnyObject)
        
    }
    
     class func getObjectForKey(_ imageKey:String)->Data?{
        
        return ImageCacheHelper.cache.object(forKey: imageKey as AnyObject) as? Data
        
    }
    
     class func getImage(_ imageUrl:String,completionHandler:@escaping (Data)->())
     {
   
          DispatchQueue.global(qos: .background).async(execute: {
               if let imgUrl = URL(string:imageUrl)
               {
                    if let imageData = try? Data(contentsOf: imgUrl)
                    {
                        completionHandler(imageData)
                        self.setObjectForKey(imageData, imageKey: "\(imageUrl.hashValue)")
                    }
               }
            })
     }
  
   class func downloadImageAtIndexPath(_ indexPath : IndexPath, mediaCollection: [Media],completionBlock: @escaping (_ result: UIImage) -> Void)
   {
        let item : Media = mediaCollection[(indexPath as NSIndexPath).row]
        let  url:String?  = item.mediaimageUrl
        let diskPath = ImageCacheHelper.cacheDirectory.appendingPathComponent("\(url?.hashValue)")
        if let imageURL = url
        {
            if ImageCacheHelper.fileManager.fileExists(atPath: diskPath)
            {
                
                 DispatchQueue.global(qos: .background).async(execute: {
                   if let image =  UIImage(contentsOfFile: diskPath)
                   {
                        completionBlock(image)
                        ImageCacheHelper.setObjectForKey(UIImageJPEGRepresentation(image, 1.0)!, imageKey: "\(imageURL.hashValue)")
                    }
                })
                                
            }
            else
            {
                ImageCacheHelper.getImage(url!,completionHandler: { imageData in
                try? imageData.write(to: URL(fileURLWithPath: diskPath), options: [.atomic])
                if let userImage =  UIImage(data: imageData)
                {
                    completionBlock(userImage)
                }
              })
            }
        }
    }
    
    class func getImageforThumbnail(_ url : String?,completionBlock: (_ result: UIImage) -> Void)
    {
        
        let diskPath = ImageCacheHelper.cacheDirectory.appendingPathComponent("\(url?.hashValue)")
        if  let imageURL = url
        {
            if ImageCacheHelper.fileManager.fileExists(atPath: diskPath)
            {
               if let image =  UIImage(contentsOfFile: diskPath)
               {
                    completionBlock(image)
                    ImageCacheHelper.setObjectForKey(UIImageJPEGRepresentation(image, 1.0)!, imageKey: "\(imageURL.hashValue)")
                }
                
            }
        }
    }

}
