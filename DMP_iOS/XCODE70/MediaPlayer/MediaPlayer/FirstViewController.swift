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
        
        let tvListButtonImage = UIImage(named: "queue_deactive.png")?.imageWithRenderingMode(.AlwaysTemplate)
        tvListButton.setImage(tvListButtonImage, forState: UIControlState.Normal)
        tvListButton.tintColor = UIColor.orangeColor()
        
        let addAllMediaButtonImage = UIImage(named: "add_all.png")?.imageWithRenderingMode(.AlwaysTemplate)
        addAllMediaButton.setImage(addAllMediaButtonImage, forState: UIControlState.Normal)
        addAllMediaButton.tintColor = UIColor.orangeColor()
        
        tvListButton.hidden = true
        tvListButton.enabled = false
        addAllMediaButton.hidden = true
        addAllMediaButton.enabled = false
       
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "showTvQueue", name: "TvlistRecieved", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "showButton", name: "onPlay", object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "hideButton", name: "onDisconnect", object: nil)

        castItem = CastButtonItem(devicesButton: devicelistButton)
        
        let label = UILabel(frame: CGRect(x: 0, y: 0, width: (self.navigationController?.navigationBar.frame.size.width)!, height: (self.navigationController?.navigationBar.frame.size.height)!))
        label.text = "DMP 2.0"
        label.textAlignment = NSTextAlignment.Left
        
        self.navigationItem.titleView = label
        self.navigationController?.navigationBar.tintColor = UIColor.blackColor()
        
        navigationItem.titleView?.tintColor = UIColor.blackColor()
        castItem!.castButton.addTarget(self, action: Selector("cast"), forControlEvents: UIControlEvents.TouchUpInside)
        castItem!.castStatus = MediaShareController.sharedInstance.getCastStatus()
        
        automaticallyAdjustsScrollViewInsets = true
        
        scrollView.frame = CGRect(x:0, y:125, width:view.bounds.size.width, height: view.bounds.size.height - 150) // height: scrollView.bounds.height
        scrollView.backgroundColor = UIColor.whiteColor()
        scrollView.contentSize = CGSizeMake((view.bounds.size.width)*3, scrollView.bounds.height)
        scrollView.autoresizingMask = UIViewAutoresizing.FlexibleWidth
        
        mediaPlayView = sb.instantiateViewControllerWithIdentifier("mediaPlayView") as? MediaPlayViewController
        mediaPlayView?.view.frame = CGRect(x:0, y:scrollView.bounds.height, width: self.view.bounds.size.width, height: 150)
        mediaPlayView?.view.backgroundColor = UIColor.whiteColor()
        
        photoView = sb.instantiateViewControllerWithIdentifier("photoView") as? PhotoCollectionView
        photoView?.view.frame = CGRect(x:0, y:0, width: self.view.bounds.size.width, height: scrollView.bounds.height - mediaPlayView!.view.frame.height)
        photoView?.view.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.035)
        scrollView.addSubview(photoView!.view)

        audioView = sb.instantiateViewControllerWithIdentifier("audioView") as? AudioViewController
        audioView?.view.frame = CGRect(x:(self.view.bounds.size.width), y:0, width: self.view.bounds.size.width, height: scrollView.bounds.height - mediaPlayView!.view.frame.height)
        audioView?.view.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.035)
        scrollView.addSubview(audioView!.view)

        videoView = sb.instantiateViewControllerWithIdentifier("videoView") as? VideoViewController
        videoView?.view.frame = CGRect(x:(self.view.bounds.size.width)*2, y:0, width: self.view.bounds.size.width, height: scrollView.bounds.height - mediaPlayView!.view.frame.height)
        videoView?.view.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: 0.035)
        scrollView.addSubview(videoView!.view)
        
        self.view.addSubview(mediaPlayView!.view)
        scrollView.delegate = self
        self.view.backgroundColor = UIColor.orangeColor()
    }
    
    override func viewWillAppear(animated: Bool)
    {
       // title = "Default Media Player"
    }
    
    override func viewWillDisappear(animated: Bool)
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
        audioView?.view.frame = CGRect(x:(self.view.bounds.size.width), y:0, width: self.view.bounds.size.width, height: scrollView.bounds.height)

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
            let deviceList = DeviceListViewController(style: UITableViewStyle.Plain)
            presentPopover(deviceList)
        }
    }

    func presentPopover(viewController: UIViewController)
    {
        viewController.preferredContentSize = CGSize(width: 320, height: 186)
        viewController.modalPresentationStyle = UIModalPresentationStyle.Popover
        let presentationController = viewController.popoverPresentationController
        presentationController!.sourceView = castItem!.castButton
        presentationController!.sourceRect = castItem!.castButton.bounds
        viewController.popoverPresentationController!.delegate = self
        presentViewController(viewController, animated: false, completion: {})
    }
    
    func adaptivePresentationStyleForPresentationController(controller: UIPresentationController) -> UIModalPresentationStyle
    {
        // Return no adaptive presentation style, use default presentation behaviour
        return .None
    }
    
    @IBAction func addAllMediaToTVqueue(sender: AnyObject)
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
                item1[uri] = mediaItem.mediaimageUrl
                item1[title] = mediaItem.mediaTitle
                item1[albumName] = ""
                item1[albumArt] = ""
                list.append(item1)
            }
            MediaShareController.sharedInstance.photoplayer?.addToList(list)
            
        }
        else if page == 1
        {
            MediaShareController.sharedInstance.deviceMediaCollection = (audioView?.deviceMediaCollection)!
            for mediaItem in MediaShareController.sharedInstance.deviceMediaCollection
            {
                item1[uri] = mediaItem.mediaUrl
                item1[title] = mediaItem.mediaTitle
                item1[albumName] = mediaItem.mediaAlbumName
                item1[albumArt] = mediaItem.mediaimageUrl
                list.append(item1)
            }
            MediaShareController.sharedInstance.audioplayer?.addToList(list)
        }
        else if page == 2
        {
            MediaShareController.sharedInstance.deviceMediaCollection = (videoView?.deviceMediaCollection)!
            for mediaItem in MediaShareController.sharedInstance.deviceMediaCollection
            {
                item1[uri] = mediaItem.mediaUrl
                item1[title] = mediaItem.mediaTitle
                item1[albumName] = mediaItem.mediaAlbumName
                item1[thumbnailUrl] = mediaItem.mediaimageUrl
                list.append(item1)
            }
            MediaShareController.sharedInstance.videoplayer?.addToList(list)
        }
        self.runafterDelay()
    }
    
    private func  runafterDelay()
    {
        let time = dispatch_time(DISPATCH_TIME_NOW,Int64(2.0 * Double(NSEC_PER_SEC)))
        dispatch_after(time, dispatch_get_main_queue()) { () -> Void in
            
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
    }
    
    @IBAction func getTVqueue(sender: AnyObject)
    {
        if bTVlistVisible == false
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
          
            bTVlistVisible = true
            scrollView.hidden = true
            lineView.hidden = true
            tvListView = sb.instantiateViewControllerWithIdentifier("Mediaview") as? MediaListController
            tvListView?.view.backgroundColor = UIColor.whiteColor()
            self.tvListView!.view.frame = CGRectMake(0, (self.navigationController?.navigationBar.frame.size.height)!+20, self.view.bounds.size.width,  self.view.frame.size.height -  (self.navigationController?.navigationBar.frame.size.height)! - 20 )
            firstViewController.addSubview(tvListView!.view)
          
        }
        else
        {
            bTVlistVisible = false
            scrollView.hidden = false
            lineView.hidden = false
            tvListView?.view.removeFromSuperview()
        }
        
    }
    
    func showButton()
    {
        self.tvListButton.hidden = false
        self.tvListButton.enabled = true
       if  (MediaShareController.sharedInstance.currentPage == 0 && MediaShareController.sharedInstance.playType == "photo") ||
           (MediaShareController.sharedInstance.currentPage == 1 && MediaShareController.sharedInstance.playType == "audio") ||
        (MediaShareController.sharedInstance.currentPage == 2 && MediaShareController.sharedInstance.playType == "video")
       {
           self.addAllMediaButton.hidden = false
           self.addAllMediaButton.enabled = true
       }
        
    }
    func hideButton()
    {
        self.tvListButton.hidden = true
        self.tvListButton.enabled = false
    
        self.addAllMediaButton.hidden = true
        self.addAllMediaButton.enabled = false
        if bTVlistVisible == true
        {
            bTVlistVisible = false
            scrollView.hidden = false
            lineView.hidden = false
            tvListView?.view.removeFromSuperview()
        }
        
    }
     func showTvQueue()
     {
       tvListView?.mediaCollection.reloadData()
     }
    func scrollViewWillBeginDragging(scrollView: UIScrollView)
    {
        
    }
    
    func scrollViewWillEndDragging(scrollView: UIScrollView, withVelocity velocity: CGPoint, targetContentOffset: UnsafeMutablePointer<CGPoint>)
    {
        
    }
    
    func scrollViewDidEndDragging(scrollView: UIScrollView, willDecelerate decelerate: Bool)
    {
        
    }
    
    func scrollViewDidScroll(scrollView: UIScrollView)
    {
        lineView.frame.origin.x = scrollView.contentOffset.x/3
        lineView.frame.size.width = scrollView.frame.width/3
    }
    
    func scrollViewWillBeginDecelerating(scrollView: UIScrollView)
    {
        
    }
   
    func scrollViewDidEndDecelerating(scrollView: UIScrollView)
    {
        let page = (scrollView.contentOffset.x) / (scrollView.frame.size.width)
        if MediaShareController.sharedInstance.playType != nil
        {
            self.addAllMediaButton.enabled = true
            self.addAllMediaButton.hidden = false
        }       
         if page == 0
         {
            photoButton.setTitleColor(UIColor.blackColor(), forState:UIControlState.Normal)
            videoButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
            audioButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
            
            MediaShareController.sharedInstance.currentPage = 0
            if MediaShareController.sharedInstance.playType != "photo"
            {
                self.addAllMediaButton.enabled = false
                self.addAllMediaButton.hidden = true
            }
         }
        else if page == 1
        {
            audioButton.setTitleColor(UIColor.blackColor(), forState:UIControlState.Normal)
            photoButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
            videoButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
         
            MediaShareController.sharedInstance.currentPage = 1
            if MediaShareController.sharedInstance.playType != "audio"
            {
                self.addAllMediaButton.enabled = false
                self.addAllMediaButton.hidden = true

            }
        }
        else if page == 2
        {
            videoButton.setTitleColor(UIColor.blackColor(), forState:UIControlState.Normal)
            photoButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
            audioButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
            
            MediaShareController.sharedInstance.currentPage = 2
            if MediaShareController.sharedInstance.playType != "video"
            {
                self.addAllMediaButton.enabled = false
                self.addAllMediaButton.hidden = true

            }
        }
    }
    
    
    @IBAction func photoBtnAction(sender: AnyObject)
    {
        photoButton.setTitleColor(UIColor.blackColor(), forState:UIControlState.Normal)
        videoButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
        audioButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
        
        MediaShareController.sharedInstance.currentPage = 0
        scrollView.setContentOffset(CGPoint(x: 0, y: 0), animated: true)
        
    }
    
    @IBAction func audioBtnAction(sender: AnyObject)
    {
        audioButton.setTitleColor(UIColor.blackColor(), forState:UIControlState.Normal)
        photoButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
        videoButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
        
        MediaShareController.sharedInstance.currentPage = 1
        scrollView.setContentOffset(CGPoint(x: self.view.bounds.size.width, y: 0), animated: true)
    }
    
    @IBAction func videoBtnAction(sender: AnyObject)
    {
        videoButton.setTitleColor(UIColor.blackColor(), forState:UIControlState.Normal)
        photoButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
        audioButton.setTitleColor(UIColor.darkGrayColor(), forState:UIControlState.Normal)
        
        MediaShareController.sharedInstance.currentPage = 2
        scrollView.setContentOffset(CGPoint(x: self.view.bounds.size.width*2, y: 0), animated: true)
    }

}

