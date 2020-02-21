package com.mycompany.mavenimagepadder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

import image.ImageHelper;
import settings.Settings;


/*
 * Problems:
 * Only swedish lang.
 * Can't detect exif in jpeg (if any) rotation
 * 
 * Small prob.
 * After making a padded image, not replacing original, the original
 * which had transparent background, got black background (men bara i Wind. (10) explorer)
 * 
 * 
 * */


public class app extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	/*public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
	}*/
	AppPhraseHandler phraseHandler;
	
	//members
	JFrame f;
	Settings settings;
	ImageHelper imageHelper;
	List<File> foundOffSized;
	DefaultListModel<File> imageListModel;
	JPanel listPane;
	JLabel info1;
	JButton btn1;
	JButton btnSearch;
	JList<File> imageFileList;
	JScrollPane listScrollPane;
	JLabel infoMessage;
	JLabel errorMessage;
	JLabel ratioXLbl;
	JTextField ratioX;
	JLabel ratioYLbl; 
	JTextField ratioY;
	JPanel ratioRow;
	JCheckBox overwriteFiles;
	JCheckBox assertBothAxis;
	JPanel buttonPane;
	JPanel pane;
	JComboBox<String> languageChoice;
	ComboBoxModel<String> languageChoiceModel;
	
	String infoText = "";//Hej, ovan �r en lista p� de bilder som �verskred den inst�llda ration, 
	String assureBothAxisInfoText;
	
	class MyDocumentListener implements DocumentListener {
		final String newline = "\n";

		public void insertUpdate(DocumentEvent e) {
			//updateLog(e, "inserted into");
			updateRatios(e, "inserted into");
			System.out.println(settings.getRatioX() + " : " + settings.getRatioY());
		}
		public void removeUpdate(DocumentEvent e) {
			//updateLog(e, "removed from");
			updateRatios(e, "removed from");
			System.out.println(settings.getRatioX() + " : " + settings.getRatioY());
		}
		public void changedUpdate(DocumentEvent e) {
			//Plain text components don't fire these events.
		}

		public void updateLog(DocumentEvent e, String action) {
			Document doc = (Document)e.getDocument();
			try {
				System.out.println("updateLog med action " + action + ", doc " + doc.getProperty("name") + ", text length: " + doc.getLength() + " getText: " + doc.getText(0, doc.getLength()));
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			/*displayArea.append(
					changeLength + " character"
							+ ((changeLength == 1) ? " " : "s ")
							+ action + " " + doc.getProperty("name") + "."
							+ newline
							+ "  Text length = " + doc.getLength() + newline);
			displayArea.setCaretPosition(displayArea.getDocument().getLength());*/
		}
		
		public double updateRatios(DocumentEvent e, String action) {
			Document doc = (Document)e.getDocument();
			int length = doc.getLength();
			if(length == 0) {
				return 0;
			}
			String name = (String) doc.getProperty("name");
			String text = "1";
			try {
				text = doc.getText(0, doc.getLength());
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			switch(name) {
			case "ratioX":
				settings.setRatioX(Double.parseDouble(text));
				return settings.getRatioX();
			case "ratioY":
				settings.setRatioY(Double.parseDouble(text));
				return settings.getRatioY();
				default:
					return 0;
			}
		}
	}

	class MyTextActionListener implements ActionListener {
		/** Handle the text field Return. */
		public void actionPerformed(ActionEvent e) {
			/*
			 * int selStart = textArea.getSelectionStart(); int selEnd =
			 * textArea.getSelectionEnd();
			 * 
			 * textArea.replaceRange(textField.getText(), selStart, selEnd);
			 * textField.selectAll();
			 */
			System.out.println("MyTextActionListener actionPerformed, " + e);
		}
	}
	
	
	  private class CheckBoxAction extends AbstractAction {
	  
	  public CheckBoxAction() { super("Disabled"); }
	  
	  @Override public void actionPerformed(ActionEvent e) {
	  
//	  if (okBtn.isEnabled()) { okBtn.setEnabled(false); } else {
//	  okBtn.setEnabled(true); }
		  }
		  }
	 
	
	
	public app(String title) {
		super(title);
	}
	
	class JTextFieldLimit extends PlainDocument {//exempel f�r JFormattedTextField
		private int limit;
		JTextFieldLimit(int limit) {
			super();
			this.limit = limit;
		}

		JTextFieldLimit(int limit, boolean upper) {
			super();
			this.limit = limit;
		}

		public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
			if (str == null)
				return;

			if ((getLength() + str.length()) <= limit) {
				super.insertString(offset, str, attr);
			}
		}
	}
	

	public static void main(String[] args) throws IOException {
	      /*SwingUtilities.invokeLater(new Runnable() {
	          public void run() {
	             init();
	          }
	       });*/
		
		app a = new app("Test");
		a.init();
	}
	
	public void init() {
		System.out.println("init");
		app a = new app("ImageRatioPadder");
		//Container c = a.getContentPane();
		
		AppLanguage myLanguage = new AppLanguage("Swedish", "sv");//Set by programmer, later user(?)
		
		phraseHandler = new AppPhraseHandler(myLanguage); //See AppLanguage.java for language codes
		
		phraseHandler.addPhrasesInMyLanguage(
				myLanguage,
				new String[] {"Following images were found exceeding ratio", "Expand", "Search again", "regardless of axis", " on the axis x:y", "Ratio", "Overwrite image files", "Apply ratio on both axis", "regardless of axis", "Will try to expand", "All images have been processed", "Nothing was processed", "No images found in folder", "address the issue and search again", "padding progressing", "Hi, above is a list of the images that exceeded the set ratio, ", "language"},
				new String[] {"Följande bilder hittade som överskrider ratio", "Expandera", "Sök igen", "oavsett ledd.", " på ledden x:y", "Ratio", "Skriv över bilder", "Låt ratio gälla på båda ledder", "oavsett ledd.", "Ska försöka expandera", "Alla bilder har bearbetats", "Inget har bearbetats", "Inga bilder funna i mappen", "åtgärda felen och sök igen", "Utför padding", "Hej, ovan är en lista på de bilder som överskred den inställda ration, ", "språk"}
				);
		
		infoText = phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("that exceeded").get_phrase();
		
		settings = new Settings();
		imageHelper = new ImageHelper(settings);

		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(450, 500);
		f.setLocation(200, 180);
		// AnalogClock a = new AnalogClock();
		// f.add(a, BorderLayout.CENTER);

		// final BufferedImage img = openImage(fileName);
		//final BufferedImage image = ImageIO.read(new File(System.getProperty("user.dir") + "\\" + fileName)); // "C:\\Users\\Emanuel\\eclipse-workspace\\DrawImageOriginal\\"
		foundOffSized = new ArrayList<>();
		
		imageListModel = new DefaultListModel<File>();
		
		String[] languageItems = phraseHandler.getLanguageItems();
		
		languageChoiceModel = new DefaultComboBoxModel<String>(languageItems);
		

		//int maxY = image.getHeight();
		//int maxX = image.getWidth();
		//double X_Y = (double) maxX / (double) maxY;
		//boolean isStanding = X_Y < 1;
		
		listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		
		info1 = new JLabel(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("Following").get_phrase());//("F�ljande bilder hittade som �verskrider ratio");
		
		languageChoice = new JComboBox<String>(languageChoiceModel);
		
		btn1 = new JButton(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("Expand").get_phrase());
		
		btnSearch = new JButton(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("Search again").get_phrase());

		imageFileList = new JList<File>(imageListModel);//images in folder that exceeds
		
		imageFileList.setVisibleRowCount(9);
		
		imageFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		listScrollPane = new JScrollPane(imageFileList);
		
		assureBothAxisInfoText = settings.isAssertBothAxis() ? phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("regardless").get_phrase() : phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("on the axis").get_phrase();
		
		infoMessage = new JLabel("<html>" + infoText + settings.getRatioX() + " : " + settings.getRatioY() + "," + assureBothAxisInfoText + "</html>");
		
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.red);
		
		
		ratioXLbl = new JLabel(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("Ratio").get_phrase() + " x: ");
		ratioX = new JTextField(5);
		ratioYLbl = new JLabel(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("Ratio").get_phrase() + " y: ");
		ratioY = new JTextField(5);
		
		ratioX.setText(String.format("%d", (int)settings.getRatioX()));
		ratioY.setText(String.format("%d", (int)settings.getRatioY()));
		
		//ratioX.setDocument(doc);
        ratioX.addActionListener(new MyTextActionListener());
        ratioX.getDocument().addDocumentListener(new MyDocumentListener());
        ratioX.getDocument().putProperty("name", "ratioX");
        
        ratioY.addActionListener(new MyTextActionListener());
        ratioY.getDocument().addDocumentListener(new MyDocumentListener());
        ratioY.getDocument().putProperty("name", "ratioY");
        
        ratioX.addKeyListener(new KeyAdapter() {//stop nondigits and big numbers
            public void keyTyped(KeyEvent e) {
                char letter = e.getKeyChar();
                if (ratioX.getText().length() >= 3)
                    e.consume();
                else if(!Character.isDigit(letter)){
                    e.consume();
                }
            }
        });
        
        ratioY.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char letter = e.getKeyChar();
                if (ratioY.getText().length() >= 3)
                    e.consume();
                else if(!Character.isDigit(letter)){
                    e.consume();
                }
            }  
        });
		
		ratioRow = new JPanel();
		ratioRow.setLayout(new FlowLayout(FlowLayout.CENTER));
		ratioRow.add(ratioXLbl);
		ratioRow.add(ratioX);
		ratioRow.add(ratioYLbl);
		ratioRow.add(ratioY);
		
		
		overwriteFiles = new JCheckBox(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("Overwrite").get_phrase());
		overwriteFiles.setSelected(settings.isReplaceOriginalImage());
		//overwriteFiles.setAction(new CheckBoxAction());
		overwriteFiles.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//System.out.println(e);
				//System.out.println(e.getActionCommand());
				
				if(e.getActionCommand() == overwriteFiles.getText()) {
					if(overwriteFiles.isSelected()) {
						settings.setReplaceOriginalImage(true);
					}
					else {
						settings.setReplaceOriginalImage(false);
					}
				}
				
			}
			
		});
		
		assertBothAxis = new JCheckBox(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("apply ratio").get_phrase());
		assertBothAxis.setSelected(settings.isAssertBothAxis());
		ratioRow.add(assertBothAxis);
		
		assertBothAxis.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand() == assertBothAxis.getText()) {
					if(assertBothAxis.isSelected()) {
						settings.setAssertBothAxis(true);
					}
					else {
						settings.setAssertBothAxis(false);
					}
					
					assureBothAxisInfoText = settings.isAssertBothAxis() ? phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("regardless").get_phrase() : phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("on the axis").get_phrase();//" oavsett ledd." : " p� ledden x:y";
				}
				System.out.println(settings);
			}
		});
		
		imageFileList.setCellRenderer(new DefaultListCellRenderer() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				Component renderer = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (renderer instanceof JLabel && value instanceof File) {
                    ((JLabel) renderer).setText(((File) value).getName());
                }
                return renderer;
			}
		}
		);
		
		searchImages();

		listPane.add(info1);
		listPane.add(Box.createRigidArea(new Dimension(0,5)));
		listPane.add(listScrollPane);
		listPane.add(infoMessage);
		listPane.add(errorMessage);
		listPane.add(overwriteFiles);
		
		buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(languageChoice);
		buttonPane.add(btnSearch);
		buttonPane.add(btn1);
		

		pane = new JPanel() {

			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// g.drawImage(image, 0, 0, null);
				//g.drawImage(image, 0, 0, targetX, targetY, 0, 0, maxX, maxY, null);
			}
		};
		
		pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));//new BorderLayout());
		
		//final ImageWithRatio firstImg = foundOffSized.get(0);

		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Clicked expand, will try expand the images");
				infoMessage.setText(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("Will try to expand").get_phrase());//"Ska f�rs�ka expandera");
				//ImageHelper.save2(firstImg, 3, 4, true);
				
				List<Integer> imagesDone = new ArrayList<>();
				for(int i=0; i< imageListModel.size(); i++) {
					try {
						if(imageHelper.save2_NonStatic(imageListModel.get(i), settings)) {//settings.getRatioX(), settings.getRatioY(), settings.isAssertBothAxis()
							imagesDone.add(i);
							infoMessage.setText(infoMessage.getText() + ".");
						}
						else {
							System.out.println("Didn't save");
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				System.out.println("Done with resizing (" + imagesDone.size() + ") images");
				Collections.sort(imagesDone, Collections.reverseOrder()); // to remove the images from list model starting from end
				//imagesDone.stream().forEach((i)->System.out.println(i));
				
				for(Integer i : imagesDone) {
					imageListModel.remove(i);//remove the images that have been processed
					System.out.println(i + " borttagen fr�n lista");
				}
				if(imageListModel.isEmpty()) {
					if(imagesDone.size()>0) {
						infoMessage.setText(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("have been processed").get_phrase() + ": " + imagesDone.size());
					}
					else {
						infoMessage.setText(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("Nothing was processed").get_phrase());
					}
				}
			}
		});
		
		btnSearch.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				searchImages();
			}
		});
		
		pane.add(listPane, BorderLayout.CENTER);
		pane.add(ratioRow);
		pane.add(buttonPane, BorderLayout.PAGE_END);
		
		f.add(pane);
		f.setVisible(true);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				//ImageHelper.save1(pane, "png", "test.png");
				//ImageHelper.save2(firstImg, 3, 4, true);
				//System.exit(0);
			}
		});
	}

	private void searchImages(){
		StringBuilder anyErrors = new StringBuilder();
		btnSearch.setEnabled(false);
		imageListModel.clear();
		errorMessage.setText("");
		
		File[] siblingFiles = ImageHelper.listFromDirectory(System.getProperty("user.dir"));
		if (ImageHelper.hasFolderAnyPicture(siblingFiles)) {
			foundOffSized = imageHelper.listImagesAndRatiosAndIfExceedingRatioLimit_NonStatic(System.getProperty("user.dir"), settings.getRatioX(), settings.getRatioY(), settings.isAssertBothAxis(), anyErrors);//27:37 example
		}
		else {
			infoMessage.setText(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("no images found").get_phrase());//Inga bilder funna i mappen
		}
		
		if(foundOffSized.size() > 0) {
			for(File file : foundOffSized)
				imageListModel.addElement(file);
		}
		btnSearch.setEnabled(true);
		
		infoMessage.setText("<html>" + infoText + settings.getRatioX() + " : " + settings.getRatioY() + "," + assureBothAxisInfoText + "</html>");
		if(anyErrors.length() > 0) {
			errorMessage.setText(anyErrors.toString());
			btn1.setEnabled(false);
			btn1.setToolTipText(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("address").get_phrase());//�tg�rda felen och s�k igen
		}
		else {
			btn1.setEnabled(true);
			btn1.setToolTipText(phraseHandler.getPhraseInMyLanguageFromEnlishRefSearch("padding progressing").get_phrase());//Utf�r padding
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO How use?
		System.out.println(e);
	}
}