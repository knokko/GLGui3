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
package nl.knokko.gui.window;

import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWVidMode.Buffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import nl.knokko.gui.component.GuiComponent;
import nl.knokko.gui.component.state.GLComponentState;
import nl.knokko.gui.component.state.GuiComponentState;
import nl.knokko.gui.keycode.GLKeyConverter;
import nl.knokko.gui.render.GLGuiRenderer;
import nl.knokko.gui.texture.loader.GLGuiTextureLoader;
import nl.knokko.gui.texture.loader.GuiTextureLoader;
import nl.knokko.gui.util.CharBuilder;

public class GLGuiWindow extends GuiWindow {

	private final GLGuiTextureLoader textureLoader;
	private final GLGuiRenderer guiRenderer;
	private final CharBuilder charBuilder;

	private int innerWidth;
	private int innerHeight;

	private int insetLeft, insetRight, insetBottom, insetTop;

	private int positionX;
	private int positionY;

	private float mouseX;
	private float mouseY;

	private long windowID;

	private float mouseDX;
	private float mouseDY;

	public GLGuiWindow() {
		textureLoader = new GLGuiTextureLoader();
		guiRenderer = new GLGuiRenderer(this, textureLoader);
		charBuilder = new CharBuilder(textureLoader);
	}

	public GLGuiWindow(GuiComponent mainComponent) {
		this();
		this.mainComponent = mainComponent;
	}

	protected void ensureOnMainThread() {
		Thread current = Thread.currentThread();
		if (!current.getName().equals("main")) {
			throw new IllegalStateException("This method can only be called on the main thread!");
		}
	}

	public long getWindowID() {
		return windowID;
	}

	@Override
	protected void directOpen(String title, int width, int height, boolean border) {
		ensureOnMainThread();
		GLFW.glfwInit();
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, border ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
		
		// Notice that innerWidth and innerHeight are actually the windowWidth and
		// windowHeight at this point, but this will be fixed in afterOpen()
		this.innerWidth = width;
		this.innerHeight = height;
		windowID = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
		afterOpen();
	}

	@Override
	protected void directOpen(String title, boolean border) {
		ensureOnMainThread();
		GLFW.glfwInit();
		long monitor;
		if (border) {
			monitor = MemoryUtil.NULL;
		} else {
			monitor = GLFW.glfwGetPrimaryMonitor();
		}
		Buffer videoModes = GLFW.glfwGetVideoModes(GLFW.glfwGetPrimaryMonitor());

		// TODO If border is true, the height of the window should be a bit smaller
		// to not make it overlap with the bottom toolbar in Windows

		// The last videoMode is the largest one available, so that's the one we want
		GLFWVidMode videoMode = videoModes.get(videoModes.limit() - 1);

		// Notice that innerWidth and innerHeight are actually the windowWidth and
		// windowHeight at this point, but this will be fixed in afterOpen()
		this.innerWidth = videoMode.width();
		this.innerHeight = videoMode.height();

		GLFW.glfwWindowHint(GLFW.GLFW_DECORATED, border ? 1 : 0);
		windowID = GLFW.glfwCreateWindow(this.innerWidth, this.innerHeight, title, monitor, MemoryUtil.NULL);
		afterOpen();
	}

