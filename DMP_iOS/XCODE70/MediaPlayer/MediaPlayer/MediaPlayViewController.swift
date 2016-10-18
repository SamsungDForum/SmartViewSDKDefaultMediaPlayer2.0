//
//  MediaPlayViewController.swift
//  MediaPlayer
//
//  Created by CHIRAG BAHETI on 30/08/16.
//  Copyright © 2016 samsung. All rights reserved.
//

import Foundation
import UIKit
import SmartView

class MediaPlayViewController : UIViewController
{
    
    @IBOutlet var mediaPlayViewController: UIView!
    @IBOutlet weak var completionTimeText: UILabel!
    @IBOutlet weak var startTimeText: UILabel!
    @IBOutlet weak var volumeSliderBar: UISlider!
    @IBOutlet weak var timeSliderBar: UISlider!
    @IBOutlet weak var mimageView: UIImageView!
    @IBOutlet weak var previousImgView: UIImageView!
    @IBOutlet weak var rewindImgView: UIImageView!
    @IBOutlet weak var playPauseImgView: UIImageView!
    @IBOutlet weak var forwardImgView: UIImageView!
    @IBOutlet weak var nextImgView: UIImageView!
    @IBOutlet weak var volumeDownImgView: UIImageView!
    @IBOutlet weak var volumeUpImgView: UIImageView!
    @IBOutlet weak var muteUnmuteImgView: UIImageView!
    @IBOutlet weak var stopImgView: UIImageView!
    @IBOutlet weak var repeatImgView: UIImageView!
    @IBOutlet weak var shuffleImgView: UIImageView!
    
