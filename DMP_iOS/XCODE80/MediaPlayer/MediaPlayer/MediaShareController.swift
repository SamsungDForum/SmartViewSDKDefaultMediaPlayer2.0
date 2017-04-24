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

import Foundation
import AssetsLibrary
import SmartView

struct Settings {
    
    var audioURL:String
    var disconnectKeepPlaying:Bool
    var showStandbyDev:Bool
}

private let backgroundMusic = "https://www.samsungdforum.com/smartview/sample/audio/Beverly_-_01_-_You_Said_It.mp3"

class MediaShareController: NSObject, ServiceSearchDelegate ,ChannelDelegate ,ConnectionDelegate{
    
  
    var videoplayer: VideoPlayer? = nil
    var audioplayer: AudioPlayer? = nil
    var photoplayer: PhotoPlayer? = nil
    
    var videoPlaycontroller: VideoPlayerController? = nil
    var audioPlaycontroller: AudioPlayerController? = nil
    var photoPlaycontroller: PhotoPlayerController? = nil
    
    var  mediaImageUrl:URL? = nil
    var tvQueueMediaCollection = [Media]()
    var deviceMediaCollection = [Media]()
    var mediaType :String? = nil
    var playType :String? = nil
    
    /// The service discovery
    let search = Service.search()
    var appURL: String = "http://prod-multiscreen-examples.s3-website-us-west-1.amazonaws.com/examples/photoshare/tv/"
    var channelId: String = "samsung.default.media.player"
    var isConnecting: Bool = false
    var isConnected: Bool = false
    var isStatusConnected = false
    var isPlayerConnected: Bool = false
    
    var services = [Service]()
    var selectedService: Service?
    var connectStatus:Bool = false
    
    var totalDurationOfVideo = 0
    var currentPage:Int = 0
    var mediaStyleDict = [String:String] ()
    var currentElement:String?
    
    let appName:String = "DefaultMediaPlayer2.0.2"
    
    var settingsValue = Settings(audioURL: backgroundMusic, disconnectKeepPlaying: false, showStandbyDev: true)
    
    static let sharedInstance = MediaShareController()
    
    fileprivate override init ()
    {
        super.init()
        search.delegate = self
        
    }
    
    func searchServices() {
        search.start(settingsValue.showStandbyDev)
        updateCastStatus()
    }
    
    func connect(_ service: Service) {
        search.stop()
        
        photoplayer = service.createPhotoPlayer(appName)
        photoplayer?.connectionDelegate = self
    
        photoPlaycontroller = PhotoPlayerController()
        photoplayer?.playerDelegate = photoPlaycontroller
        
        
        audioplayer = service.createAudioPlayer(appName)
        audioplayer?.connectionDelegate = self
        
        audioPlaycontroller = AudioPlayerController()
        audioplayer?.playerDelegate = audioPlaycontroller

        
        videoplayer = service.createVideoPlayer(appName)
        videoplayer?.connectionDelegate = self
        
        videoPlaycontroller = VideoPlayerController()
        videoplayer?.playerDelegate = videoPlaycontroller

        isConnecting = false
        isConnected = true
        self.updateCastStatus()
        
    }
    

    func getCastStatus() -> CastStatus {
        var castStatus = CastStatus.notReady
        if isConnected {
            castStatus = CastStatus.connected
        } else if isConnecting {
            castStatus = CastStatus.connecting
        } else if services.count > 0 {
            castStatus = CastStatus.readyToConnect
        }
        return castStatus
    }
    
    // Update the cast button status: Since they may be many cast buttons and
    // the MediaShareController does not need to be coupled to the view controllers
    // the use of Notifications seems appropriate.
    func updateCastStatus() {
  
         NotificationCenter.default.post(name: Notification.Name(rawValue: "CastStatusDidChange"), object: self, userInfo: ["status":getCastStatus().rawValue])
    }
    
    // MARK: - ChannelDelegate -
    
    func onConnect(_ error: NSError?)
    {
        if (error != nil) {
            search.start(settingsValue.showStandbyDev)
        }
        isConnecting = false
        isConnected = true
        isStatusConnected = true
        isPlayerConnected = true
        updateCastStatus()
    }
    
    func onDisconnect(_ error: NSError?)
    {
        if (isPlayerConnected)
        {
            NotificationCenter.default.post(name: Notification.Name(rawValue: "onDisconnect"), object: self, userInfo: nil)
            search.start(settingsValue.showStandbyDev)
            isConnecting = false
            isConnected = false
            isPlayerConnected = false
            updateCastStatus()
            if self.playType != nil
            {
                MediaShareController.sharedInstance.tvQueueMediaCollection.removeAll()
                NotificationCenter.default.post(name: Notification.Name(rawValue: "clearTvQueue"), object: self, userInfo: nil)
            }
            self.playType = nil
        }
        
    }
    
    func onClientDisconnect(_ client: ChannelClient) {
        print("onClientDisconnect")
    }
    
    func onClientConnect(_ client: ChannelClient) {
        print("onClientConnect")

    }
    
    func onReady() {
        print("onReady")
    }
    
    func onError(_ error: NSError) {
        
        print("onError")
    }
    
    @objc func onServiceFound(_ service: Service) {
        services.append(service)
        updateCastStatus()
    }
    
    @objc func onServiceLost(_ service: Service) {
        removeObject(service)
        updateCastStatus()
    }
    
    @objc func onStop() {
        services.removeAll(keepingCapacity: false)
    }
    
    func removeObject(_ service :Service) -> Void {
        if let found = services.index(of: service) {
             services.remove(at: found)
        }
        
    }

}
