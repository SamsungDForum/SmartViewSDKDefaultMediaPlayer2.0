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
    
    var totalMediaDuration:Int = 0
    var mediaPlay:Bool = false
    var mediaPause:Bool = false
    var mute:Bool = false
    var repeatAll:Int = 0
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        self.view.layer.borderWidth = 1
        self.view.layer.borderColor = UIColor.lightGray.cgColor
        startTimeText.isHidden = true
        completionTimeText.isHidden = true
        timeSliderBar.isHidden = true
        timeSliderBar.frame = CGRect(x: timeSliderBar.frame.origin.x, y: timeSliderBar.frame.origin.y, width: self.view.frame.size.width - 60, height: timeSliderBar.frame.size.height)
        timeSliderBar.setThumbImage(UIImage(named: "thumb"), for:  UIControlState())
        volumeSliderBar.setThumbImage(UIImage(named: "thumb"), for:  UIControlState())
        completionTimeText.frame = CGRect(x: timeSliderBar.frame.origin.x + self.view.frame.size.width - 59 , y: completionTimeText.frame.origin.y, width: completionTimeText.frame.size.width, height: completionTimeText.frame.size.height)
        startTimeText.text = timeFormatted(0)
        completionTimeText.text = timeFormatted(0)
        if self.mimageView != nil
        {
            self.mimageView.layer.borderWidth = 1.0
            self.mimageView.layer.cornerRadius = 5.0
            self.mimageView.layer.borderColor = UIColor.lightGray.cgColor
            self.mimageView.layer.masksToBounds = true
        }
        
        NotificationCenter.default.addObserver(self, selector: #selector(MediaPlayViewController.Shuffle(_:)), name: NSNotification.Name(rawValue: "NotifyShuffle"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(MediaPlayViewController.Repeat(_:)), name: NSNotification.Name(rawValue: "NotifyRepeat"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(MediaPlayViewController.Stop), name: NSNotification.Name(rawValue: "NotifyStop"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(MediaPlayViewController.Play), name: NSNotification.Name(rawValue: "NotifyPlay"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(MediaPlayViewController.Pause), name: NSNotification.Name(rawValue: "NotifyPause"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(MediaPlayViewController.thumbnailChange(_:)), name: NSNotification.Name(rawValue: "onThumbnailChange"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(MediaPlayViewController.Mute), name: NSNotification.Name(rawValue: "NotifyMute"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(MediaPlayViewController.UnMute), name: NSNotification.Name(rawValue: "NotifyUnMute"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(MediaPlayViewController.updateTimeProgress(_:)), name: NSNotification.Name(rawValue: "NotifyTimeProgress"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(MediaPlayViewController.showSlider), name: NSNotification.Name(rawValue: "NotifySliderShow"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(MediaPlayViewController.hideSlider), name: NSNotification.Name(rawValue: "NotifySliderHide"), object: nil)

        let tapOnTimeBar = UITapGestureRecognizer(target: self, action: #selector(MediaPlayViewController.handleTapOnTimeProgessBar(_:)))
        tapOnTimeBar.numberOfTapsRequired = 1
        
        timeSliderBar.addGestureRecognizer(tapOnTimeBar)
        timeSliderBar.value = 0.0
        
        let tapOnVolumeBar = UITapGestureRecognizer(target: self, action: #selector(MediaPlayViewController.handleTapOnVolumeProgessBar(_:)))
        tapOnVolumeBar.numberOfTapsRequired = 1
        
        volumeSliderBar.addGestureRecognizer(tapOnVolumeBar)
        volumeSliderBar.value = 0.0
        
        rewindImgView.isUserInteractionEnabled = true
        let rewindRecognizer = UITapGestureRecognizer(target: self, action: #selector(MediaPlayViewController.rewindAction(_:)))
        rewindImgView.addGestureRecognizer(rewindRecognizer)
        
        playPauseImgView.isUserInteractionEnabled = true
        let playRecognizer = UITapGestureRecognizer(target: self, action: #selector(MediaPlayViewController.playPauseAction(_:)))
        playPauseImgView.addGestureRecognizer(playRecognizer)
        
        forwardImgView.isUserInteractionEnabled = true
        let forwardRecognizer = UITapGestureRecognizer(target: self, action: #selector(MediaPlayViewController.forwardAction(_:)))
        forwardImgView.addGestureRecognizer(forwardRecognizer)
        
        nextImgView.isUserInteractionEnabled = true
        let nextRecognizer = UITapGestureRecognizer(target: self, action: #selector(MediaPlayViewController.nextAction(_:)))
        nextImgView.addGestureRecognizer(nextRecognizer)
        
        muteUnmuteImgView.isUserInteractionEnabled = true
        let muteRecognizer = UITapGestureRecognizer(target: self, action: #selector(MediaPlayViewController.muteUnmuteAction(_:)))
        muteUnmuteImgView.addGestureRecognizer(muteRecognizer)
        
        stopImgView.isUserInteractionEnabled = true
        let stopRecognizer = UITapGestureRecognizer(target: self, action: #selector(MediaPlayViewController.stopAction(_:)))
        stopImgView.addGestureRecognizer(stopRecognizer)
        
        repeatImgView.isUserInteractionEnabled = true
        let repeatRecognizer = UITapGestureRecognizer(target: self, action: #selector(MediaPlayViewController.repeatAction(_:)))
        repeatImgView.addGestureRecognizer(repeatRecognizer)
        
        shuffleImgView.isUserInteractionEnabled = true
        let shuffleRecognizer = UITapGestureRecognizer(target: self, action: #selector(MediaPlayViewController.shuffleAction(_:)))
        shuffleImgView.addGestureRecognizer(shuffleRecognizer)
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
    
    func previousAction(_ gestureRecognizer:UITapGestureRecognizer)
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
    
    func rewindAction(_ gestureRecognizer:UITapGestureRecognizer)
    {
        
        if (MediaShareController.sharedInstance.playType == "video")
        {
            MediaShareController.sharedInstance.videoplayer?.rewind()
        }
    }
    
    func playPauseAction(_ gestureRecognizer:UITapGestureRecognizer)
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
    
    func forwardAction(_ gestureRecognizer:UITapGestureRecognizer)
    {
        if (MediaShareController.sharedInstance.playType == "video")
        {
            MediaShareController.sharedInstance.videoplayer?.forward()
        }

    }
    
    func nextAction(_ gestureRecognizer:UITapGestureRecognizer)
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
    
    func muteUnmuteAction(_ gestureRecognizer:UITapGestureRecognizer)
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
    
    func stopAction(_ gestureRecognizer:UITapGestureRecognizer)
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
    
    func repeatAction(_ gestureRecognizer:UITapGestureRecognizer)
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
    
    func shuffleAction(_ gestureRecognizer:UITapGestureRecognizer)
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
        completionTimeText.text = timeFormatted(totalMediaDuration/1000)
        startTimeText.text = timeFormatted(0)
    }
    
    func Repeat(_ notification: Notification)
    {
        let repeatMode = (notification as NSNotification).userInfo?["repeatMode"] as! String
        
        if(repeatMode.compare("repeatOff") == ComparisonResult.orderedSame)
        {
            repeatImgView.image = UIImage(named: "repeat_off")
        }
        else if(repeatMode.compare("repeatSingle") == ComparisonResult.orderedSame)
        {
            repeatImgView.image = UIImage(named: "repeat_single")
        }
        else if(repeatMode.compare("repeatAll") == ComparisonResult.orderedSame)
        {
            repeatImgView.image = UIImage(named: "repeat_all")
        }
    }
    
    func Shuffle(_ notification: Notification)
    {
         let shuffleStatus = (notification as NSNotification).userInfo?["shuffleStatus"] as! Bool
        
        if(shuffleStatus)
        {
            shuffleImgView.image = UIImage(named: "shuffle_off")
        }
        else
        {
            shuffleImgView.image = UIImage(named: "shuffle_on")
        }
    }
    
    func thumbnailChange(_ notification: Notification)
    {
       if MediaShareController.sharedInstance.isConnected == true
        {
            let imageurl: String?
            imageurl = (notification as NSNotification).userInfo?["url"] as? String
        
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
            
          ImageCacheHelper.getImageforThumbnail(imageurl!,completionBlock: { (result: UIImage) in
            DispatchQueue.main.async
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
    
    
    func updateTimeProgress(_ notification: Notification)
    {
        let progressValue = (notification as NSNotification).userInfo?["progress"] as! Int
        let  totalDuration = (notification as NSNotification).userInfo?["totalDuration"] as! Int
        
        totalMediaDuration = (notification as NSNotification).userInfo?["totalDuration"] as! Int
        timeSliderBar.value = Float(progressValue*100)/Float(totalDuration)
        
        startTimeText.text = timeFormatted(Int(progressValue/1000))
        completionTimeText.text = timeFormatted(Int(totalDuration/1000))
        
        
    }
    
    @IBAction func timeSliderValueChanged(_ sender: AnyObject)
    {
        let currentValue = sender.value as Float
        let currenTimeinSec = ((currentValue/100.0)*Float(totalMediaDuration))/1000.0
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
    
    func handleTapOnTimeProgessBar(_ sender: UITapGestureRecognizer)
    {
        if sender.state == .ended
        {
            let location = (sender.location(in: self.view))
            let x = Float(location.x)
            let width = Float(self.view.frame.size.width - 40)
            let margin = Float(timeSliderBar.frame.origin.x)
            let progressValue = ((x - margin)/width)*Float(totalMediaDuration)
            
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

    @IBAction func volumeSliderValueChanged(_ sender: AnyObject)
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
    
    func handleTapOnVolumeProgessBar(_ sender: UITapGestureRecognizer)
    {
        if sender.state == .ended
        {
            let location = (sender.location(in: self.view))
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
        timeSliderBar.isHidden = false
        startTimeText.isHidden = false
        completionTimeText.isHidden = false
    }
    func hideSlider()
    {
        timeSliderBar.isHidden = true
        startTimeText.isHidden = true
        completionTimeText.isHidden = true
    }
    func timeFormatted(_ totalSeconds: Int) -> String
    {
        let seconds: Int = totalSeconds % 60
        let minutes: Int = (totalSeconds / 60) % 60
        return String(format: "%02d:%02d",minutes, seconds)
    }
}
