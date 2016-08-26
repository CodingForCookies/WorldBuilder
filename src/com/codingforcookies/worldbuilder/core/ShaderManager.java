package com.codingforcookies.worldbuilder.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ShaderManager {
	private final Map<String, Integer> _shaderPrograms = new HashMap<String, Integer>(32);
	private final Map<String, Integer> _fragmentShader = new HashMap<String, Integer>(32);
	private final Map<String, Integer> _vertexShader = new HashMap<String, Integer>(32);
	private static ShaderManager _instance = null;

	/**
	 * Returns (and creates – if necessary) the static instance
	 * of this helper class.
	 *
	 * @return The instance
	 */
	public static ShaderManager getInstance() {
		if(_instance == null)
			_instance = new ShaderManager();

		return _instance;
	}

	private ShaderManager() {
		initShader();

		System.out.println("Loading shader manager...");
	}

	private void initShader() {
		createVertexShader("pixelate.vert", "pixelate");
		createFragShader("pixelate.frag", "pixelate");

		for(HashMap.Entry<String, Integer> e : _fragmentShader.entrySet()) {
			int shaderProgram = GL20.glCreateProgram();

			GL20.glAttachShader(shaderProgram, _fragmentShader.get(e.getKey()));
			GL20.glAttachShader(shaderProgram, _vertexShader.get(e.getKey()));
			
			GL20.glLinkProgram(shaderProgram);
			if(GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
				System.err.println("Failed to link shader. Offender: " + e.getKey());
				System.err.println(GL20.glGetProgramInfoLog(shaderProgram));
			}
			
			GL20.glValidateProgram(shaderProgram);
			if(GL20.glGetProgrami(shaderProgram, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
				System.err.println("Failed to validate. Offender: " + e.getKey());
				System.err.println(GL20.glGetProgramInfoLog(shaderProgram));
			}
			

			_shaderPrograms.put(e.getKey(), shaderProgram);
		}
	}

	private int createFragShader(String filename, String title) {

		_fragmentShader.put(title, GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER));

		if (_fragmentShader.get(title) == 0) {
			return 0;
		}

		String fragCode = "";
		String line;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(ShaderManager.class.getClassLoader().getResourceAsStream("resources/shaders/" + filename)));
			while ((line = reader.readLine()) != null) {
				fragCode += line + "\n";
			}
		} catch (Exception e) {
			System.err.println("Failed to read fragment shader. Offender: " + filename);
			return 0;
		}

		GL20.glShaderSource(_fragmentShader.get(title), fragCode);
		GL20.glCompileShader(_fragmentShader.get(title));

		return _fragmentShader.get(title);
	}

	private int createVertexShader(String filename, String title) {

		_vertexShader.put(title, GL20.glCreateShader(GL20.GL_VERTEX_SHADER));

		if (_vertexShader.get(title) == 0) {
			return 0;
		}

		String fragCode = "";
		String line;
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(ShaderManager.class.getClassLoader().getResourceAsStream("resources/shaders/" + filename)));
			while ((line = reader.readLine()) != null) {
				fragCode += line + "\n";
			}
		} catch (Exception e) {
			System.err.println("Failed to read vertex shader. Offender: " + filename);
			return 0;
		}

		GL20.glShaderSource(_vertexShader.get(title), fragCode);
		GL20.glCompileShader(_vertexShader.get(title));

		return _vertexShader.get(title);
	}

	public void enableShader(String s) {
		if(s == null) {
			GL20.glUseProgram(0);
			return;
		}

		int shader = getShader(s);
		GL20.glUseProgram(shader);
	}

	public int getShader(String s) {
		return _shaderPrograms.get(s);
	}
}