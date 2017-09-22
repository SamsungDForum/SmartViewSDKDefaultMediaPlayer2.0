//
//  SettingsViewController.swift
//  MediaPlayer
//
//  Created by aseemkapoor on 16/02/17.
//  Copyright © 2017 samsung. All rights reserved.
//

import Foundation
import UIKit
import SmartView
import QuartzCore

private let kConstraintScroll = 240
private let kCornerRadius = 7

class SettingsViewController : UIViewController
{
    
    @IBOutlet weak var scroll: UIScrollView!
    @IBOutlet weak var textFieldAudioUrl: UITextField!
    @IBOutlet weak var contentView: UIView!
    
    @IBOutlet weak var disconnectButton: UIButton!
    @IBOutlet weak var standbyDevButton: UIButton!
    @IBOutlet weak var standbyScreenButton: UIButton!
    
    @IBOutlet weak var standbyScreenDetailView: UIView!
    @IBOutlet weak var textFieldwatermarkUrl: UITextField!
    @IBOutlet weak var textFieldUrl3: UITextField!
    @IBOutlet weak var textFieldUrl2: UITextField!
    @IBOutlet weak var textFieldUrl1: UITextField!
    
    @IBOutlet weak var constraintLineTop: NSLayoutConstraint!
    @IBOutlet weak var constraintScrollBottom: NSLayoutConstraint!
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        
        textFieldAudioUrl.delegate = self
        
        textFieldUrl1.delegate = self
        textFieldUrl2.delegate = self
        textFieldUrl3.delegate = self
        textFieldwatermarkUrl.delegate = self
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)        
        updateData()
        
        if disconnectButton.currentTitle == "STOP PLAYING"
        {
            disconnectButton.backgroundColor = .lightGray
        }
        else
        {
            disconnectButton.backgroundColor = .orange
        }
        
        if standbyDevButton.currentTitle == "HIDE"
        {
            standbyDevButton.backgroundColor = .lightGray
        }
        else
        {
            standbyDevButton.backgroundColor = .orange
        }
        
        if standbyScreenButton.currentTitle == "DISABLED"
        {
            standbyScreenButton.backgroundColor = .lightGray
            standbyScreenDetailView.isHidden = true
            constraintLineTop.constant = 14
        }
        else
        {
            standbyScreenButton.backgroundColor = .orange
            standbyScreenDetailView.isHidden = false
            constraintLineTop.constant = 170
        }
        
    }
    
    @IBAction func closeView(_ sender: Any) {
        saveData()
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func clearDevices(_ sender: Any) {
        MediaShareController.sharedInstance.search.clearStandbyDevices()
    }
    
    @IBAction func disconnectTouch(_ sender: Any) {
        if disconnectButton.currentTitle == "KEEP PLAYING"
        {
            disconnectButton.setTitle("STOP PLAYING", for: .normal)
            disconnectButton.backgroundColor = .lightGray
        }
        else
        {
            disconnectButton.setTitle("KEEP PLAYING", for: .normal)
            disconnectButton.backgroundColor = .orange
        }
        
    }
    @IBAction func standbyDevTouch(_ sender: Any) {
        if standbyDevButton.currentTitle == "SHOWING"
        {
            standbyDevButton.setTitle("HIDE", for: .normal)
            standbyDevButton.backgroundColor = .lightGray
        }
        else
        {
            standbyDevButton.setTitle("SHOWING", for: .normal)
            standbyDevButton.backgroundColor = .orange
        }
    }
    
    @IBAction func standbyScreen(_ sender: Any) {
        
        if standbyScreenButton.currentTitle == "ENABLED"
        {
            standbyScreenButton.setTitle("DISABLED", for: .normal)
            standbyScreenButton.backgroundColor = .lightGray
            standbyScreenDetailView.isHidden = true
            constraintLineTop.constant = 14
        }
        else
        {
            standbyScreenButton.setTitle("ENABLED", for: .normal)
            standbyScreenButton.backgroundColor = .orange
            standbyScreenDetailView.isHidden = false
            constraintLineTop.constant = 170
        }
    }
    
    func saveData()
    {
        MediaShareController.sharedInstance.settingsValue.audioURL = textFieldAudioUrl.text ?? ""
        MediaShareController.sharedInstance.settingsValue.disconnectKeepPlaying = disconnectButton.currentTitle == "KEEP PLAYING" ? true : false
        MediaShareController.sharedInstance.settingsValue.showStandbyDev = standbyDevButton.currentTitle == "SHOWING" ? true : false
        MediaShareController.sharedInstance.settingsValue.showStandbyScreen = standbyScreenButton.currentTitle == "ENABLED" ? true: false
        MediaShareController.sharedInstance.settingsValue.url1 = textFieldUrl1.text ?? ""
        MediaShareController.sharedInstance.settingsValue.url2 = textFieldUrl2.text ?? ""
        MediaShareController.sharedInstance.settingsValue.url3 = textFieldUrl3.text ?? ""
        MediaShareController.sharedInstance.settingsValue.watermarkURL = textFieldwatermarkUrl.text ?? ""
    }
    
    func updateData()
    {
        textFieldAudioUrl.text = MediaShareController.sharedInstance.settingsValue.audioURL
        disconnectButton.setTitle((MediaShareController.sharedInstance.settingsValue.disconnectKeepPlaying ? "KEEP PLAYING" : "STOP PLAYING"), for: .normal)
        standbyDevButton.setTitle((MediaShareController.sharedInstance.settingsValue.showStandbyDev ? "SHOWING" : "HIDE"), for: .normal)
        standbyScreenButton.setTitle(MediaShareController.sharedInstance.settingsValue.showStandbyScreen ? "ENABLED" : "DISABLED", for: .normal)
        textFieldUrl1.text = MediaShareController.sharedInstance.settingsValue.url1
        textFieldUrl2.text = MediaShareController.sharedInstance.settingsValue.url2
        textFieldUrl3.text = MediaShareController.sharedInstance.settingsValue.url3
        textFieldwatermarkUrl.text = MediaShareController.sharedInstance.settingsValue.watermarkURL
        
    }
}

extension SettingsViewController : UITextFieldDelegate
{
    func textFieldShouldReturn(_ textField: UITextField) -> Bool
    {
        constraintScrollBottom.constant = 15
        textField.resignFirstResponder()
        return true
    }
    
    func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool
    {
        constraintScrollBottom.constant = CGFloat(kConstraintScroll)
//        constraintScrollTop.constant = CGFloat(-1 * kConstraintScroll)
//        textField.becomeFirstResponder()
        return true
    }
}
