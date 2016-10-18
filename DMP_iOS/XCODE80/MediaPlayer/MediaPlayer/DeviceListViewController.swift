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

class DeviceListViewController: UITableViewController {
    var didFindServiceObserver: AnyObject? = nil

    var didRemoveServiceObserver: AnyObject? = nil

    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.register(UITableViewCell.self, forCellReuseIdentifier: "DeviceCell")
    }

    override func viewWillAppear(_ animated: Bool) {
        didFindServiceObserver =  MediaShareController.sharedInstance.search.on(MSDidFindService) { [unowned self] notification in
            self.tableView.reloadData()
        }
        didRemoveServiceObserver = MediaShareController.sharedInstance.search.on(MSDidRemoveService) {[unowned self] notification in
            self.tableView.reloadData()
        }
    }

    override func viewWillDisappear(_ animated: Bool) {
        MediaShareController.sharedInstance.search.off(didFindServiceObserver!)
        MediaShareController.sharedInstance.search.off(didRemoveServiceObserver!)
    }

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if MediaShareController.sharedInstance.search.isSearching {
            return MediaShareController.sharedInstance.services.count
        } else {
            return 1
        }
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "DeviceCell", for: indexPath) as UITableViewCell
        cell.textLabel!.text = MediaShareController.sharedInstance.services[indexPath.row].name
        return cell
    }

    override func tableView(_ tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
        return "Connect to TV"
    }

    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if MediaShareController.sharedInstance.search.isSearching {
            let service = MediaShareController.sharedInstance.services[indexPath.row] as Service
            MediaShareController.sharedInstance.selectedService = service
           
            MediaShareController.sharedInstance.connect(service)
        }
        dismiss(animated: true) { }
    }
}
