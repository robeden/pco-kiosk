# PCO Kiosk #

The PCO Kiosk is a simple application to display upcoming assignments from Planning Center 
Online. It can easily be run on small devices (such as a Raspberry Pi) to create digital 
signage allowing people to see who is scheduled in the near future.

![Example kiosk](https://github.com/robeden/pco-kiosk/wiki/images/front2.jpg)

## Features ##

* Display three weeks of upcoming schedules for multiple services
* Needed positions are highlighted
* Colors invert periodically to prevent burn-in 
    * ...except within 30 minutes of service hours, to keep it pretty


## Building ##

To build from source, simply run `./gradlew` on Mac/Linux or `gradlew.bat` on Windows.

Note that in order to run you will need OAuth keys obtained from PCO. See the 
[API documentation](http://get.planningcenteronline.com/api) for more information.


## More Info ##

For more instructions, including how to setup a Raspberry Pi kiosk, see 
[the wiki](../../wiki/).


## Problems or Ideas ##

Admittedly, the code is currently very rigid to suit my current needs. Problem reports or 
feature suggestions are always welcome via 
[the issues page](https://github.com/robeden/pco-kiosk/issues), 
email (robeden1 at gmail dot com) or twitter (RobEden).