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
package nl.knokko.gui.shader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GuiShader {
	
	public static final GuiShader GUI_SHADER = new GuiShader();
	
	private static int loadShader(URL url, int type){
        StringBuilder shaderSource = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
            String line;
            while((line = reader.readLine())!=null){
                shaderSource.append(line).append("//\n");
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS ) == GL11.GL_FALSE){
        	System.out.println("Failed to load shader " + url + " (" + type + ")");
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        return shaderID;
    }
	
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private int locationScreenPosition;
	private int locationSize;
	
	private int locationUV;

	private GuiShader() {
		vertexShaderID = loadShader(GuiShader.class.getClassLoader().getResource("nl/knokko/gui/shader/vertex.shader"), GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(GuiShader.class.getClassLoader().getResource("nl/knokko/gui/shader/fragment.shader"), GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		GL20.glBindAttribLocation(programID, 0, "modelPosition");
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		locationScreenPosition = GL20.glGetUniformLocation(programID, "screenPosition");
		locationSize = GL20.glGetUniformLocation(programID, "size");
		locationUV = GL20.glGetUniformLocation(programID, "uv");
	}
	
	public void loadPosition(float x, float y){
		GL20.glUniform2f(locationScreenPosition, x, y);
	}
	
	public void loadSize(float width, float height){
		GL20.glUniform2f(locationSize, width, height);
	}
	
	public void loadBounds(float minU, float minV, float maxU, float maxV) {
		GL20.glUniform4f(locationUV, minU, minV, maxU, maxV);
	}
	
	public void start(){
		GL20.glUseProgram(programID);
	}
	
	public void stop(){
		GL20.glUseProgram(0);
	}
	
	public void clean(){
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}
}