	protected void afterOpen() {
		IntBuffer leftBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer topBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer rightBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer bottomBuffer = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowFrameSize(windowID, leftBuffer, topBuffer, rightBuffer, bottomBuffer);
		this.insetLeft = leftBuffer.get();
		this.insetTop = topBuffer.get();
		this.insetRight = rightBuffer.get();
		this.insetBottom = bottomBuffer.get();
		this.innerWidth -= this.insetLeft;
		this.innerWidth -= this.insetRight;
		this.innerHeight -= this.insetBottom;
		this.innerHeight -= this.insetTop;
		GLFW.glfwSetWindowSize(windowID, this.innerWidth, this.innerHeight);
		GLFW.glfwMakeContextCurrent(windowID);
		GL.createCapabilities();
		// GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
		guiRenderer.init();
		GLFW.glfwSetWindowSizeCallback(windowID, (long windowID, int width, int height) -> {
			this.innerWidth = width;
			this.innerHeight = height;
			markChange();
		});
		IntBuffer positionBufferX = BufferUtils.createIntBuffer(1);
		IntBuffer positionBufferY = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowPos(windowID, positionBufferX, positionBufferY);
		this.positionX = positionBufferX.get() - this.insetLeft;
		this.positionY = positionBufferY.get() - this.insetTop;
		System.out.println("Window position is (" + positionX + "," + positionY + ")");
		GLFW.glfwSetWindowPosCallback(windowID, (long windowID, int newX, int newY) -> {
			
			// 32000 will be returned when the window is iconified (hidden)
			if (newX != -32000 && newY != -32000) {
				this.positionX = newX - this.insetLeft;
				this.positionY = newY - this.insetTop;
			}
		});
		GLFW.glfwSetCharCallback(windowID, (long windowID, int codePoint) -> {
			if (codePoint > Character.MAX_VALUE) {
				System.out.println("Unsupported character (" + codePoint + ")");
				// TODO Add proper support for high code points
				return;
			}
			char character = (char) codePoint;
			if (listener == null || !listener.preKeyPressed(character)) {
				mainComponent.keyPressed(character);
				if (listener != null) {
					listener.postKeyPressed(character);
				}
			}
		});
		GLFW.glfwSetKeyCallback(windowID, (long windowID, int glKey, int scancode, int action, int mods) -> {
			int[] keys = GLKeyConverter.get(glKey);
			if (keys != null) {
				for (int key : keys) {
					if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
						input.setKeyDown(key);
						if (listener == null || !listener.preKeyPressed(key)) {
							mainComponent.keyPressed(key);
							if (listener != null) {
								listener.postKeyPressed(key);
							}
						}
					} else if (action == GLFW.GLFW_RELEASE) {
						if (listener == null || !listener.preKeyReleased(key)) {
							mainComponent.keyReleased(key);
							if (listener != null) {
								listener.postKeyReleased(key);
							}
						}
						input.setKeyUp(key);
					} else {
						System.out.println("Unknown key action " + action);
					}
				}
			}
		});
		GLFW.glfwSetCursorPosCallback(windowID, (long windowID, double newX, double newY) -> {
			if (newX < 0 || newY < 0 || newX >= this.innerWidth || newY >= this.innerHeight) {
				this.mouseX = Float.NaN;
				this.mouseY = Float.NaN;
			} else {
				float newMouseX = (float) (newX / this.innerWidth);
				float newMouseY = 1f - (float) (newY / this.innerHeight);
				this.mouseDX = newMouseX - this.mouseX;
				this.mouseDY = newMouseY - this.mouseY;
				this.mouseX = newMouseX;
				this.mouseY = newMouseY;
			}
			markChange();
		});
		GLFW.glfwSetCursorEnterCallback(windowID, (long windowID, boolean entered) -> {
			if (!entered) {
				this.mouseX = Float.NaN;
				this.mouseY = Float.NaN;
				markChange();
			}
		});
		GLFW.glfwSetMouseButtonCallback(windowID, (long windowID, int button, int action, int mods) -> {

			// The gui library works with buttons starting from 1 instead of 0, so a simple
			// fix:
			button++;

			if (action == GLFW.GLFW_PRESS) {
				input.setMouseDown(button);
			} else if (action == GLFW.GLFW_RELEASE) {
				input.setMouseUp(button);
				if (listener == null || !listener.preClick(this.mouseX, this.mouseY, button)) {
					mainComponent.click(this.mouseX, this.mouseY, button);
					if (listener != null) {
						listener.postClick(this.mouseX, this.mouseY, button);
					}
				}
			} else {
				System.out.println("Unknown mouse button action " + action);
			}
		});
		GLFW.glfwSetScrollCallback(windowID, (long windowID, double offsetX, double offsetY) -> {
			// The magic number 0.04 appears to be necessary to make scrolling the same as in AWTGuiWindow.
			float scroll = (float) (offsetY * 0.04);
			if (listener != null) {
				scroll = listener.preScroll(scroll);
			}
			if (scroll != 0) {
				mainComponent.scroll(scroll);
				if (listener != null) {
					listener.postScroll(scroll);
				}
			}
		});
	}

	@Override
	protected void preUpdate() {
		ensureOnMainThread();
		this.mouseDX = 0;
		this.mouseDY = 0;
		GLFW.glfwPollEvents();
	}

	@Override
	protected void postUpdate() {
	}

	@Override
	protected void directRender() {
		guiRenderer.start();
		mainComponent.render(guiRenderer);
		guiRenderer.stop();
		GLFW.glfwSwapBuffers(windowID);
	}

	@Override
	protected void directClose() {
		ensureOnMainThread();
		textureLoader.clean();
		guiRenderer.clean();
		GL.destroy();
		Callbacks.glfwFreeCallbacks(windowID);
		GLFW.glfwDestroyWindow(windowID);
		GLFW.glfwTerminate();
	}

	@Override
	public void run(int fps) {
		ensureOnMainThread();
		long delay = 1000000000 / fps;
		while (!GLFW.glfwWindowShouldClose(windowID) && !shouldStopRunning) {
			long startTime = System.nanoTime();
			if (listener == null || !listener.preRunLoop()) {
				update();
				render();
				if (listener != null)
					listener.postRunLoop();
			}
			long endTime = System.nanoTime();
			long passedTime = endTime - startTime;
			long sleepTime = delay - passedTime;
			if (sleepTime > 0) {
				try {
					Thread.sleep(sleepTime / 1000000, (int) (sleepTime % 1000000));
				} catch (InterruptedException e) {
					throw new RuntimeException("Unexpected interrupt", e);
				}
			}
		}
		close();
	}

	@Override
	public GuiTextureLoader getTextureLoader() {
		return textureLoader;
	}

	@Override
	public GLGuiRenderer getRenderer() {
		return guiRenderer;
	}

	@Override
	public CharBuilder getCharBuilder() {
		return charBuilder;
	}

	@Override
	protected GuiComponentState createState() {
		return new GLComponentState(this);
	}

	@Override
	public float getMouseX() {
		return this.mouseX;
	}

	@Override
	public float getMouseY() {
		return this.mouseY;
	}

	@Override
	public float getMouseDX() {
		return this.mouseDX;
	}

	@Override
	public float getMouseDY() {
		return this.mouseDY;
	}

	@Override
	public int getWindowPosX() {
		if (isOpen()) {
			return positionX;
		} else {
			return -1;
		}
	}

	@Override
	public int getWidth() {
		if (isOpen()) {
			return this.innerWidth;
		} else {
			return -1;
		}
	}

	@Override
	public int getWindowPosY() {
		if (isOpen()) {
			return positionY;
		} else {
			return -1;
		}
	}

	@Override
	public int getHeight() {
		if (isOpen()) {
			return this.innerHeight;
		} else {
			return -1;
		}
	}

	@Override
	public int getPosX() {
		if (isOpen()) {
			return this.positionX + this.insetLeft;
		} else {
			return -1;
		}
	}

	@Override
	public int getWindowWidth() {
		if (isOpen()) {
			return this.innerWidth + this.insetLeft + this.insetRight;
		} else {
			return -1;
		}
	}

	@Override
	public int getPosY() {
		if (isOpen()) {
			return this.positionY + this.insetTop;
		} else {
			return -1;
		}
	}

	@Override
	public int getWindowHeight() {
		if (isOpen()) {
			return this.innerHeight + this.insetBottom + this.insetTop;
		} else {
			return -1;
		}
	}
}