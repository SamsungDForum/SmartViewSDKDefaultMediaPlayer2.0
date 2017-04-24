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

import UIKit

class FirstViewController: UIViewController, UIPopoverPresentationControllerDelegate, UIScrollViewDelegate
{
    
    var bTVlistVisible = false
    var castItem: CastButtonItem?
    var imageView: UIImageView!
    
    @IBOutlet var firstViewController: UIView!
    @IBOutlet weak var devicelistButton: UIButton!
    @IBOutlet weak var scrollView: UIScrollView!
    @IBOutlet weak var addAllMediaButton: UIButton!
    @IBOutlet weak var tvListButton: UIButton!
    @IBOutlet weak var photoButton: UIButton!
    @IBOutlet weak var videoButton: UIButton!
    @IBOutlet weak var audioButton: UIButton!
    @IBOutlet weak var lineView: UIView!
    
    var videoView:VideoViewController?
    var audioView:AudioViewController?
    var photoView:PhotoCollectionView?
    var tvListView:MediaListController?
    var mediaPlayView:MediaPlayViewController?
    
    let sb:UIStoryboard = UIStoryboard(name:"Main", bundle: nil)
    
    override func viewDidLoad()
    {
        super.viewDidLoad()
        
        let tvListButtonImage = UIImage(named: "queue_deactive.png")?.withRenderingMode(.alwaysTemplate)
        tvListButton.setImage(tvListButtonImage, for: UIControlState())
        tvListButton.tintColor = UIColor.orange
        
        let addAllMediaButtonImage = UIImage(named: "add_all.png")?.withRenderingMode(.alwaysTemplate)
        addAllMediaButton.setImage(addAllMediaButtonImage, for: UIControlState())
        addAllMediaButton.tintColor = UIColor.orange
        
        tvListButton.isHidden = true
        tvListButton.isEnabled = false
        addAllMediaButton.isHidden = true
        addAllMediaButton.isEnabled = false
  
        NotificationCenter.default.addObserver(self, selector: #selector(FirstViewController.showButton), name: NSNotification.Name(rawValue: "onPlay"), object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(FirstViewController.hideButton), name: NSNotification.Name(rawValue: "onDisconnect"), object: nil)

        castItem = CastButtonItem(devicesButton: devicelistButton)
        
        let label = UILabel(frame: CGRect(x: 0, y: 0, width: (self.navigationController?.navigationBar.frame.size.width)!, height: (self.navigationController?.navigationBar.frame.size.height)!))
        label.text = "DMP 2.0"
        label.textAlignment = NSTextAlignment.left
        
        self.navigationItem.titleView = label
        self.navigationController?.navigationBar.tintColor = UIColor.black
        
        let button = UIButton(frame: CGRect(x: self.view.frame.width - 80, y: self.view.frame.height - 250, width: 70, height: 70))
        button.backgroundColor = .orange
        button.setImage(UIImage(named:"settings.png"), for: .normal)
        button.layer.cornerRadius = 0.5 * button.bounds.size.width
        button.clipsToBounds = true
        button.addTarget(self, action: #selector(buttonAction), for: .touchUpInside)
        
        self.view.addSubview(button)
        
        navigationItem.titleView?.tintColor = UIColor.black
        castItem!.castButton.addTarget(self, action: #selector(FirstViewController.cast), for: UIControlEvents.touchUpInside)
        castItem!.castStatus = MediaShareController.sharedInstance.getCastStatus()
        
        automaticallyAdjustsScrollViewInsets = true
        
        scrollView.frame = CGRect(x:0, y:125, width:view.bounds.size.width, height: view.bounds.size.height - 150) // height: scrollView.bounds.height
        scrollView.backgroundColor = UIColor.white
        scrollView.contentSize = CGSize(width: (view.bounds.size.width)*3, height: scrollView.bounds.height)
        scrollView.autoresizingMask = UIViewAutoresizing.flexibleWidth
        
        mediaPlayView = sb.instantiateViewController(withIdentifier: "mediaPlayView") as? MediaPlayViewController
        mediaPlayView?.view.frame = CGRect(x:0, y:scrollView.bounds.height, width: self.view.bounds.size.width, height: 150)
        mediaPlayView?.view.backgroundColor = UIColor.white
        
        photoView = sb.instantiateViewController(withIdentifier: "photoView") as? PhotoCollectionView
        photoView?.view.frame = CGRect(x:0, y:0, width: self.view.bounds.size.width, height: scrollView.bounds.height - mediaPlayView!.view.frame.height)
        photoView?.view.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.035)
        scrollView.addSubview(photoView!.view)

        audioView = sb.instantiateViewController(withIdentifier: "audioView") as? AudioViewController
        audioView?.view.frame = CGRect(x:(self.view.bounds.size.width), y:0, width: self.view.bounds.size.width, height: scrollView.bounds.height - mediaPlayView!.view.frame.height)
        audioView?.view.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.035)
        scrollView.addSubview(audioView!.view)

        videoView = sb.instantiateViewController(withIdentifier: "videoView") as? VideoViewController
        videoView?.view.frame = CGRect(x:(self.view.bounds.size.width)*2, y:0, width: self.view.bounds.size.width, height: scrollView.bounds.height - mediaPlayView!.view.frame.height)
        videoView?.view.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.035)
        scrollView.addSubview(videoView!.view)
        
        self.view.addSubview(mediaPlayView!.view)
        scrollView.delegate = self
        self.view.backgroundColor = UIColor.orange
    }
    
