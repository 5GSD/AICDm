### AICD ###

The *Next Generation* **Android IMSI-Catcher Detector** (AICD).

This is the AICD source-code repository, for the forward development branch of [AIMSICDL](https://github.com/5GSD/AIMSICDL). 
*AICD* is an extremely light weight 
re-implementation of the original [AIMSICD](https://github.com/CellularPrivacy/Android-IMSI-Catcher-Detector/) design. It's code base is a complete
re-write from the orginal, focusing on a minimalistic UI, functionality, ease of use 
and perhaps most importantly, portability/flexibility due to its modular design. 

AICD collects and analyzes mobile radio data to make you aware of your mobile network 
security and to warn you about threats like fake base stations and IMSI-catchers, that 
are now commonly used by both criminals and various over-reaching government agencies.
These are used for a range of purposes, from simple user-tracking (location and use) 
to various sophisticated network attacks such as phone interception, and direct device 
remote control, using over-the-air (OTA) updates and various [Stagefright]()-like RCE 
vulneabilities. 

* Status:     **WIP**
* Date:       `2017-06-15`

---

### License ###


   * Copyright (C) 2017       GPLv3  5GSD

   This program is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3 of the License, or
   (at your option) any later version. See [COPYING](https://github.com/5GSD/AICDm/blob/master/COPYING) for details.



### Resources ###

Useful GitHub [Markdown Cheatsheet](https://github.com/adam-p/markdown-here/wiki/Markdown-Cheatsheet)

TBA


#### THIS work: ####

* Project Website:       https://github.com/5GSD/AICDm/
* Public Git repository: https://github.com/5GSD/AICDm.git
* Mailing list:          [TBA]
* Email:                 emigenix@gmail.com
* PGP:                   950B 7745 565A 4A48 1D66  D699 4923 6E35 27D8 F4E6


For all technical questions concerning the detection mechnaism and its 
detailed functionality, please refer to our Wiki and FAQ pages.
For development, maintenance and other security affairs, please 
email: emigenix@gmail.com.

---

### How to build and install AICD ###

 1. ~~[Installation of Android Studio](https://github.com/SnoopSnitch/xLite/blob/master/INSTALL_AS.md)~~
 2. ~~[How to compile parser dependencies](https://github.com/SnoopSnitch/xLite/blob/master/COMPILE.md)~~
 3. Building and Signing the app (WIP)


### Building from source ###

Please please consult the Android documentation on how to set up the tools and
perform a release build.

[1]: https://developer.android.com/sdk/ 
[2]: https://developer.android.com/tools/sdk/ndk/ 

### app permissions ###

The following permissions are required to run SnoopSnitch:

[NEED UPDATE!]

   * `ACCESS_SUPERUSER`:       Open Qualcomm diagnosis interface to capture radio data
   * `READ_PHONE_STATE,` 
   * `RECEIVE_SMS`:            Generate mobile network traffic recorded in active tests
   * `GET_TASKS`:              Retrieve state of helper processes interacting with diagnosis interface
   * `WAKE_LOCK`:              Acquire CPU for long-running analysis steps
   * `ACCESS_FINE_LOCATION,` 
   * `ACCESS_COARSE_LOCATION`: record location of IMSI catchers and security events if configured
   * `INTERNET`:               Download new data from gsmmap.org, upload radio traces and debug logs upon request
   * `ACCESS_NETWORK_STATE`:   Postpone uploads until network is available


In addition, the app require root privileges, which are only used to access 
the */dev/diag* interface from which the baseband netwrok information is read.

---

### Known Bugs ###

For the most recent list of bugs, please refer to the currently [open GitHb issues](https://github.com/5GSD/AICDm/issues).

For technical bugs, and limitations please refer to the GitHub Wiki article "Bugs and Limitations".

----

EOF
