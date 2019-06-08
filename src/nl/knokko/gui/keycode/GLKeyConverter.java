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
package nl.knokko.gui.keycode;

import static nl.knokko.gui.keycode.KeyCode.*;
import static org.lwjgl.glfw.GLFW.*;

public class GLKeyConverter {
	
	private static int[][] CONVERT_MAP;
	
	static {
		CONVERT_MAP = new int[349][];
		//put(GLFW_KEY_UNKNOWN, UNDEFINED);
		
		put(GLFW_KEY_0, KEY_0_BASE, KEY_0);
		put(GLFW_KEY_1, KEY_1_BASE, KEY_1);
		put(GLFW_KEY_2, KEY_2_BASE, KEY_2);
		put(GLFW_KEY_3, KEY_3_BASE, KEY_3);
		put(GLFW_KEY_4, KEY_4_BASE, KEY_4);
		put(GLFW_KEY_5, KEY_5_BASE, KEY_5);
		put(GLFW_KEY_6, KEY_6_BASE, KEY_6);
		put(GLFW_KEY_7, KEY_7_BASE, KEY_7);
		put(GLFW_KEY_8, KEY_8_BASE, KEY_8);
		put(GLFW_KEY_9, KEY_9_BASE, KEY_9);
		
		put(GLFW_KEY_A, KEY_A);
		put(GLFW_KEY_B, KEY_B);
		put(GLFW_KEY_C, KEY_C);
		put(GLFW_KEY_D, KEY_D);
		put(GLFW_KEY_E, KEY_E);
		put(GLFW_KEY_F, KEY_F);
		put(GLFW_KEY_G, KEY_G);
		put(GLFW_KEY_H, KEY_H);
		put(GLFW_KEY_I, KEY_I);
		put(GLFW_KEY_J, KEY_J);
		put(GLFW_KEY_K, KEY_K);
		put(GLFW_KEY_L, KEY_L);
		put(GLFW_KEY_M, KEY_M);
		put(GLFW_KEY_N, KEY_N);
		put(GLFW_KEY_O, KEY_O);
		put(GLFW_KEY_P, KEY_P);
		put(GLFW_KEY_Q, KEY_Q);
		put(GLFW_KEY_R, KEY_R);
		put(GLFW_KEY_S, KEY_S);
		put(GLFW_KEY_T, KEY_T);
		put(GLFW_KEY_U, KEY_U);
		put(GLFW_KEY_V, KEY_V);
		put(GLFW_KEY_W, KEY_W);
		put(GLFW_KEY_X, KEY_X);
		put(GLFW_KEY_Y, KEY_Y);
		put(GLFW_KEY_Z, KEY_Z);
		
		put(GLFW_KEY_ESCAPE, KEY_ESCAPE);
		put(GLFW_KEY_GRAVE_ACCENT, KEY_GRAVE);
		put(GLFW_MOD_CAPS_LOCK, KEY_CAPSLOCK);
		put(GLFW_KEY_LEFT_SHIFT, KEY_SHIFT);
		put(GLFW_KEY_RIGHT_SHIFT, KEY_SHIFT);
		put(GLFW_KEY_LEFT_CONTROL, KEY_CONTROL);
		put(GLFW_KEY_RIGHT_CONTROL, KEY_CONTROL);
		put(GLFW_KEY_MENU, KEY_ALT);
		put(GLFW_KEY_SPACE, KEY_SPACE);
		
		put(GLFW_KEY_F1, KEY_F1);
		put(GLFW_KEY_F2, KEY_F2);
		put(GLFW_KEY_F3, KEY_F3);
		put(GLFW_KEY_F4, KEY_F4);
		put(GLFW_KEY_F5, KEY_F5);
		put(GLFW_KEY_F6, KEY_F6);
		put(GLFW_KEY_F7, KEY_F7);
		put(GLFW_KEY_F8, KEY_F8);
		put(GLFW_KEY_F9, KEY_F9);
		put(GLFW_KEY_F10, KEY_F10);
		put(GLFW_KEY_F11, KEY_F11);
		put(GLFW_KEY_F12, KEY_F12);
		
		put(GLFW_KEY_PAUSE, KEY_PAUSE);
		put(GLFW_KEY_INSERT, KEY_INSERT);
		put(GLFW_KEY_DELETE, KEY_DELETE);
		put(GLFW_KEY_MINUS, KEY_MINUS_BASE);
		put(GLFW_KEY_EQUAL, KEY_EQUALS);
		put(GLFW_KEY_KP_EQUAL, KEY_EQUALS);
		put(GLFW_KEY_BACKSPACE, KEY_BACKSPACE);
		put(GLFW_MOD_NUM_LOCK, KEY_NUMLOCK);
		
		put(GLFW_KEY_KP_DIVIDE, KEY_DIVIDE_NUMPAD);
		put(GLFW_KEY_KP_MULTIPLY, KEY_MULTIPLY_NUMPAD);
		put(GLFW_KEY_KP_SUBTRACT, KEY_MINUS_NUMPAD);
		put(GLFW_KEY_KP_ADD, KEY_PLUS_NUMPAD);
		
		put(GLFW_KEY_LEFT_BRACKET, KEY_OPENBRACKET);
		put(GLFW_KEY_RIGHT_BRACKET, KEY_CLOSEBRACKET);
		put(GLFW_KEY_BACKSLASH, KEY_BACKSLASH);
		put(GLFW_KEY_SEMICOLON, KEY_SEMICOLON);
		put(GLFW_KEY_APOSTROPHE, KEY_QUOTE);
		put(GLFW_KEY_ENTER, KEY_ENTER);
		put(GLFW_KEY_KP_ENTER, KEY_ENTER);
		put(GLFW_KEY_COMMA, KEY_COMMA);
		put(GLFW_KEY_PERIOD, KEY_PERIOD);
		put(GLFW_KEY_SLASH, KEY_SLASH);
		
		
		put(GLFW_KEY_KP_DECIMAL, KEY_DECIMAL);
		put(GLFW_KEY_KP_0, KEY_0_NUMPAD, KEY_0);
		put(GLFW_KEY_KP_1, KEY_1_NUMPAD, KEY_1);
		put(GLFW_KEY_KP_2, KEY_2_NUMPAD, KEY_2);
		put(GLFW_KEY_KP_3, KEY_3_NUMPAD, KEY_3);
		put(GLFW_KEY_KP_4, KEY_4_NUMPAD, KEY_4);
		put(GLFW_KEY_KP_5, KEY_5_NUMPAD, KEY_5);
		put(GLFW_KEY_KP_6, KEY_6_NUMPAD, KEY_6);
		put(GLFW_KEY_KP_7, KEY_7_NUMPAD, KEY_7);
		put(GLFW_KEY_KP_8, KEY_8_NUMPAD, KEY_8);
		put(GLFW_KEY_KP_9, KEY_9_NUMPAD, KEY_9);
		
		put(GLFW_KEY_LEFT, KEY_LEFT);
		put(GLFW_KEY_UP, KEY_UP);
		put(GLFW_KEY_RIGHT, KEY_RIGHT);
		put(GLFW_KEY_DOWN, KEY_DOWN);
	}
	
	private static void put(int glKeyCode, int... guiKeyCodes){
		CONVERT_MAP[glKeyCode] = guiKeyCodes;
	}
	
	public static int[] get(int glKeyCode){
		if (glKeyCode < 0 || glKeyCode >= CONVERT_MAP.length) {
			return null;
		}
		int[] original = getDirect(glKeyCode);
		if(original == null || original[0] == UNDEFINED)
			return null;
		int[] copy = new int[original.length];
		for(int i = 0; i < original.length; i++)
			copy[i] = original[i];
		return copy;
	}
	
	public static int[] getDirect(int glKeyCode){
		return CONVERT_MAP[glKeyCode];
	}
}