    func buttonAction(sender: UIButton!) {
        //print("Button tapped")
        
        
        let settingsView = sb.instantiateViewController(withIdentifier: "settingsView") as? SettingsViewController
//        settingsView?.view.frame = CGRect(x:0, y:scrollView.bounds.height, width: self.view.bounds.size.width - 50, height: self.view.bounds.size.height + 50)
        
//        let dummyViewController = UIViewController()
//        let scroll = UIScrollView(frame: CGRect(x:0, y:20, width:view.bounds.size.width - 50, height: view.bounds.size.height - 50))
//        scroll.backgroundColor = UIColor.white
//        scroll.contentSize = CGSize(width: scrollView.bounds.width, height: scrollView.bounds.height + 150)
//        scroll.autoresizingMask = UIViewAutoresizing.flexibleWidth
//        
//        scrollView.addSubview(settingsView!.view)
//        
//        dummyViewController.view.addSubview(scrollView)
        settingsView?.modalPresentationStyle = UIModalPresentationStyle.overCurrentContext
        
        present(settingsView!, animated: true, completion: nil)
        //self.navigationController?.pushViewController(settingsView!, animated: false)
        
    }
    
    override func viewWillAppear(_ animated: Bool)
    {
       // title = "Default Media Player"
    }
    
    override func viewWillDisappear(_ animated: Bool)
    {
       // title = nil
    }

    override func didReceiveMemoryWarning()
    {
        super.didReceiveMemoryWarning()

        // Dispose of any resources that can be recreated.
    }
    
    override func viewWillLayoutSubviews()
    {
        super.viewWillLayoutSubviews()
    }
    // 2: Add the cast button action
    func cast()
    {
        switch castItem!.castStatus {
    
        case .notReady:
            return
        case .connecting:
            return
        case .connected:
            let termiateApp = TerminateAppViewController(nibName: "TerminateAppViewController", bundle: nil)
            presentPopover(termiateApp)
        case .readyToConnect:
            let deviceList = DeviceListViewController(style: UITableViewStyle.plain)
            presentPopover(deviceList)
        }
    }

    func presentPopover(_ viewController: UIViewController)
    {
        viewController.preferredContentSize = CGSize(width: 320, height: 186)
        viewController.modalPresentationStyle = UIModalPresentationStyle.popover
        let presentationController = viewController.popoverPresentationController
        presentationController!.sourceView = castItem!.castButton
        presentationController!.sourceRect = castItem!.castButton.bounds
        viewController.popoverPresentationController!.delegate = self
        present(viewController, animated: false, completion: {})
    }
    
    func adaptivePresentationStyle(for controller: UIPresentationController) -> UIModalPresentationStyle
    {
        // Return no adaptive presentation style, use default presentation behaviour
        return .none
    }
    
