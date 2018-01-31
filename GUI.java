package osucleaner;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.filechooser.FileNameExtensionFilter;
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

	private JCheckBox chckbxReplaceBackgrounds;

	private JLabel lblStandard;
	private JLabel lblMania;
	private JLabel lblTaiko;
	private JLabel lblCTB;

	private JLabel lblOsuFolder;

	private JProgressBar progressBar;

	private Options options = new Options();

	private boolean inProgress = false;

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
		frame.setSize(450, 300);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		WindowListener exitListener = new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (inProgress) {
					int confirm = JOptionPane.showOptionDialog(null,
							"Are you sure you want to exit?\nThe application is currently working", "Exit Confirmation",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
					if (confirm == 0) {
						System.exit(0);
					}
				} else
					System.exit(0);

			}
		};
		frame.addWindowListener(exitListener);
		frame.getContentPane().setLayout(null);

		JLabel lblTestrun = new JLabel("Testrun");
		lblTestrun.setBounds(11, 45, 70, 15);
		frame.getContentPane().add(lblTestrun);

		chckbxTestrun = new JCheckBox("");
		chckbxTestrun.setBounds(145, 45, 20, 20);
		frame.getContentPane().add(chckbxTestrun);
		chckbxTestrun.setSelected(true);
		chckbxTestrun.addActionListener(this);

		progressBar = new JProgressBar();
		progressBar.setBounds(10, 225, 415, 25);
		progressBar.setValue(0);
		frame.getContentPane().add(progressBar);

		btnSelectOsuSong = new JButton("Select osu song folder");
		btnSelectOsuSong.setBounds(10, 10, 170, 25);
		frame.getContentPane().add(btnSelectOsuSong);
		btnSelectOsuSong.addActionListener(this);

		lblOsuFolder = new JLabel("");
		lblOsuFolder.setBounds(200, 10, 175, 15);
		frame.getContentPane().add(lblOsuFolder);

		btnStart = new JButton("Start");
		btnStart.setBounds(200, 170, 140, 25);
		frame.getContentPane().add(btnStart);
		btnStart.addActionListener(this);

		JLabel lblRemoveGamemodes = new JLabel("Remove gamemodes");
		lblRemoveGamemodes.setBounds(10, 70, 150, 15);
		frame.getContentPane().add(lblRemoveGamemodes);

		chckbxRemoveGamemodes = new JCheckBox("");
		chckbxRemoveGamemodes.setBounds(145, 65, 20, 20);
		frame.getContentPane().add(chckbxRemoveGamemodes);
		chckbxRemoveGamemodes.addActionListener(this);

		lblStandard = new JLabel("Standard");
		lblStandard.setBounds(10, 95, 60, 15);
		frame.getContentPane().add(lblStandard);

		lblTaiko = new JLabel("Taiko");
		lblTaiko.setBounds(10, 120, 60, 15);
		frame.getContentPane().add(lblTaiko);

		lblCTB = new JLabel("CTB");
		lblCTB.setBounds(10, 145, 60, 15);
		frame.getContentPane().add(lblCTB);

		lblMania = new JLabel("Mania");
		lblMania.setBounds(10, 170, 60, 15);
		frame.getContentPane().add(lblMania);

		chckbxStandard = new JCheckBox("");
		chckbxStandard.setBounds(145, 90, 20, 20);
		frame.getContentPane().add(chckbxStandard);
		chckbxStandard.addActionListener(this);

		chckbxTaiko = new JCheckBox("");
		chckbxTaiko.setBounds(145, 115, 20, 20);
		frame.getContentPane().add(chckbxTaiko);
		chckbxTaiko.addActionListener(this);

		chckbxCTB = new JCheckBox("");
		chckbxCTB.setBounds(145, 140, 20, 20);
		frame.getContentPane().add(chckbxCTB);
		chckbxCTB.addActionListener(this);

		chckbxMania = new JCheckBox("");
		chckbxMania.setBounds(145, 165, 20, 20);
		frame.getContentPane().add(chckbxMania);
		chckbxMania.addActionListener(this);

		JLabel lblKeepHitsounds = new JLabel("Keep hitsounds");
		lblKeepHitsounds.setBounds(200, 45, 100, 15);
		frame.getContentPane().add(lblKeepHitsounds);

		chckbxKeephitsounds = new JCheckBox("");
		chckbxKeephitsounds.setBounds(325, 40, 20, 20);
		frame.getContentPane().add(chckbxKeephitsounds);

		JLabel lblReplaceBackgrounds = new JLabel("Replace backgrounds");
		lblReplaceBackgrounds.setBounds(200, 70, 120, 15);
		frame.getContentPane().add(lblReplaceBackgrounds);

		chckbxReplaceBackgrounds = new JCheckBox("");
		chckbxReplaceBackgrounds.setBounds(325, 65, 20, 20);
		frame.getContentPane().add(chckbxReplaceBackgrounds);
		chckbxReplaceBackgrounds.addActionListener(this);

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
		} else if (ae.getSource() == chckbxReplaceBackgrounds) {
			if (chckbxReplaceBackgrounds.isSelected()) {
				JFileChooser f = new JFileChooser();
				f.setFileSelectionMode(JFileChooser.FILES_ONLY);
				f.setDialogTitle("Select your image");
				f.setApproveButtonText("Select");
				f.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png"));
				if (f.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
					options.image = f.getSelectedFile();
					options.replaceAllBackgrounds = true;
				} else {
					chckbxReplaceBackgrounds.setSelected(false);
					options.replaceAllBackgrounds = false;
				}
			} else {
				options.replaceAllBackgrounds = false;
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
			f.setDialogTitle("Select your songs folder");
			f.setApproveButtonText("Select");
			if (f.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
				options.root = f.getSelectedFile();
				lblOsuFolder.setText(options.root.getPath());
			}

		} else if (ae.getSource() == btnStart) {
			if (options.root == null) {
				JOptionPane.showMessageDialog(frame, "You did not yet select a folder", "Something doesn't look right",
						JOptionPane.OK_OPTION);
			} else {
				int status = options.verify();
				Object[] a = { "Ok", "Cancel" };
				int n = 0;
				if (status == 0) {
					n = JOptionPane.showOptionDialog(frame, "Please doublecheck you settings. Confirm with ok",
							"Ready to go", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, a,
							a[1]);
					//OK
					if (n == 0) {
						start();
					}
				} else if (status == 1) {

					n = JOptionPane.showOptionDialog(frame,
							"osu!.exe was not found one directory lower in your folder. This is ok if your songs folder is not in your osu! folder",
							"Something doesn't look right", JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.WARNING_MESSAGE, null, a, a[1]);
					//OK
					if (n == 0) {
						n = JOptionPane.showOptionDialog(frame,
								"Are you ABSOLUTLY sure you got the right folder?\nYou may loose important files, they will not apear in your recycle bin",
								"Something doesn't look right", JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.ERROR_MESSAGE, null, a, a[1]);
						if (n == 0) {
							start();
						}
					}
				} else if (status == 2) {
					Util.notice("If you wanted to delete all gamemodes you could just uninstall osu");
				} else {
					Util.notice("You didn't selecet a valid folder");
				}
			}
		}
	}

	public void setInactive() {
		chckbxTestrun.setEnabled(false);
		btnSelectOsuSong.setEnabled(false);
		btnStart.setEnabled(false);
		chckbxKeephitsounds.setEnabled(false);
		chckbxStandard.setEnabled(false);
		chckbxCTB.setEnabled(false);
		chckbxTaiko.setEnabled(false);
		chckbxMania.setEnabled(false);
		chckbxRemoveGamemodes.setEnabled(false);
	}

	private void start() {
		setInactive();
		new Thread(() -> Walker.start(options, progressBar)).start();
	}
}