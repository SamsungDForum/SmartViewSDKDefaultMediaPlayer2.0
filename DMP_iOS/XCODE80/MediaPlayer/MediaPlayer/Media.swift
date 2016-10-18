//
//  Media.swift
//  PhotoShare
//
//  Created by Amit Khoth on 5/25/16.
//  Copyright © 2016 Samsung. All rights reserved.
//

import UIKit

class Media: NSObject
{
    var mediaimageUrl : String!
    var mediaimageUrl_HD :String!
    var mediaTitle :String!
    var mediaUrl :String!
    var mediaAlbumName :String!
    var mediaAlbumArt :String!
    
    init(url :String)
    {
        mediaimageUrl = ""
        mediaTitle = ""
        mediaUrl = ""
        mediaAlbumArt = ""
        mediaAlbumName = ""
    }
}