    @IBAction func addAllMediaToTVqueue(_ sender: AnyObject)
    {
        
        let page = (scrollView.contentOffset.x) / (scrollView.frame.size.width)
        
        var item1 = [String: AnyObject]()
        var list = [Dictionary<String, AnyObject>]()
        
        let uri = "uri"
        let title = "title"
        let albumName = "albumName"
        let albumArt = "albumArt"
        let thumbnailUrl = "thumbnailUrl"
        if page == 0
        {
            MediaShareController.sharedInstance.deviceMediaCollection = (photoView?.deviceMediaCollection)!
            for mediaItem in MediaShareController.sharedInstance.deviceMediaCollection
            {
                item1[uri] = mediaItem.mediaimageUrl_HD as AnyObject?
                item1[title] = mediaItem.mediaTitle as AnyObject?
                item1[albumName] = "" as AnyObject?
                item1[albumArt] = "" as AnyObject?
                list.append(item1)
            }
            MediaShareController.sharedInstance.photoplayer?.addToList(list)
            
        }
        else if page == 1
        {
            MediaShareController.sharedInstance.deviceMediaCollection = (audioView?.deviceMediaCollection)!
            for mediaItem in MediaShareController.sharedInstance.deviceMediaCollection
            {
                item1[uri] = mediaItem.mediaUrl as AnyObject?
                item1[title] = mediaItem.mediaTitle as AnyObject?
                item1[albumName] = mediaItem.mediaAlbumName as AnyObject?
                item1[albumArt] = mediaItem.mediaimageUrl as AnyObject?
                list.append(item1)
            }
            MediaShareController.sharedInstance.audioplayer?.addToList(list)
        }
        else if page == 2
        {
            MediaShareController.sharedInstance.deviceMediaCollection = (videoView?.deviceMediaCollection)!
            for mediaItem in MediaShareController.sharedInstance.deviceMediaCollection
            {
                item1[uri] = mediaItem.mediaUrl as AnyObject?
                item1[title] = mediaItem.mediaTitle as AnyObject?
                item1[albumName] = mediaItem.mediaAlbumName as AnyObject?
                item1[thumbnailUrl] = mediaItem.mediaimageUrl as AnyObject?
                list.append(item1)
            }
            MediaShareController.sharedInstance.videoplayer?.addToList(list)
        }
        self.runafterDelay()
    }
    
    fileprivate func  runafterDelay()
    {
        let time = DispatchTime.now() + Double(Int64(2.0 * Double(NSEC_PER_SEC))) / Double(NSEC_PER_SEC)
        DispatchQueue.main.asyncAfter(deadline: time) { () -> Void in
            self.getTVQueue()
        }
    }
    
    func getTVQueue()
    {
        if MediaShareController.sharedInstance.playType == "photo"
        {
            MediaShareController.sharedInstance.photoplayer?.getList()
        }
        else if MediaShareController.sharedInstance.playType == "audio"
        {
            MediaShareController.sharedInstance.audioplayer?.getList()
        }
        else if MediaShareController.sharedInstance.playType == "video"
        {
            MediaShareController.sharedInstance.videoplayer?.getList()
        }
    }
    @IBAction func showTVqueue(_ sender: AnyObject)
    {
        self.getTVQueue()
        DispatchQueue.main.async {
            
        if self.bTVlistVisible == false
        {
            self.bTVlistVisible = true
            self.scrollView.isHidden = true
            self.lineView.isHidden = true
            self.tvListView?.updateMediaCollection()
            self.firstViewController.addSubview(self.tvListView!.view)
        }
        else
        {
            self.bTVlistVisible = false
            self.scrollView.isHidden = false
            self.lineView.isHidden = false
            self.tvListView?.view.removeFromSuperview()
        }
      }
        
    }
    
    func showButton()
    {
        self.getTVQueue()
        
        DispatchQueue.main.async {
            
        self.tvListButton.isHidden = false
        self.tvListButton.isEnabled = true
        if  (MediaShareController.sharedInstance.currentPage == 0 && MediaShareController.sharedInstance.playType == "photo") ||
            (MediaShareController.sharedInstance.currentPage == 1 && MediaShareController.sharedInstance.playType == "audio") ||
            (MediaShareController.sharedInstance.currentPage == 2 && MediaShareController.sharedInstance.playType == "video")
            {
              self.addAllMediaButton.isHidden = false
              self.addAllMediaButton.isEnabled = true
            }
         }
    }
    func hideButton()
    {
        
        DispatchQueue.main.async {
            
        self.tvListButton.isHidden = true
        self.tvListButton.isEnabled = false
    
        self.addAllMediaButton.isHidden = true
        self.addAllMediaButton.isEnabled = false
        if self.bTVlistVisible == true
        {
            self.bTVlistVisible = false
            NotificationCenter.default.post(name: Notification.Name(rawValue: "isTVListViewVisible"), object: self, userInfo: ["bTVlistVisible": self.bTVlistVisible])
            self.scrollView.isHidden = false
            self.lineView.isHidden = false
            self.tvListView?.view.removeFromSuperview()
        }
      }
        
    }
    
