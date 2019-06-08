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
package nl.knokko.gui.texture.loader;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import nl.knokko.gui.texture.GLGuiTexture;
import nl.knokko.gui.texture.GLPartGuiTexture;
import nl.knokko.gui.texture.GuiTexture;

public class GLGuiTextureLoader implements GuiTextureLoader {
	
	private final List<Integer> textures;
	
	private PrintStream errorOutput;
	
	private static final int[] POWERS = {
		1,8,16,32,64,128,256,512,1024,2048,4096,8192,16384,32768,65536,131072,262144,524288,1048576,
		2097152, 4194304, 8388608, 16777216, 33554432, 67108864, 134217728, 268435456, 536870912, 1073741824
	};
	
	private static boolean isPowerOf2(int size){
		for(int power : POWERS)
			if(power == size)
				return true;
		return false;
	}
	
	private static int next2Power(int number) {
		for (int index = 0; index < POWERS.length; index++)
			if (POWERS[index] >= number)
				return POWERS[index];
		throw new IllegalArgumentException("Number too big: " + number);
	}

	public GLGuiTextureLoader() {
		textures = new ArrayList<Integer>();
		errorOutput = System.out;
	}
	
	@Override
	public GuiTexture loadTexture(BufferedImage source, int minX, int minY, int maxX, int maxY) {
		if (isPowerOf2(source.getWidth()) && isPowerOf2(source.getHeight())) {
			boolean allowAlpha = source.getTransparency() != BufferedImage.OPAQUE;
			int width = source.getWidth();
			int height = source.getHeight();
			ByteBuffer buffer = BufferUtils.createByteBuffer(source.getWidth() * source.getHeight() * (allowAlpha ? 4 : 3)); //4 for RGBA, 3 for RGB
	    	for(int y = 0; y < height; y++){
	        	for(int x = 0; x < width; x++){
	        		int rgb = source.getRGB(x, y);;
	        		buffer.put((byte) (rgb >> 16));
	        		buffer.put((byte) (rgb >> 8));
	        		buffer.put((byte) (rgb >> 0));
	        		if(allowAlpha)
	        			buffer.put((byte) (rgb >> 24));
	        	}
	    	}
	    	buffer.flip();
	    	int textureID = GL11.glGenTextures();
	    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	    	GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, allowAlpha ? GL11.GL_RGBA8 : GL11.GL_RGB8, source.getWidth(), source.getHeight(), 0, allowAlpha ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
	    	textures.add(textureID);
	    	return new GLPartGuiTexture(textureID, (float) minX / width, (float) minY / height, (float) maxX / width, (float) maxY / height, width, height);
		} else {
			BufferedImage image2 = new BufferedImage(next2Power(source.getWidth()), next2Power(source.getHeight()), source.getType());
			Graphics2D g = image2.createGraphics();
			g.drawImage(source, 0, 0, null);
			g.dispose();
			return loadTexture(image2, minX, minY, maxX, maxY);
		}
	}

	@Override
	public GuiTexture loadTexture(BufferedImage source) {
		if (isPowerOf2(source.getWidth()) && isPowerOf2(source.getHeight())) {
			boolean allowAlpha = source.getTransparency() != BufferedImage.OPAQUE;
			ByteBuffer buffer = BufferUtils.createByteBuffer(source.getWidth() * source.getHeight() * (allowAlpha ? 4 : 3)); //4 for RGBA, 3 for RGB
	    	for(int y = 0; y < source.getHeight(); y++){
	        	for(int x = 0; x < source.getWidth(); x++){
	        		int rgb = source.getRGB(x, y);
	        		buffer.put((byte) (rgb >> 16));
	        		buffer.put((byte) (rgb >> 8));
	        		buffer.put((byte) (rgb >> 0));
	        		if(allowAlpha)
	        			buffer.put((byte) (rgb >> 24));
	        	}
	    	}
	    	buffer.flip();
	    	int textureID = GL11.glGenTextures();
	    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
	    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
	    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
	    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
	    	GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
	    	GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, allowAlpha ? GL11.GL_RGBA8 : GL11.GL_RGB8, source.getWidth(), source.getHeight(), 0, allowAlpha ? GL11.GL_RGBA : GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, buffer);
	    	textures.add(textureID);
	    	return new GLGuiTexture(textureID, source.getWidth(), source.getHeight());
		} else {
			BufferedImage image2 = new BufferedImage(next2Power(source.getWidth()), next2Power(source.getHeight()), source.getType());
			Graphics2D g = image2.createGraphics();
			g.drawImage(source, 0, 0, null);
			g.dispose();
			return loadTexture(image2, 0, 0, source.getWidth() - 1, source.getHeight() - 1);
		}
	}

	@Override
	public GuiTexture loadTexture(String texturePath, int minX, int minY, int maxX, int maxY) {
		try {
			URL resource = GLGuiTextureLoader.class.getClassLoader().getResource(texturePath);
			if (resource == null) {
				throw new IOException("Can't find texture " + texturePath);
			}
			BufferedImage image = ImageIO.read(resource);
			return loadTexture(image, minX, minY, maxX, maxY);
		} catch (IOException e) {
			errorOutput.println("Can't load texture '" + texturePath + "': " + e.getMessage());
			e.printStackTrace(errorOutput);
			return null;
		}
	}

	@Override
	public GuiTexture loadTexture(String texturePath) {
		try {
			URL resource = GLGuiTextureLoader.class.getClassLoader().getResource(texturePath);
			if (resource == null) {
				throw new IOException("Can't find texture " + texturePath);
			}
			BufferedImage image = ImageIO.read(resource);
			return loadTexture(image);
		} catch (IOException e) {
			errorOutput.println("Can't load texture '" + texturePath + "': " + e.getMessage());
			e.printStackTrace(errorOutput);
			return null;
		}
	}

	@Override
	public GuiTextureLoader setErrorOutput(PrintStream output) {
		errorOutput = output;
		return this;
	}
	
	public void clean(){
		for(int texture : textures)
			GL11.glDeleteTextures(texture);
	}
}