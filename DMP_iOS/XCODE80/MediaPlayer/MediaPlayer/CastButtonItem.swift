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
import UIKit

enum CastStatus: String {
    case notReady = "notReady"
    case readyToConnect = "readyToConnect"
    case connecting = "connecting"
    case connected = "connected"
}

class CastButtonItem : NSObject{

    var castButton : UIButton!
    var castStatus: CastStatus = CastStatus.notReady {
        didSet {
            if castButton.imageView!.isAnimating {
                castButton.imageView!.stopAnimating()
            }
            switch castStatus {
            case .notReady:
                let castImage = UIImage(named: "cast_off.png")?.withRenderingMode(.alwaysTemplate)
                castButton.setImage(castImage, for: UIControlState())
                castButton.tintColor = UIColor.black
                castButton.isEnabled = false
            case .readyToConnect:
                let castImage = UIImage(named: "cast_off.png")?.withRenderingMode(.alwaysTemplate)
                castButton.setImage(castImage, for: UIControlState())
                castButton.tintColor = UIColor.black
                castButton.isEnabled = true
            case .connecting:
                castButton.imageView!.animationImages = [UIImage(named: "cast_on0.png")!.withRenderingMode(.alwaysTemplate) ,UIImage(named: "cast_on1.png")!.withRenderingMode(.alwaysTemplate), UIImage(named: "cast_on2.png")!.withRenderingMode(.alwaysTemplate), UIImage(named: "cast_on1.png")!.withRenderingMode(.alwaysTemplate)]
                castButton.imageView!.animationDuration = 2
                castButton.imageView!.startAnimating()
                castButton.tintColor = UIColor.black
            case .connected:
                if castButton.imageView!.isAnimating {
                   castButton.imageView!.stopAnimating()
                }
                let castImage = UIImage(named: "cast_on.png")!.withRenderingMode(.alwaysTemplate)
                castButton.setImage(castImage, for: UIControlState())
                castButton.tintColor = UIColor.blue
                castButton.isEnabled = true
            }
        }
        
    }

    func statusDidChange(_ notification: Notification!) {
        let status = notification.userInfo?["status"] as! NSString
        self.castStatus = CastStatus(rawValue: status as String)!
    }
    
    override init() {
        
    }
       init(devicesButton: UIButton) {
        super.init()
        NotificationCenter.default.addObserver(self, selector: #selector(CastButtonItem.statusDidChange(_:)), name: NSNotification.Name(rawValue: "CastStatusDidChange"), object: nil)
        self.castButton = devicesButton
        let castImage = UIImage(named: "cast_off.png")?.withRenderingMode(.alwaysTemplate)
        castButton.setImage(castImage, for: UIControlState())
        castButton.tintColor = UIColor.black
        castButton.isEnabled = false
    }

    deinit {
        NotificationCenter.default.removeObserver(self)
    }

}