    func scrollViewWillBeginDragging(_ scrollView: UIScrollView)
    {
        
    }
    
    func scrollViewWillEndDragging(_ scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>)
    {
        
    }
    
    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool)
    {
        
    }
    
    func scrollViewDidScroll(_ scrollView: UIScrollView)
    {
        DispatchQueue.main.async {
            
        self.lineView.frame.origin.x = scrollView.contentOffset.x/3
        self.lineView.frame.size.width = scrollView.frame.width/3
            
        }
    }
    
    func scrollViewWillBeginDecelerating(_ scrollView: UIScrollView)
    {
        
    }
   
    func scrollViewDidEndDecelerating(_ scrollView: UIScrollView)
    {
        DispatchQueue.main.async {
        let page = (scrollView.contentOffset.x) / (scrollView.frame.size.width)
        if MediaShareController.sharedInstance.playType != nil
        {
            self.addAllMediaButton.isEnabled = true
            self.addAllMediaButton.isHidden = false
        }       
         if page == 0
         {
            self.photoButton.setTitleColor(UIColor.black, for:UIControlState())
            self.videoButton.setTitleColor(UIColor.darkGray, for:UIControlState())
            self.audioButton.setTitleColor(UIColor.darkGray, for:UIControlState())
            
            MediaShareController.sharedInstance.currentPage = 0
            if MediaShareController.sharedInstance.playType != "photo"
            {
                self.addAllMediaButton.isEnabled = false
                self.addAllMediaButton.isHidden = true
            }
         }
        else if page == 1
        {
            self.audioButton.setTitleColor(UIColor.black, for:UIControlState())
            self.photoButton.setTitleColor(UIColor.darkGray, for:UIControlState())
            self.videoButton.setTitleColor(UIColor.darkGray, for:UIControlState())
         
            MediaShareController.sharedInstance.currentPage = 1
            if MediaShareController.sharedInstance.playType != "audio"
            {
                self.addAllMediaButton.isEnabled = false
                self.addAllMediaButton.isHidden = true

            }
        }
        else if page == 2
        {
            self.videoButton.setTitleColor(UIColor.black, for:UIControlState())
            self.photoButton.setTitleColor(UIColor.darkGray, for:UIControlState())
            self.audioButton.setTitleColor(UIColor.darkGray, for:UIControlState())
            
            MediaShareController.sharedInstance.currentPage = 2
            if MediaShareController.sharedInstance.playType != "video"
            {
                self.addAllMediaButton.isEnabled = false
                self.addAllMediaButton.isHidden = true

            }
        }
      
        }
    }
    
    
    @IBAction func photoBtnAction(_ sender: AnyObject)
    {
        MediaShareController.sharedInstance.currentPage = 0
        
        DispatchQueue.main.async {
            
        self.photoButton.setTitleColor(UIColor.black, for:UIControlState())
        self.videoButton.setTitleColor(UIColor.darkGray, for:UIControlState())
        self.audioButton.setTitleColor(UIColor.darkGray, for:UIControlState())
        self.scrollView.setContentOffset(CGPoint(x: 0, y: 0), animated: true)
            
        }
    }
    
    @IBAction func audioBtnAction(_ sender: AnyObject)
    {
        MediaShareController.sharedInstance.currentPage = 1
        DispatchQueue.main.async {
            
        self.audioButton.setTitleColor(UIColor.black, for:UIControlState())
        self.photoButton.setTitleColor(UIColor.darkGray, for:UIControlState())
        self.videoButton.setTitleColor(UIColor.darkGray, for:UIControlState())
        self.scrollView.setContentOffset(CGPoint(x: self.view.bounds.size.width, y: 0), animated: true)
            
        }
    }
    
    @IBAction func videoBtnAction(_ sender: AnyObject)
    {
        MediaShareController.sharedInstance.currentPage = 2
        DispatchQueue.main.async {

        self.videoButton.setTitleColor(UIColor.black, for:UIControlState())
        self.photoButton.setTitleColor(UIColor.darkGray, for:UIControlState())
        self.audioButton.setTitleColor(UIColor.darkGray, for:UIControlState())
        self.scrollView.setContentOffset(CGPoint(x: self.view.bounds.size.width*2, y: 0), animated: true)
            
        }
    }

}

