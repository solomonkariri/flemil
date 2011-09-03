package org.flemil.control;

import javax.microedition.lcdui.Canvas;


public class BBKeysListener implements net.rim.device.api.system.KeyListener{
	public BBKeysListener(){
		net.rim.device.api.system.Application.getApplication().addKeyListener(this);
	}
	public boolean keyChar(char arg0, int arg1, int arg2) {
		return shouldHandle();
	}

	public boolean keyDown(int arg0, int arg1) {
		if(shouldHandle()){
			if(net.rim.device.api.ui.Keypad.key(arg0) == net.rim.device.api.ui.Keypad.KEY_MENU){
				GlobalControl.getControl().keyPressedEvent(GlobalControl.softKeys[1]);
			}
			return true;
		}
		return false;
	}

	public boolean keyRepeat(int arg0, int arg1) {
		return keyDown(arg0, arg1);
	}

	public boolean keyStatus(int arg0, int arg1) {
		return shouldHandle();
	}

	public boolean keyUp(int arg0, int arg1) {
		if(shouldHandle()){
			if(net.rim.device.api.ui.Keypad.key(arg0) == net.rim.device.api.ui.Keypad.KEY_MENU){
				GlobalControl.getControl().keyReleasedEvent(GlobalControl.softKeys[1]);
			}
			return true;
		}
		return false;
	}
	
	private boolean shouldHandle(){
		if(GlobalControl.getControl().getDisplay().getCurrent() instanceof Canvas){
			return true;
		}
		return false;
	}
}