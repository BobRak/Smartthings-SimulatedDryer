/**
 *  Simulated Dryer Device Handler
 *  Bob Raker 1/28/2020
 *
 */
metadata {
	definition (name: "Simulated Dryer", namespace: "bobrak", author: "Bob Raker") {
		capability "Switch"
        capability "Dryer Mode"
        capability "Dryer Operating State"
		capability "Refresh"
        capability "Lock"
        capability "Actuator"
        capability "Sensor"
        
        // Dryer mode commands
        command "lowHeat"
        command "highHeat"
        command "regular"
        
        // machineState commands
        command "start"
        command "stop"
        command "pause"
        command "unpause"
        
        // Custom command
        command "reset"
        
        attribute "version", "number"
        attribute "machineRunTime", "number"
	}

	simulator {
	}

	// No icons because so few work anymore
	tiles (scale: 2) {
		standardTile("resetTileLabel", "", width: 4, height: 2) {
			state "val", label:"Click Reset to initialize"
		}
		standardTile("resetTile", "device.reset", width: 2, height: 2) {
			state "val", label:"Reset", action:"reset", backgroundColor:"#99A3A4"
		}

		standardTile("onOffTileLabel", "", width: 4, height: 2) {
			state "off", label: "Click Off to turn on"
			state "on", label: "Click On to turn off"
		}
		standardTile("switch", "device.switch", width: 2, height: 2, canChangeIcon: true) {
			state "off", label: "Off", action: "switch.on", backgroundColor: "#ffffff"
			state "on", label: "On", action: "switch.off", backgroundColor: "#00a0dc"
		}

		standardTile("machineState", "device.machineState", width: 2, height: 2) {
			state("stop", label:"Start", action:"start", backgroundColor:"#ff4d4d")
			state("run", label:"Stop", action:"stop", backgroundColor:"#00e64d")
			state("none", label:"", backgroundColor:"#cccccc")
			state("pause", label:"Paused", backgroundColor:"#adadad")
		}
		standardTile("machineStatePause", "device.machineState", width: 2, height: 2) {
			state("pause", label:"Resume", action:"unpause", backgroundColor:"#e86d13")
			state("run", label:"Pause", action:"pause", backgroundColor:"#00A0DC")
			state("stop", label:"", backgroundColor:"#cccccc")
			state("none", label:"", backgroundColor:"#cccccc")
		} 
   		standardTile("mode", "device.dryerMode", width: 2, height: 2) {
			state "regular", label:'${name}', action:"lowHeat", backgroundColor:"#ff471a", nextState: "lowHeat"
			state "lowHeat", label:'${name}', action:"highHeat", backgroundColor:"#ffad99"
			state "highHeat", label:'${name}', action:"regular", backgroundColor:"#cc2900"
			state("none", label:"", backgroundColor:"#cccccc")
		}
		standardTile("jobState", "device.dryerJobState", width: 2, height: 2) {
			state "none", label:'Jobstate: ${currentValue}', defaultState: true
            state "cooling", label:'Jobstate: ${currentValue}'
            state "drying", label:'Jobstate: ${currentValue}'
            state "finished", label:'Jobstate: ${currentValue}'
            state "weight sensing", label:'Jobstate: ${currentValue}'
            state "wrinkle prevent", label:'Jobstate: ${currentValue}'
		}
        valueTile("machineStateDisplayTile", "device.machineState", width: 2, height: 2) {
        	state "val", label:'MachineState\n ${currentValue}', defaultState: true
        }
        valueTile("runTimeTile", "machineRunTime", width: 2, height: 2) {
        	state "val", label:'Runtime\n ${currentValue}', defaultState: true
        }
        valueTile("versionTile", "version", width: 2, height: 2) {
        	state "val", label:'Version\n ${currentValue}'
        }
        
		main "switch"
		details(["resetTileLabel", "resetTile", "onOffTileLabel", "switch", "machineState", "machineStatePause", "mode", "jobState", "runTimeTile", "machineStateDisplayTile", "versionTile"])
	}
}

