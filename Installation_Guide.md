# Installation #

Flemil is a user interface library that you include as an external library to your project. You can also copy and paste its code into your project if you are not comfortable with linking projects and you want to operate with it from the source code. The current repository structure is an eclipse project. This means that you can check out the repository as a project in eclipse and add it as a dependency to your project.
You can also include the project as a jar file by downloading the jar file in the **Downloads** section. This jar file is not updated with every source commit. That is why it is recommended to always use the source since it contains the latest fixes.

## External Libraries ##
Flemil requires the following external libraries in order to function properly.
  * Blackberry specific jar file.
This file is included in the lib folder of the project. This is necessary to enable the custom handling of the BlackBerry devices menu key so that pressing this key brings up the Flemil window menu for the current window. This file is required for the project to build properly even if you are not going to be deploying your app in BlackBerry devices. This is because Flemil is designed to work on all devices for a single build. The same jar you build for one device is going to run on any other device.
  * MIDP 2.0 and CLDC 1.0 Compliant J2ME development environment
This is mostly provided by the Sun Wireless Toolkit. But you can resolve to use other environments provided they provide support for MIDP 2.0 and cldc 1.0.