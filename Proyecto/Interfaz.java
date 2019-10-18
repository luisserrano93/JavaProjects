package Aestrella;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextPane;

public class Interfaz  extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JFrame frame;
	static AstarSearchAlgo algoritmo;
	JButton btnBuscarCamino;
	JLabel lblOrigen;
	JLabel lblDestino;
	JComboBox<String> comboBox_1;
	JComboBox<String> comboBox;
	JTextPane textPane;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		algoritmo = new AstarSearchAlgo();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interfaz window = new Interfaz();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interfaz() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings("static-access")
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		comboBox = new JComboBox<String>();
		comboBox.setBounds(47, 31, 149, 20);
		frame.getContentPane().add(comboBox);
		for(int i = 0;i<AstarSearchAlgo.nodos.length;i++)
		{
			comboBox.addItem(algoritmo.nodos[i].nombre);
		}
		
		comboBox_1 = new JComboBox<String>();
		comboBox_1.setBounds(242, 31, 149, 20);
		frame.getContentPane().add(comboBox_1);
		for(int i = 0;i<algoritmo.nodos.length;i++)
		{
			comboBox_1.addItem(algoritmo.nodos[i].nombre);
		}
		
		lblOrigen = new JLabel("Origen");
		lblOrigen.setBounds(48, 6, 46, 14);
		frame.getContentPane().add(lblOrigen);
		
		lblDestino = new JLabel("Destino");
		lblDestino.setBounds(242, 6, 46, 14);
		frame.getContentPane().add(lblDestino);
		
		btnBuscarCamino = new JButton("Buscar camino");
		btnBuscarCamino.setBounds(159, 88, 129, 23);
		btnBuscarCamino.addActionListener(this);
		frame.getContentPane().add(btnBuscarCamino);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBounds(33, 132, 366, 118);
		frame.getContentPane().add(textPane);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == btnBuscarCamino)
		{
			textPane.setText(algoritmo.iniciar((String)comboBox.getSelectedItem(), (String)comboBox_1.getSelectedItem()));
		}
	}
}
