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
import SmartView

class TerminateAppViewController: UIViewController {

    @IBOutlet
    var serviceInfo: UILabel? = nil

    override func viewDidAppear(_ animated: Bool) {
        if MediaShareController.sharedInstance.isConnected == true {
            serviceInfo?.text = (MediaShareController.sharedInstance.selectedService?.name)
        }
    }

    @IBAction
    func disconnect() {
        
        if MediaShareController.sharedInstance.settingsValue.showStandbyScreen
        {
            MediaShareController.sharedInstance.videoplayer?.removePlayerWatermark()
        }
        
        if MediaShareController.sharedInstance.playType == "photo"{
            MediaShareController.sharedInstance.photoplayer?.disconnect(!MediaShareController.sharedInstance.settingsValue.disconnectKeepPlaying)
        }
        else if MediaShareController.sharedInstance.playType == "audio"{
            MediaShareController.sharedInstance.audioplayer?.disconnect(!MediaShareController.sharedInstance.settingsValue.disconnectKeepPlaying)
        }
        else// MediaShareController.sharedInstance.playType == "video"{
        {
            MediaShareController.sharedInstance.videoplayer?.disconnect(!MediaShareController.sharedInstance.settingsValue.disconnectKeepPlaying)
        }
        
        MediaShareController.sharedInstance.updateCastStatus()
        MediaShareController.sharedInstance.isConnecting = false
        MediaShareController.sharedInstance.isConnected = false
        MediaShareController.sharedInstance.search.start()

        self.dismiss(animated: true) { }
    }

}
