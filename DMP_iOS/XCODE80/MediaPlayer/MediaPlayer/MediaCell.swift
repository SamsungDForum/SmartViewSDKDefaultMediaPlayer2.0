//
//  PhotoCell.swift
//  PhotoShare
//
//  Created by Amit Khoth on 5/25/16.
//  Copyright © 2016 Samsung. All rights reserved.
//

import UIKit
import AssetsLibrary

class MediaCell: UICollectionViewCell {
    
 
    
    @IBOutlet weak var separatorLine: UILabel!
    @IBOutlet var mImageView: UIImageView!
    @IBOutlet weak var titleTxt: UILabel!
    @IBOutlet weak var indicator: UIActivityIndicatorView!
    @IBOutlet weak var addMediaBtn: UIButton!
    @IBOutlet weak var albumName: UILabel!
    @IBOutlet weak var delMediabtn: UIButton!
    
    override func layoutSubviews()
    {
        super.layoutSubviews()
        layer.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.035).cgColor

        if self.addMediaBtn != nil
        {
        self.addMediaBtn.setTitleColor(UIColor.orange, for: UIControlState())
        self.addMediaBtn.layer.borderWidth = 1.0
        self.addMediaBtn.layer.cornerRadius = 5.0
        self.addMediaBtn.layer.borderColor = UIColor.gray.cgColor
        }
        if self.mImageView != nil
        {
        self.mImageView.layer.borderWidth = 1.0
        self.mImageView.layer.cornerRadius = 5.0
        self.mImageView.layer.borderColor = UIColor.lightGray.cgColor
        self.mImageView.layer.masksToBounds = true
        }
        if nil != separatorLine
        {
            separatorLine.frame = CGRect(x: separatorLine.frame.origin.x, y: separatorLine.frame.origin.y, width: self.frame.size.width, height: separatorLine.frame.size.height)
        }
        if delMediabtn != nil
        {
            self.delMediabtn.frame = CGRect(x: self.frame.size.width - 20 - self.delMediabtn.frame.size.width, y: self.delMediabtn.frame.origin.y, width: self.delMediabtn.frame.size.width, height: self.delMediabtn.frame.size.height)
            self.delMediabtn.setTitleColor(UIColor.orange, for: UIControlState())
            self.delMediabtn.layer.borderWidth = 1.0
            self.delMediabtn.layer.cornerRadius = 5.0
            self.delMediabtn.layer.borderColor = UIColor.lightGray.cgColor
        }
    }
    
}
