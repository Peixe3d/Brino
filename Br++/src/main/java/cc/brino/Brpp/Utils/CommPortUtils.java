package cc.brino.Brpp.Utils;

/*
 * Copyright (c) 2016 StarFruitBrasil
 * 
 * Permission is hereby granted, free of charge, to any
 * person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the
 * Software without restriction, including without
 * limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following
 * conditions:
 * 
 * The above copyright notice and this permission notice
 * shall be included in all copies or substantial portions
 * of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY
 * KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import cc.brino.SerialMonitor.SerialMonitor;


/**
 * 
 * @author Mateus Berardo de Souza Terra
 */
public class CommPortUtils {

	private static volatile boolean isOpen = false;
	private BufferedReader stdout;
	private BufferedWriter stdin;
	private static ProcessBuilder pb;
	private static Process p;
	final Thread ioThread = new Thread() {

		@Override
		public void run() {
			while (isOpen)
				try {
					SerialMonitor.display(String.valueOf((char) stdout.read()));
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	};

	public ArrayList<String> getComPorts() {
		ArrayList<String> enu_ports;
		if (System.getProperty("os.name").contains("Windows")) {
			String proc = "Python/Coms.exe";
			pb = new ProcessBuilder("cmd.exe", "/c", proc);
		} else {
			pb = new ProcessBuilder("bash", "-c",
					("python3 Python/Coms.py"));
		}
		pb.redirectErrorStream(true);
		String c = "";
		try {
			p = pb.start();
			stdout = new BufferedReader(new InputStreamReader(
					p.getInputStream(), "UTF8"));
			c = stdout.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		c = c.replace("[", "");
		c = c.replace("]", "");
		c = c.replace(" ", "");
		c = c.replace("'", "");
		if (!c.equals("")) {
			String[] coms = c.split(",");
			enu_ports = new ArrayList<String>(Arrays.asList(coms));
		} else {
			enu_ports = new ArrayList<String>();
		}
		return enu_ports;
	}

	public boolean openPort(String com) {
		ArrayList<String> ports = getComPorts();
		if (ports.contains(com)) {
			if (System.getProperty("os.name").contains("Windows")) {
				String proc = "Python/Monitor.exe";
				pb = new ProcessBuilder("cmd.exe", "/c", proc);
			} else {
				pb = new ProcessBuilder("bash", "-c",
						("python3 Python/Monitor.py " + com));
			}
			pb.redirectErrorStream(true);
			try {
				p = pb.start();
				stdout = new BufferedReader(
						new InputStreamReader(
								p.getInputStream(),
								"UTF8"));
				stdin = new BufferedWriter(
						new OutputStreamWriter(
								p.getOutputStream(),
								"UTF8"));
			} catch (IOException e) {
				// TODO Auto-generated catch
				// block
				e.printStackTrace();
				return false;
			}
			isOpen = true;
			ioThread.start();
			return true;
		}
		return false;
	}

	public void send(String msg) {
		try {
			stdin.write(msg);
			stdin.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void closePort() throws NullPointerException {
		isOpen = false;
		try {
			p.getErrorStream().close();
			p.getInputStream().close();
			p.getOutputStream().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		p.destroy();
	}
}