def reset() {
	log.debug "reset()"
    state.runTime = 0
	sendEvent(name: "switch", value: "off")
    sendEvent(name: "machineState", value: "none")
	sendEvent(name: "pause", value: "none")
	sendEvent(name: "dryerJobState", value: "none")
	sendEvent(name: "dryerMode", value: "none")
	sendEvent(name: "machineRunTime", value: 0)
    sendEvent(name: "dryerJobState", value: "none")
    state.runTime = 0
	sendEvent(name: "machineRunTime", value: state.runTime)
	runEvery1Minute( updateRunTime )
    getVersion()

}

def on() {
	log.debug "on()"
	sendEvent(name: "switch", value: "on")
    sendEvent(name: "machineState", value: "stop")
    sendEvent(name: "pause", value: "none")
    sendEvent(name: "dryerMode", value: "none")
    sendEvent(name: "dryerJobState", value: "none")
    state.runTime = 0
	sendEvent(name: "machineRunTime", value: state.runTime)
    def curRunTime = device.currentValue("machineRunTime")
	log.debug "on(), current machineRunTime is ${curRunTime}"
    getVersion()
}

def off() {
	log.debug "off()"
	sendEvent(name: "switch", value: "off")
	sendEvent(name: "machineState", value: "none")
	sendEvent(name: "pause", value: "none")
    sendEvent(name: "dryerMode", value: "none")
    sendEvent(name: "dryerJobState", value: "none")
    state.runTime = 0
	sendEvent(name: "machineRunTime", value: state.runTime)
    getVersion()
}

def start() {
	log.debug "start()"
    // Not needed?? if( isOff() ) return
	sendEvent(name: "machineState", value: "run")
    sendEvent(name: "pause", value: "resume")
    sendEvent(name: "dryerMode", value: "regular")
    sendEvent(name: "dryerJobState", value: "drying")
    state.runTime = 0
	sendEvent(name: "machineRunTime", value: state.runTime)
}

def stop() {
	log.debug "stop()"
    if( isOff() ) return
	sendEvent(name: "machineState", value: "stop")
	sendEvent(name: "pause", value: "none")
	sendEvent(name: "dryerMode", value: "none")
	sendEvent(name: "dryerJobState", value: "none")
    state.runTime = 0
}

def pause() {
	log.debug "pause()"
    if( isOff() ) return
	sendEvent(name: "machineState", value: "pause")
}

def unpause() {
	log.debug "unpause()"
    // if( isOff() ) return
	sendEvent(name: "machineState", value: "run")
}

def regular() {
	log.debug "regular()"
    if( isOff() ) return
	sendEvent(name: "dryerMode", value: "regular")
}

def lowHeat() {
	log.debug "lowHeat()"
    if( isOff() ) return
	sendEvent(name: "dryerMode", value: "lowHeat")
}

def highHeat() {
	log.debug "highHeat()"
    if( isOff() ) return
	sendEvent(name: "dryerMode", value: "highHeat")
}

def parse(String description) {
	log.debug "description: $description"
}

def updateRunTime() {
	if( device.currentValue("machineState").equals("run")) {
        state.runTime = state.runTime + 1
        sendEvent(name: "machineRunTime", value: state.runTime)
    	log.debug "Runtime updated to ${state.runTime}"

        if( state.runTime >= 5 ) {
            sendEvent(name: "switch", value: "off")
            sendEvent(name: "machineState", value: "none")
            sendEvent(name: "pause", value: "none")
            sendEvent(name: "dryerMode", value: "none")
            sendEvent(name: "dryerJobState", value: "finished")
			sendEvent(name: "machineRunTime", value: state.runTime)
        }
    }
	else {
    	log.debug "Runtime not updated because machine isn't running"
    }
}

def isOff() {
	if( device.currentValue("switch").equals("off") ) {
    	log.debug "Dryer is off, no action taken"
        return true
    } else {
    	log.debug "Dryer is on, proceeding"
        return false
    }
}

def getVersion(){
    sendEvent(name: "version", value: 0.23)
}