    var totalMediaDuration:Float = 0.0
    var mediaPlay:Bool = false
    var mediaPause:Bool = false
    var mute:Bool = false
    var repeatAll:Int = 0
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        self.view.layer.borderWidth = 1
        self.view.layer.borderColor = UIColor.lightGrayColor().CGColor
        startTimeText.hidden = true
        completionTimeText.hidden = true
        timeSliderBar.hidden = true
        timeSliderBar.frame = CGRectMake(timeSliderBar.frame.origin.x, timeSliderBar.frame.origin.y, self.view.frame.size.width - 60, timeSliderBar.frame.size.height)
        timeSliderBar.setThumbImage(UIImage(named: "thumb"), forState:  UIControlState.Normal)
        volumeSliderBar.setThumbImage(UIImage(named: "thumb"), forState:  UIControlState.Normal)
        completionTimeText.frame = CGRectMake(timeSliderBar.frame.origin.x + self.view.frame.size.width - 59 , completionTimeText.frame.origin.y, completionTimeText.frame.size.width, completionTimeText.frame.size.height)
        startTimeText.text = timeFormatted(0)
        completionTimeText.text = timeFormatted(0)
        if self.mimageView != nil
        {
            self.mimageView.layer.borderWidth = 1.0
            self.mimageView.layer.cornerRadius = 5.0
            self.mimageView.layer.borderColor = UIColor.lightGrayColor().CGColor
            self.mimageView.layer.masksToBounds = true
        }
        
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "Shuffle:", name: "NotifyShuffle", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "Repeat:", name: "NotifyRepeat", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "Stop", name: "NotifyStop", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "Play", name: "NotifyPlay", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "Pause", name: "NotifyPause", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "thumbnailChange:", name: "onThumbnailChange", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "Mute", name: "NotifyMute", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "UnMute", name: "NotifyUnMute", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "updateTimeProgress:", name: "NotifyTimeProgress", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "showSlider", name: "NotifySliderShow", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "hideSlider", name: "NotifySliderHide", object: nil)

        let tapOnTimeBar = UITapGestureRecognizer(target: self, action: "handleTapOnTimeProgessBar:")
        tapOnTimeBar.numberOfTapsRequired = 1
        
        timeSliderBar.addGestureRecognizer(tapOnTimeBar)
        timeSliderBar.value = 0.0
        
        let tapOnVolumeBar = UITapGestureRecognizer(target: self, action: "handleTapOnVolumeProgessBar:")
        tapOnVolumeBar.numberOfTapsRequired = 1
        
        volumeSliderBar.addGestureRecognizer(tapOnVolumeBar)
        volumeSliderBar.value = 0.0
        
        rewindImgView.userInteractionEnabled = true
        let rewindRecognizer = UITapGestureRecognizer(target: self, action: Selector("rewindAction:"))
        rewindImgView.addGestureRecognizer(rewindRecognizer)
        
        playPauseImgView.userInteractionEnabled = true
        let playRecognizer = UITapGestureRecognizer(target: self, action: Selector("playPauseAction:"))
        playPauseImgView.addGestureRecognizer(playRecognizer)
        
        forwardImgView.userInteractionEnabled = true
        let forwardRecognizer = UITapGestureRecognizer(target: self, action: Selector("forwardAction:"))
        forwardImgView.addGestureRecognizer(forwardRecognizer)
        
        nextImgView.userInteractionEnabled = true
        let nextRecognizer = UITapGestureRecognizer(target: self, action: Selector("nextAction:"))
        nextImgView.addGestureRecognizer(nextRecognizer)
        
        muteUnmuteImgView.userInteractionEnabled = true
        let muteRecognizer = UITapGestureRecognizer(target: self, action: Selector("muteUnmuteAction:"))
        muteUnmuteImgView.addGestureRecognizer(muteRecognizer)
        
        stopImgView.userInteractionEnabled = true
        let stopRecognizer = UITapGestureRecognizer(target: self, action: Selector("stopAction:"))
        stopImgView.addGestureRecognizer(stopRecognizer)
        
        repeatImgView.userInteractionEnabled = true
        let repeatRecognizer = UITapGestureRecognizer(target: self, action: Selector("repeatAction:"))
        repeatImgView.addGestureRecognizer(repeatRecognizer)
        
        shuffleImgView.userInteractionEnabled = true
        let shuffleRecognizer = UITapGestureRecognizer(target: self, action: Selector("shuffleAction:"))
        shuffleImgView.addGestureRecognizer(shuffleRecognizer)
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
    
    func previousAction(gestureRecognizer:UITapGestureRecognizer)
    {
        if  (MediaShareController.sharedInstance.playType == "photo")
        {
            MediaShareController.sharedInstance.photoplayer?.previous()
        }
        else if (MediaShareController.sharedInstance.playType == "audio")
        {
            MediaShareController.sharedInstance.audioplayer?.previous()
        }
        else if (MediaShareController.sharedInstance.playType == "video")
        {
            MediaShareController.sharedInstance.videoplayer?.previous()
        }
    }
    
    func rewindAction(gestureRecognizer:UITapGestureRecognizer)
    {
        
        if (MediaShareController.sharedInstance.playType == "video")
        {
            MediaShareController.sharedInstance.videoplayer?.rewind()
        }
    }
    
    func playPauseAction(gestureRecognizer:UITapGestureRecognizer)
    {
        if(mediaPlay)
        {
            if  (MediaShareController.sharedInstance.playType == "photo")
            {
                MediaShareController.sharedInstance.photoplayer?.pause()
            }
            else if (MediaShareController.sharedInstance.playType == "audio")
            {
                MediaShareController.sharedInstance.audioplayer?.pause()
            }
            else if (MediaShareController.sharedInstance.playType == "video")
            {
                MediaShareController.sharedInstance.videoplayer?.pause()
            }
            mediaPlay = false
            mediaPause = true
        }
        else if(mediaPause)
        {
            if  (MediaShareController.sharedInstance.playType == "photo")
            {
                MediaShareController.sharedInstance.photoplayer?.play()
            }
            else if (MediaShareController.sharedInstance.playType == "audio")
            {
                MediaShareController.sharedInstance.audioplayer?.play()
            }
            else if (MediaShareController.sharedInstance.playType == "video")
            {
                MediaShareController.sharedInstance.videoplayer?.play()
            }
            mediaPlay = true
            mediaPause = false
        }
    }
    
    func forwardAction(gestureRecognizer:UITapGestureRecognizer)
    {
        if (MediaShareController.sharedInstance.playType == "video")
        {
            MediaShareController.sharedInstance.videoplayer?.forward()
        }

    }
    
    func nextAction(gestureRecognizer:UITapGestureRecognizer)
    {
        if  (MediaShareController.sharedInstance.playType == "photo")
        {
            MediaShareController.sharedInstance.photoplayer?.next()
        }
        else if (MediaShareController.sharedInstance.playType == "audio")
        {
            MediaShareController.sharedInstance.audioplayer?.next()
        }
        else if (MediaShareController.sharedInstance.playType == "video")
        {
            MediaShareController.sharedInstance.videoplayer?.next()
        }

    }
    
    func muteUnmuteAction(gestureRecognizer:UITapGestureRecognizer)
    {
        if(mute)
        {
            if  (MediaShareController.sharedInstance.playType == "photo")
            {
                MediaShareController.sharedInstance.photoplayer?.unMute()
            }
            else if (MediaShareController.sharedInstance.playType == "audio")
            {
                MediaShareController.sharedInstance.audioplayer?.unMute()
            }
            else if (MediaShareController.sharedInstance.playType == "video")
            {
                MediaShareController.sharedInstance.videoplayer?.unMute()
            }
           
        }
        else
        {
            if  (MediaShareController.sharedInstance.playType == "photo")
            {
                MediaShareController.sharedInstance.photoplayer?.mute()
            }
            else if (MediaShareController.sharedInstance.playType == "audio")
            {
                MediaShareController.sharedInstance.audioplayer?.mute()
            }
            else if (MediaShareController.sharedInstance.playType == "video")
            {
                MediaShareController.sharedInstance.videoplayer?.mute()
            }
            mute = true
            
        }
    }
    
    func stopAction(gestureRecognizer:UITapGestureRecognizer)
    {
        
        if  (MediaShareController.sharedInstance.playType == "photo")
        {
            MediaShareController.sharedInstance.photoplayer?.stop()
        }
        else if (MediaShareController.sharedInstance.playType == "audio")
        {
            MediaShareController.sharedInstance.audioplayer?.stop()
        }
        else if (MediaShareController.sharedInstance.playType == "video")
        {
            MediaShareController.sharedInstance.videoplayer?.stop()
        }

    }
    
    func repeatAction(gestureRecognizer:UITapGestureRecognizer)
    {
        if (MediaShareController.sharedInstance.playType == "audio")
        {
            MediaShareController.sharedInstance.audioplayer?.repeatQueue()
        }
        else if (MediaShareController.sharedInstance.playType == "video")
        {
            MediaShareController.sharedInstance.videoplayer?.repeatQueue()
        }
    }
    
    func shuffleAction(gestureRecognizer:UITapGestureRecognizer)
    {
        if (MediaShareController.sharedInstance.playType == "audio")
        {
            MediaShareController.sharedInstance.audioplayer?.shuffle()
        }
//        else if (MediaShareController.sharedInstance.playType == "video")
//        {
//            MediaShareController.sharedInstance.videoplayer?.shuffle()
//        }
    }
    
    func Play()
    {
        mediaPlay = true
        mediaPause = false
        playPauseImgView.image = UIImage(named: "pause")
    }
    
    func Pause()
    {
        mediaPause = true
        mediaPlay = false
        playPauseImgView.image = UIImage(named: "play")
    }
    
    func Stop()
    {
        mediaPause = false
        mediaPlay = true
        playPauseImgView.image = UIImage(named: "play")
        timeSliderBar.value = 0.0
        completionTimeText.text = timeFormatted(0)
    }
    
    func Repeat(notification: NSNotification)
    {
        let repeatMode = notification.userInfo?["repeatMode"] as! String
        
        if(repeatMode.compare("repeatOff") == NSComparisonResult.OrderedSame)
        {
            repeatImgView.image = UIImage(named: "repeat_off")
        }
        else if(repeatMode.compare("repeatSingle") == NSComparisonResult.OrderedSame)
        {
            repeatImgView.image = UIImage(named: "repeat_single")
        }
        else if(repeatMode.compare("repeatAll") == NSComparisonResult.OrderedSame)
        {
            repeatImgView.image = UIImage(named: "repeat_all")
        }
    }
    
    func Shuffle(notification: NSNotification)
    {
         let shuffleStatus = notification.userInfo?["shuffleStatus"] as! Bool
        
        if(shuffleStatus)
        {
            shuffleImgView.image = UIImage(named: "shuffle_off")
        }
        else
        {
            shuffleImgView.image = UIImage(named: "shuffle_on")
        }
    }
    
    func thumbnailChange(notification: NSNotification)
    {
        if MediaShareController.sharedInstance.isConnected == true
        {
          let imageurl = notification.userInfo?["url"] as! String
        
         if imageurl == ""
         {
            if MediaShareController.sharedInstance.playType == "audio"{
                self.mimageView.image = UIImage(named:"Music_Placeholder.png")!
            }
            else if MediaShareController.sharedInstance.playType == "video"{
                self.mimageView.image = UIImage(named:"video_Placeholder.jpeg")!
            }
            else if MediaShareController.sharedInstance.playType == "photo"{
                self.mimageView.image = UIImage(named:"image_Placeholder.png")!
            }
         }
            
          ImageCacheHelper.getImageforThumbnail(imageurl,completionBlock: { (result: UIImage) in
            dispatch_async(dispatch_get_main_queue())
                {
                 self.mimageView.image = result
                }
            })
        }
    }
    
    func Mute()
    {
        mute = true
        muteUnmuteImgView.image = UIImage(named: "unmute")
    }
    
    func UnMute()
    {
         mute = false
         muteUnmuteImgView.image = UIImage(named: "mute")
    }
    
    
    func updateTimeProgress(notification: NSNotification)
    {
        let progressValue = notification.userInfo?["progress"] as! Float
        let  totalDuration = notification.userInfo?["totalDuration"] as! Float
        
        totalMediaDuration = notification.userInfo?["totalDuration"] as! Float
        timeSliderBar.value = (progressValue*100)/totalDuration
        
        startTimeText.text = timeFormatted(Int(progressValue/1000))
        completionTimeText.text = timeFormatted(Int(totalDuration/1000))
        
    }
    
    @IBAction func timeSliderValueChanged(sender: AnyObject)
    {
        let currentValue = sender.value as Float
        let currenTimeinSec = ((currentValue/100.0)*totalMediaDuration)/1000.0
        startTimeText.text = timeFormatted(Int(currenTimeinSec))
        if MediaShareController.sharedInstance.playType == "audio"
        {
            MediaShareController.sharedInstance.audioplayer?.seek(Double(currenTimeinSec))
        }
        else if MediaShareController.sharedInstance.playType == "video"
        {
            MediaShareController.sharedInstance.videoplayer?.seek(Double(currenTimeinSec))
        }
        
    }
    
    func handleTapOnTimeProgessBar(sender: UITapGestureRecognizer)
    {
        if sender.state == .Ended
        {
            let location = (sender.locationInView(self.view))
            let x = Float(location.x)
            let width = Float(self.view.frame.size.width - 40)
            let margin = Float(timeSliderBar.frame.origin.x)
            let progressValue = ((x - margin)/width)*totalMediaDuration
            
            startTimeText.text = timeFormatted(Int(progressValue/1000.0))
            timeSliderBar.value = (x-margin)*(100/width)
            if MediaShareController.sharedInstance.playType == "audio"
            {
              MediaShareController.sharedInstance.audioplayer?.seek(Double(progressValue/1000.0))
            }
            else if MediaShareController.sharedInstance.playType == "video"
            {
              MediaShareController.sharedInstance.videoplayer?.seek(Double(progressValue/1000.0))
            }
        }
    }

    @IBAction func volumeSliderValueChanged(sender: AnyObject)
    {
         let currentValue = sender.value as Float
         let currenVolume = currentValue * 100
        if MediaShareController.sharedInstance.playType == "audio"
        {
            MediaShareController.sharedInstance.audioplayer?.setVolume(UInt8(currenVolume))
        }
        else if MediaShareController.sharedInstance.playType == "video"
        {
            MediaShareController.sharedInstance.videoplayer?.setVolume(UInt8(currenVolume))
        }
        else if MediaShareController.sharedInstance.playType == "photo"
        {
            MediaShareController.sharedInstance.photoplayer?.setVolume(UInt8(currenVolume))
        }
    }
    
    func handleTapOnVolumeProgessBar(sender: UITapGestureRecognizer)
    {
        if sender.state == .Ended
        {
            let location = (sender.locationInView(self.view))
            let x = Float(location.x)
            let width = Float(volumeSliderBar.frame.size.width)
            let margin = Float(volumeSliderBar.frame.origin.x)
            let progressValue = ((x - margin)/width)*100
            
            volumeSliderBar.value = ((x - margin)/width)
            
            if MediaShareController.sharedInstance.playType == "audio"
            {
                MediaShareController.sharedInstance.audioplayer?.setVolume(UInt8(progressValue))
            }
            else if MediaShareController.sharedInstance.playType == "video"
            {
                MediaShareController.sharedInstance.videoplayer?.setVolume(UInt8(progressValue))
            }
            else if MediaShareController.sharedInstance.playType == "photo"
            {
                MediaShareController.sharedInstance.photoplayer?.setVolume(UInt8(progressValue))
            }
        }
    }
    
    func showSlider()
    {
        startTimeText.text = timeFormatted(0)
        completionTimeText.text = timeFormatted(0)
        timeSliderBar.value = 0.0
        timeSliderBar.hidden = false
        startTimeText.hidden = false
        completionTimeText.hidden = false
    }
    func hideSlider()
    {
        timeSliderBar.hidden = true
        startTimeText.hidden = true
        completionTimeText.hidden = true
    }
    func timeFormatted(totalSeconds: Int) -> String
    {
        let seconds: Int = totalSeconds % 60
        let minutes: Int = (totalSeconds / 60) % 60
        return String(format: "%02d:%02d",minutes, seconds)
    }
}