package br.com.RatosDePC.Brpp.IDEui;

/**
 * Barra de Menu do IDE
 * 
 * @author Mateus Berardo de Souza Terra e Rafael Mascarenhas Dal Moro
 * @contributors 
 * @version 5/2/2016
 */

//import gnu.io.CommPortIdentifier;

import gnu.io.CommPortIdentifier;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import br.com.RatosDePC.SerialMonitor.SerialMonitor;
import br.com.RatosDePC.Brpp.Utils.CommPortUtils;
import br.com.RatosDePC.Brpp.Utils.FileUtils;

@SuppressWarnings("serial")
public class MenuBar extends JMenuBar {

	private static String[] coms; // fix
	private static String[] boards = { "Uno", "Mega 1280", "Mega 2560",
			"Mega ADK", "Nano", "Nano 168", "Diecimila ou Duemilanove 328",
			"Diecimila ou Duemilanove 168", "Leonardo", "Micro", "Esplora",
			"Mini 328", "Mini 168", "Ethernet", "Fio", "BT 328", "BT 168",
			"LilyPad USB", "LilyPad 328", "LilyPad 168",
			"Pro ou Pro Mini 328 5V", "Pro ou Pro Mini 328 3V3",
			"Pro ou Pro Mini 168 5V", "Pro ou Pro Mini 168 3V3", "Gemma" };
	private JMenu subBoard;
	private static JRadioButtonMenuItem[] radioBoards;
	private ButtonGroup gp;
	private static JRadioButtonMenuItem[] radioCOMS;
	private ButtonGroup gpCom;
	private JMenu subCOM;
	private JMenu fileMenu;
	private JMenu ferrMenu;
	private JMenuItem novoItem;
	private JMenuItem salvarItem;
	private JMenuItem salvarComoItem;
	private JMenuItem abrirItem;
	private JMenuItem serialMonitor;

	public MenuBar() {
		// TODO Auto-generated constructor stub
		coms = new String[15];
		// for (int x = 0; x < coms.length; x++) {
		// coms[x] = "COM" + (x + 1);
		// }
		setComs();
		fileMenu = new JMenu("Arquivo");
		fileMenu.setMnemonic(KeyEvent.VK_A);
		add(fileMenu);

		novoItem = new JMenuItem("Novo");
		Action novoAction = new AbstractAction("Novo") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				int choice = JOptionPane.showConfirmDialog(null,
						"Voc� quer salvar o rascunho antes de criar um novo?");
				JTextPane txt = BrppIDEFrame.getTextPane();
				switch (choice) {
				case 0:
					FileUtils.saveFile(txt);
				case 1:
					BrppIDEFrame.getTextPane().setText(BrppIDEFrame.getMin());
					FileUtils.createFile(txt);
					break;
				case 2:
					break;
				}
			}
		};
		novoAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
		novoItem.setAction(novoAction);
		fileMenu.add(novoItem);

		ferrMenu = new JMenu("Ferramentas");
		add(ferrMenu);
		subBoard = new JMenu("Placa");
		subCOM = new JMenu("Porta");
		ferrMenu.add(subBoard);
		ferrMenu.add(subCOM);
		int x = 0;
		gp = new ButtonGroup();
		radioBoards = new JRadioButtonMenuItem[boards.length];
		for (String a : boards) {
			radioBoards[x] = new JRadioButtonMenuItem(a);
			gp.add(radioBoards[x]);
			subBoard.add(radioBoards[x]);
			x++;
		}
		gpCom = new ButtonGroup();
		x = 0;
		radioCOMS = new JRadioButtonMenuItem[coms.length];
		for (String a : coms) {
			radioCOMS[x] = new JRadioButtonMenuItem(a);
			gpCom.add(radioCOMS[x]);
			subCOM.add(radioCOMS[x]);
			x++;
		}
		serialMonitor = new JMenuItem("Monitor Serial");
		Action serialAction = new AbstractAction("Monitor Serial") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				try {
					SerialMonitor serial = new SerialMonitor(
							getSelectedIndexCOM());
					serial.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					serial.setSize(500, 600);
					if (serial.getConnected())
						serial.setVisible(true);
					else
						serial.dispose();

				} catch (TooManyListenersException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		serialAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_M, KeyEvent.CTRL_DOWN_MASK
						| KeyEvent.SHIFT_DOWN_MASK));
		serialMonitor.setAction(serialAction);
		ferrMenu.addSeparator();
		ferrMenu.add(serialMonitor);
		abrirItem = new JMenuItem("Abrir");
		Action abrirAction = new AbstractAction("Abrir") {
			@Override
			public void actionPerformed(ActionEvent e) {
				FileUtils.abrirFile(BrppIDEFrame.getTextPane());
			}
		};
		abrirAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
		abrirItem.setAction(abrirAction);
		fileMenu.add(abrirItem);

		salvarItem = new JMenuItem("Salvar");
		Action salvarAction = new AbstractAction("Salvar") {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (FileUtils.getDiretorio() == null) {
					FileUtils.createFile(BrppIDEFrame.getTextPane());
				} else {
					FileUtils.saveFile(BrppIDEFrame.getTextPane());
				}

			}
		};

		salvarAction.putValue(Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
		salvarItem.setAction(salvarAction);
		fileMenu.add(salvarItem);

		salvarComoItem = new JMenuItem("Salvar como");
		Action salvarComoAction = new AbstractAction("SalvarComo") {

			@Override
			public void actionPerformed(ActionEvent e) {
				FileUtils.createFile(BrppIDEFrame.getTextPane());
			}
		};

		salvarComoAction.putValue(
				Action.ACCELERATOR_KEY,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK
						| KeyEvent.SHIFT_DOWN_MASK));
		salvarComoItem.setAction(salvarComoAction);
		fileMenu.add(salvarComoItem);

	}

	public static int getSelectedIndex() {

		for (int i = 0; i < boards.length; i++) {
			if (radioBoards[i].isSelected())
				return i;
		}
		return 0;
	}

	public static String getSelectedIndexCOM() {

		for (int i = 0; i < coms.length; i++) {
			if (radioCOMS[i].isSelected())
				return radioCOMS[i].getText();
		}
		return "COM1";
	}

	public void setComs() {
		@SuppressWarnings("rawtypes")
		Enumeration comm = CommPortUtils.getComPorts();
		ArrayList<String> comList = new ArrayList<String>();
		while (comm.hasMoreElements()) {
			CommPortIdentifier port_identifier = (CommPortIdentifier) comm
					.nextElement();
			if (port_identifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				comList.add(port_identifier.getName());
			}
		}
		if (comList.size() > 0) {
			coms = new String[comList.size()];
			int cont = 0;
			for (String c : comList) {
				this.coms[cont] = c;
			}
		} else {
			coms = new String[1];
			coms[0] = "N�o h� portas dispon�veis";
		}
	}
}