/*******************************************************************************
 * The MIT License
 *
 * Copyright (c) 2018 knokko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *  
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package nl.knokko.gui.component.state;

import nl.knokko.gui.window.GLGuiWindow;

public class GLComponentState implements GuiComponentState {
	
	private final GLGuiWindow window;
	
	public GLComponentState(GLGuiWindow window){
		this.window = window;
	}

	@Override
	public boolean isMouseOver() {
		double x = window.getMouseX();
		double y = window.getMouseY();
		return x == x && y == y;
	}

	@Override
	public float getMouseX() {
		return window.getMouseX();
	}

	@Override
	public float getMouseY() {
		return window.getMouseY();
	}

	@Override
	public GLGuiWindow getWindow(){
		return window;
	}

	@Override
	public float getMouseDX() {
		return window.getMouseDX();
	}

	@Override
	public float getMouseDY() {
		return window.getMouseDY();
	}

	@Override
	public float getMinX() {
		return 0;
	}

	@Override
	public float getMinY() {
		return 0;
	}

	@Override
	public float getMaxX() {
		return 1;
	}

	@Override
	public float getMaxY() {
		return 1;
	}
}