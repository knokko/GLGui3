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
package nl.knokko.gui.render;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import nl.knokko.gui.color.GuiColor;
import nl.knokko.gui.texture.GuiTexture;
import nl.knokko.gui.texture.loader.GLGuiTextureLoader;
import nl.knokko.gui.window.GLGuiWindow;

import static nl.knokko.gui.shader.GuiShader.GUI_SHADER;

public class GLGuiRenderer extends GuiRenderer {
	
	private static final float[] QUAD = {0,1, 0,0, 1,1, 1,0};
	
	private static FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private void loadModel(){
		loadModel(QUAD);
	}
	
	private void loadModel(float[] vertices){
		quadVAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(quadVAO);
		quadVBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, quadVBO);
		FloatBuffer buffer = storeDataInFloatBuffer(vertices);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	private int quadVAO;
	private int quadVBO;
	
	private final GLGuiWindow window;
	
	private Map<GuiColor,GuiTexture> colorMap;
	private GLGuiTextureLoader textureLoader;
	
	public GLGuiRenderer(GLGuiWindow window, GLGuiTextureLoader loader){
		textureLoader = loader;
		this.window = window;
	}
	
	public void init(){
		loadModel();
		colorMap = new HashMap<GuiColor,GuiTexture>();
	}
	
	public void start(){
		IntBuffer widthBuffer = BufferUtils.createIntBuffer(1);
		IntBuffer heightBuffer = BufferUtils.createIntBuffer(1);
		GLFW.glfwGetWindowSize(window.getWindowID(), widthBuffer, heightBuffer);
		GL11.glViewport(0, 0, widthBuffer.get(), heightBuffer.get());
		GUI_SHADER.start();
		GL30.glBindVertexArray(quadVAO);
		GL20.glEnableVertexAttribArray(0);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	public void stop(){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL20.glDisableVertexAttribArray(0);
		GL30.glBindVertexArray(0);
		GUI_SHADER.stop();
	}
	
	public void clean(){
		GL30.glDeleteVertexArrays(quadVAO);
		GL15.glDeleteBuffers(quadVBO);
		GUI_SHADER.clean();
	}

	@Override
	public GuiRenderer getArea(float minX, float minY, float maxX, float maxY) {
		return new RelativeGuiRenderer.Static(this, minX, minY, maxX, maxY);
	}

	@Override
	void renderTextureNow(GuiTexture texture, float minX, float minY, float maxX, float maxY) {
		if (minX <= 1 && minY <= 1 && maxX >= 0 && maxY >= 0) {
			// Don't waste time rendering things that are completely off the screen
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GUI_SHADER.loadPosition(minX * 2 - 1, minY * 2 - 1);
			GUI_SHADER.loadSize(2 * (maxX - minX), 2 * (maxY - minY));
			GUI_SHADER.loadBounds(texture.getMinU(), texture.getMinV(), texture.getMaxU(), texture.getMaxV());
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getTextureID());
			GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		}
	}

	@Override
	void fillNow(GuiColor color, float minX, float minY, float maxX, float maxY) {
		renderTextureNow(getFilledTexture(color), minX, minY, maxX, maxY);
	}

	@Override
	void clearNow(GuiColor color) {
		GL11.glClearColor(color.getRedF(), color.getGreenF(), color.getBlueF(), color.getAlphaF());
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}
	
	@Override
	void renderNow(List<RenderCommand> renderCommands) {
		start();
		super.renderNow(renderCommands);
		stop();
		GLFW.glfwSwapBuffers(window.getWindowID());
	}
	
	private GuiTexture getFilledTexture(GuiColor color){
		GuiTexture texture = colorMap.get(color);
		if(texture == null){
			BufferedImage image = new BufferedImage(1, 1, color.getAlphaF() == 1 ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB);
			image.setRGB(0, 0, new Color(color.getRedF(), color.getGreenF(), color.getBlueF(), color.getAlphaF()).getRGB());
			texture = textureLoader.loadTexture(image);
			colorMap.put(color, texture);
		}
		return texture;
	}
}