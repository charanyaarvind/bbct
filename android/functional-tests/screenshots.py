# This file is part of BBCT for Android.
#
# Copyright 2012 codeguru <codeguru@users.sourceforge.net>
#
# BBCT for Android is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# BBCT for Android is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
from optparse import OptionParser
import util

import os
import sys

defaultPackage = 'bbct.android.common'
defaultDbFileName = 'data/bbct.db'
defaultDelay = 30.0
defaultScreenshotDir='screenshots'

parser = OptionParser(usage='Usage: %prog [options] <APK file> <list activity>')
parser.add_option('-k', '--package', dest='package', help='Android package name', default=defaultPackage)
parser.add_option('-f', '--dbfile', dest='dbfile', help='Path to local database file', default=defaultDbFileName)
parser.add_option('-d', '--delay', dest='delay', type='float', help='delay before taking a screenshot', default=defaultDelay)
parser.add_option('-s', '--screenshots', dest='screenshotDir', help='destination directory for screenshots', default=defaultScreenshotDir)

try:
    options, (apkFile, activity) = parser.parse_args()
except ValueError:
    parser.print_help()
    sys.exit(1)

package = options.package
dbFileName = "bbct.db"
localDb = options.dbfile
remoteDb = "/data/data/" + package + "/databases/" + dbFileName
screenshotDir = options.screenshotDir

runComponent = package + '/' + activity

# Amount of time to sleep in order to allow the Android emulator to finish
# a task before taking a screenshot. This is necessary for my slow-ass computer
delay = options.delay
scrnCnt = 0

# Create screenshot directory
if not os.path.exists(screenshotDir):
    os.makedirs(screenshotDir)

print("Connecting to device...")
device = MonkeyRunner.waitForConnection()

print("Installing APK: " + apkFile + "...")
if device.installPackage(apkFile):
    print("Reading baseball card data...")
    cards = util.read_card_data('data/cards.csv')

    print("Starting activity: " + runComponent + "...")
    device.startActivity(component=runComponent)
    MonkeyRunner.sleep(delay)
    util.take_screenshot(device, screenshotDir, delay)

    print("Menu...")
    device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP)
    util.take_screenshot(device, screenshotDir, delay)

    print("Add card...")
    device.press('KEYCODE_DPAD_CENTER', MonkeyDevice.DOWN_AND_UP)
    util.take_screenshot(device, screenshotDir, delay)

    print("Enter data...")
    util.input_card(device, cards[0])
    util.take_screenshot(device, screenshotDir, delay)

    print("Removing package: " + package + "...")
    device.removePackage(package)

print("Push database...")
print("Install APK: " + apkFile + "...")
if device.installPackage(apkFile):
    print("Push database to device...")
    os.system("adb push " + localDb + " " + remoteDb)

    print("Starting activity: " + runComponent + "...")
    device.startActivity(component=runComponent)
    MonkeyRunner.sleep(delay)
    util.take_screenshot(device, screenshotDir, delay)

    print("Filter Cards...")
    device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(delay)
    device.press('KEYCODE_DPAD_DOWN', MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(delay)
    device.press('KEYCODE_DPAD_CENTER', MonkeyDevice.DOWN_AND_UP)
    util.take_screenshot(device, screenshotDir, delay)

    print("Filter By Year...")
    device.press('KEYCODE_DPAD_CENTER', MonkeyDevice.DOWN_AND_UP)
    device.type("1993")
    util.take_screenshot(device, screenshotDir, delay)

    print("Apply filter...")
    device.press('KEYCODE_DPAD_DOWN')
    MonkeyRunner.sleep(delay)
    device.press('KEYCODE_DPAD_CENTER')
    util.take_screenshot(device, screenshotDir, delay)

    print("Clear filter...")
    device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(delay)
    device.press('KEYCODE_DPAD_CENTER', MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(delay)

    print("Select cards...")
    x = 20
    y = 165
    device.touch(x, y, MonkeyDevice.DOWN_AND_UP)
    device.touch(x, y + 80, MonkeyDevice.DOWN_AND_UP)
    util.take_screenshot(device, screenshotDir, delay)

    print("About...")
    device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP)
    MonkeyRunner.sleep(delay)
    for i in range(3):
        device.press('KEYCODE_DPAD_DOWN', MonkeyDevice.DOWN_AND_UP)
        MonkeyRunner.sleep(delay)
    device.press('KEYCODE_DPAD_CENTER', MonkeyDevice.DOWN_AND_UP)
    util.take_screenshot(device, screenshotDir, delay)

    print("Removing package: " + package + "...")
    device.removePackage(package)
