
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;

public class GUI implements ActionListener {

	private JFrame frame;
	private JCheckBox chckbxTestrun;
	private JButton btnSelectOsuSong;
	private JButton btnStart;
	private JCheckBox chckbxKeephitsounds;
	private JCheckBox chckbxStandard;
	private JCheckBox chckbxCTB;
	private JCheckBox chckbxTaiko;
	private JCheckBox chckbxMania;
	private JCheckBox chckbxRemoveGamemodes;

	private JLabel lblStandard;
	private JLabel lblMania;
	private JLabel lblTaiko;
	private JLabel lblCTB;

	private JLabel lblOsuFolder;

	private Options options = new Options();

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
		frame.setVisible(true);
	}

	public Options getOptions() {
		return options;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JLabel lblTestrun = new JLabel("Testrun");
		lblTestrun.setBounds(11, 45, 46, 14);
		frame.getContentPane().add(lblTestrun);

		chckbxTestrun = new JCheckBox("");
		chckbxTestrun.setBounds(124, 45, 21, 23);
		frame.getContentPane().add(chckbxTestrun);
		chckbxTestrun.setSelected(true);
		chckbxTestrun.addActionListener(this);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(10, 227, 414, 23);
		frame.getContentPane().add(progressBar);

		btnSelectOsuSong = new JButton("Select osu song folder");
		btnSelectOsuSong.setBounds(10, 11, 139, 23);
		frame.getContentPane().add(btnSelectOsuSong);
		btnSelectOsuSong.addActionListener(this);

		lblOsuFolder = new JLabel("");
		lblOsuFolder.setBounds(175, 11, 175, 14);
		frame.getContentPane().add(lblOsuFolder);

		btnStart = new JButton("Start");
		btnStart.setBounds(175, 167, 139, 23);
		frame.getContentPane().add(btnStart);
		btnStart.addActionListener(this);

		JLabel lblRemoveGamemodes = new JLabel("Remove gamemodes");
		lblRemoveGamemodes.setBounds(11, 70, 107, 14);
		frame.getContentPane().add(lblRemoveGamemodes);

		chckbxRemoveGamemodes = new JCheckBox("");
		chckbxRemoveGamemodes.setBounds(124, 66, 97, 23);
		frame.getContentPane().add(chckbxRemoveGamemodes);
		chckbxRemoveGamemodes.addActionListener(this);

		lblStandard = new JLabel("Standard");
		lblStandard.setBounds(11, 95, 46, 14);
		frame.getContentPane().add(lblStandard);

		lblTaiko = new JLabel("Taiko");
		lblTaiko.setBounds(11, 120, 78, 14);
		frame.getContentPane().add(lblTaiko);

		lblCTB = new JLabel("CTB");
		lblCTB.setBounds(11, 145, 46, 14);
		frame.getContentPane().add(lblCTB);

		lblMania = new JLabel("Mania");
		lblMania.setBounds(11, 170, 46, 14);
		frame.getContentPane().add(lblMania);

		chckbxStandard = new JCheckBox("");
		chckbxStandard.setBounds(124, 91, 97, 23);
		frame.getContentPane().add(chckbxStandard);
		chckbxStandard.addActionListener(this);

		chckbxTaiko = new JCheckBox("");
		chckbxTaiko.setBounds(124, 116, 97, 23);
		frame.getContentPane().add(chckbxTaiko);
		chckbxTaiko.addActionListener(this);

		chckbxCTB = new JCheckBox("");
		chckbxCTB.setBounds(124, 141, 97, 23);
		frame.getContentPane().add(chckbxCTB);
		chckbxCTB.addActionListener(this);

		chckbxMania = new JCheckBox("");
		chckbxMania.setBounds(124, 166, 97, 23);
		frame.getContentPane().add(chckbxMania);
		chckbxMania.addActionListener(this);

		JLabel lblKeepHitsounds = new JLabel("Keep hitsounds");
		lblKeepHitsounds.setBounds(175, 45, 83, 14);
		frame.getContentPane().add(lblKeepHitsounds);

		chckbxKeephitsounds = new JCheckBox("");
		chckbxKeephitsounds.setBounds(264, 41, 97, 23);
		frame.getContentPane().add(chckbxKeephitsounds);

		lblStandard.setVisible(false);
		lblMania.setVisible(false);
		lblTaiko.setVisible(false);
		lblCTB.setVisible(false);
		chckbxStandard.setVisible(false);
		chckbxMania.setVisible(false);
		chckbxTaiko.setVisible(false);
		chckbxCTB.setVisible(false);

		chckbxKeephitsounds.addActionListener(this);
	}

	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == chckbxTestrun) {
			if (chckbxTestrun.isSelected()) {
				options.testrun = true;
			} else {
				options.testrun = false;
			}
		} else if (ae.getSource() == chckbxKeephitsounds) {
			if (chckbxKeephitsounds.isSelected()) {
				options.keepHitsounds = true;
			} else {
				options.keepHitsounds = false;
			}

		} else if (ae.getSource() == chckbxStandard) {
			if (chckbxStandard.isSelected()) {
				options.gamemodesToRemove[0] = true;
			} else {
				options.gamemodesToRemove[0] = false;
			}
		} else if (ae.getSource() == chckbxTaiko) {
			if (chckbxTaiko.isSelected()) {
				options.gamemodesToRemove[1] = true;
			} else {
				options.gamemodesToRemove[1] = false;
			}
		} else if (ae.getSource() == chckbxCTB) {
			if (chckbxCTB.isSelected()) {
				options.gamemodesToRemove[2] = true;
			} else {
				options.gamemodesToRemove[2] = false;
			}
		} else if (ae.getSource() == chckbxMania) {
			if (chckbxMania.isSelected()) {
				options.gamemodesToRemove[3] = true;
			} else {
				options.gamemodesToRemove[3] = true;
			}

		} else if (ae.getSource() == chckbxRemoveGamemodes) {
			if (chckbxRemoveGamemodes.isSelected()) {
				lblStandard.setVisible(true);
				lblMania.setVisible(true);
				lblTaiko.setVisible(true);
				lblCTB.setVisible(true);
				chckbxStandard.setVisible(true);
				chckbxMania.setVisible(true);
				chckbxTaiko.setVisible(true);
				chckbxCTB.setVisible(true);
				options.removeGamemodes = true;
			} else {
				lblStandard.setVisible(false);
				lblMania.setVisible(false);
				lblTaiko.setVisible(false);
				lblCTB.setVisible(false);
				chckbxStandard.setVisible(false);
				chckbxMania.setVisible(false);
				chckbxTaiko.setVisible(false);
				chckbxCTB.setVisible(false);
				options.removeGamemodes = false;
			}
		} else if (ae.getSource() == btnSelectOsuSong) {
			JFileChooser f = new JFileChooser();
			f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			f.showSaveDialog(null);
			if (f.getSelectedFile() != null) {
				options.path = f.getSelectedFile().getAbsolutePath().replace("\\", "/");
				lblOsuFolder.setText(options.path);
			}

		} else if (ae.getSource() == btnStart) {
			if (options.path == null) {
				JOptionPane.showMessageDialog(frame, "You did not yet select a folder", "Something doesn't look right",
						JOptionPane.OK_OPTION);
			} else {
				if (!options.verifyPath()) {
					Object[] options = { "Ok", "Cancel" };
					int n = JOptionPane.showOptionDialog(frame,
							"osu!.exe was not found one directory lower in your folder. This is ok if your songs folder is not in your osu! folder",
							"Something doesn't look right", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE, null, options, options[1]);
					//OK
					if (n == 0) {
						n = JOptionPane.showOptionDialog(frame,
								"Are you ABSOLUTLY sure you got the right folder?\nYou may loose important files, they will not apear in your recycle bin",
								"Something doesn't look right", JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.ERROR_MESSAGE, null, options, options[1]);
						if (n == 0) {

						}
					}
				}
			}
		}
	}
}