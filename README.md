# Simulated Dryer Test Tools

This repo contains tools to create a controller for a dryer that can be run using the smartthings **Classis** app.
And, it includes sample openHAB config files for a dryer.

This directory consists of:
1. The Simulated Dryer device handler
2. A set of openHAB config files (.things, .items and .sitemap) for testing the Smartthings dryer cpabilities

## Simulated Dryer Device Handler

This is a Smartthings "Device Handler" that can be used to control a "Virtual" Simulated dryer. This is useful for testing the openHAB Dryer components.

To use this you will have to first create a virtual Device named simulated dryer.


### Installation
1. Locate SimulatedDryer.groovy file.
2. Open it in an editor (Some program you can use to copy the contents to the clipboard)
3. Copy the contents to the clipboard
4. Using the Smartthings developers tools:
5. Select **My Device Handlers** 
6. Click on the **+ Create New Device Handler** near the top right
7. Click on the **From Code** tab
8. Paste the contents of the clipboard
9. Click on the **Create** button near the bottom left
10. Click on **Publish -> For Me**
11. The Device Handler is now ready

## openHAB config files

These three files are a set of openHAB config files for testing the dryer.

### Installation
Copy each of the files to the appropriate directory on your openHAB server.

You will need to make the following changes to the **DryerTest.things** file
1. Update the **smartthingsIp** to match the IP of your smartthings hub
2. Update the **smartthingsName** to match your dryer Device 