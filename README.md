# PCO Kiosk #

The PCO Kiosk is a simple application to display upcoming assignments from Planning Center Online. It can easily be run on small devices (such as a Raspberry Pi) to create digital signage allowing people to see who is scheduled in the near future.

![Example kiosk](https://bitbucket.org/repo/xrjqyX/images/691354340-final.jpg)


**Features**

* Display three weeks of upcoming schedules for multiple services
* Needed positions are highlighted
* Colors invert periodically to prevent burn-in 
    * ...except without 30 minutes of service hours, to keep it pretty


**Building**

To build from source, simply run `./gradlew` on Mac/Linux or `gradlew.bat` on Windows.


**More Info**

For more instructions, including settings for setting up a Raspberry Pi kiosk, see [the wiki](https://bitbucket.org/robeden/pco-kiosk/wiki/).