// Java Chatting Server

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Server extends JFrame {
	static int tCnt = 0; // �� �ο���
	static int mCnt = 0; // ���� �� �ο���
	static int fCnt = 0; // ���� �� �ο���
	static int sumCnt = 0; // �� �ο���
	static int bigger = 0; //�˻����� ����, ���ڵ� �� ū��
	static int match = 0; 
	static int roomCnt = 0; //���ȣ
	int myIndex = 0; // ���� ����� �ε���
	int yourIndex = 0; // �޴� ����� �ε���
	
	int mRandom[] = new int[100]; 
	int fRandom[] = new int[100]; 
	Random random; 
	
	String sender;
	String receiver;

	Vector<String> totalId = new Vector<String>(); // ��ü id ����

	Vector<String> mId = new Vector<String>(); // ���� id ����
	Vector<String> fId = new Vector<String>(); // ���� id ����

	ArrayList<String> mIdSearch = new ArrayList<String>();
	ArrayList<String> fIdSearch = new ArrayList<String>();
	
	int[] mState = new int[100];  //���� ���� 0-�⺻ 1-��Ī�� 2-��ȭ��
	int[] fState = new int[100];
	
	private JPanel contentPane;
	private JTextField textField; // ����� PORT��ȣ �Է�
	private JButton Start; // ������ �����Ų ��ư
	JTextArea textArea; // Ŭ���̾�Ʈ �� ���� �޽��� ���

	private ServerSocket socket; // ��������
	private Socket soc; // �������
	private int Port; // ��Ʈ��ȣ

	private Vector vc = new Vector(); // ����� ����ڸ� ������ ����

	public static void main(String[] args) {
		
		Server frame = new Server();
		frame.setVisible(true);
	}

	public Server() {

		init();

	}

	private void init() { // GUI�� �����ϴ� �޼ҵ�

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 280, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane js = new JScrollPane();

		textArea = new JTextArea();
		textArea.setColumns(20);
		textArea.setRows(5);
		js.setBounds(0, 0, 264, 254);
		contentPane.add(js);
		js.setViewportView(textArea);

		textField = new JTextField();
		textField.setBounds(98, 264, 154, 37);
		contentPane.add(textField);
		textField.setColumns(10);
		textField.setText("44444");
	
		JLabel lblNewLabel = new JLabel("Port Number");
		lblNewLabel.setBounds(12, 264, 98, 37);
		contentPane.add(lblNewLabel);
		Start = new JButton("���� ����");
		Myaction action = new Myaction();
		Start.addActionListener(action); // ����Ŭ������ �׼� �����ʸ� ��ӹ��� Ŭ������
		textField.addActionListener(action);
		Start.setBounds(0, 325, 264, 37);
		contentPane.add(Start);
		textArea.setEditable(false); // textArea�� ����ڰ� ���� ���ϰԲ� ���´�.
	}

	class Myaction implements ActionListener // ����Ŭ������ �׼� �̺�Ʈ ó�� Ŭ����
	{
		@Override
		public void actionPerformed(ActionEvent e) {

			// �׼� �̺�Ʈ�� sendBtn�϶� �Ǵ� textField ���� Enter key ġ��
			if (e.getSource() == Start || e.getSource() == textField) {
				if (textField.getText().equals("") || textField.getText().length() == 0)// textField��
																						// ����
																						// �������
																						// ������
				{
					textField.setText("��Ʈ��ȣ�� �Է����ּ���");
					textField.requestFocus(); // ��Ŀ���� �ٽ� textField�� �־��ش�
				} else {
					try {
						Port = Integer.parseInt(textField.getText()); // ���ڷ�
																		// �Է�����
																		// ������
																		// ���� �߻�
																		// ��Ʈ��
																		// ����
																		// ����.
						server_start(); // ����ڰ� ����ε� ��Ʈ��ȣ�� �־����� ���� ���������� �޼ҵ� ȣ��
					} catch (Exception er) {
						// ����ڰ� ���ڷ� �Է����� �ʾ����ÿ��� ���Է��� �䱸�Ѵ�
						textField.setText("���ڷ� �Է����ּ���");
						textField.requestFocus(); // ��Ŀ���� �ٽ� textField�� �־��ش�
					}
				} // else �� ��
			}

		}

	}

	private void server_start() {
		try {
			socket = new ServerSocket(Port); // ������ ��Ʈ ���ºκ�
			Start.setText("����������");
			Start.setEnabled(false); // ������ ���̻� �����Ű�� �� �ϰ� ���´�
			textField.setEnabled(false); // ���̻� ��Ʈ��ȣ ������ �ϰ� ���´�

			if (socket != null) // socket �� ���������� ��������
			{
				Connection();
			}

		} catch (IOException e) {
			textArea.append("������ �̹� ������Դϴ�...\n");

		}
	}

	private void Connection() {
		Thread th = new Thread(new Runnable() { // ����� ������ ���� ������
			@Override
			public void run() {
				while (true) { // ����� ������ ����ؼ� �ޱ� ���� while��
					try {
						textArea.append("����� ���� �����...\n");
						soc = socket.accept(); // accept�� �Ͼ�� �������� ���� �����
						textArea.append("����� ����!!\n");
						UserInfo user = new UserInfo(soc, vc); // ����� ���� ������ �ݹ�
																// ������Ƿ�, user
																// Ŭ���� ���·� ��ü ����
						// �Ű������� ���� ����� ���ϰ�, ���͸� ��Ƶд�
						vc.add(user); // �ش� ���Ϳ� ����� ��ü�� �߰�
						user.start(); // ���� ��ü�� ������ ����
					} catch (IOException e) {
						
						textArea.append("!!!! accept ���� �߻�... !!!!\n");
					}
				}
			}
		});
		th.start();
	}

	class UserInfo extends Thread {
		private InputStream is;
		private OutputStream os;
		private DataInputStream dis;
		private DataOutputStream dos;
		private Socket user_socket;
		private Vector user_vc;
		private String Nickname = "";

		public UserInfo(Socket soc, Vector vc) // �����ڸ޼ҵ�
		{
			// �Ű������� �Ѿ�� �ڷ� ����
			this.user_socket = soc;
			this.user_vc = vc;

			User_network();
		}

		public void User_network() {
			try {
				is = user_socket.getInputStream();
				dis = new DataInputStream(is);
				os = user_socket.getOutputStream();
				dos = new DataOutputStream(os);

				// Nickname = dis.readUTF(); // ������� �г��� �޴ºκ�
				byte[] b = new byte[1024];
				dis.read(b);

				String sMessage;
				String gender = new String(b);
				gender = gender.trim();
				System.out.println("ó��" + gender);

	
				if (gender.equals("male")) {
					System.out.println("���� ���");
					mId.addElement("m" + mCnt); // add�� boolean ���ϰ�, addElement��
												// ���ϰ�����
					mState[mCnt] = 0;
					textArea.append("ID " + mId.elementAt(mCnt) + " ����\n");
					textArea.setCaretPosition(textArea.getText().length()); // Ŀ��
																			// ��ġ
																			// ó������?
					sMessage = "DEFINE-" + mId.elementAt(mCnt);
					send_Message(sMessage); // ����� ����ڿ��� ���������� �˸�
					System.out.println(mId.elementAt(mCnt) + "�������" + mCnt + "��° ��ϿϷ�");
					
					totalId.addElement("m" + mCnt);
					mCnt++;
					tCnt++;
				}

				else if (gender.equals("female")) {
					System.out.println("���� ���");
					fId.addElement("f" + fCnt); // add�� boolean ���ϰ�, addElement��
												// ���ϰ�����
					fState[fCnt] = 0;
					textArea.append("ID " + fId.elementAt(fCnt) + " ����\n");
					textArea.setCaretPosition(textArea.getText().length()); // Ŀ��
																			// ��ġ
																			// ó������?
					sMessage = "DEFINE-" + fId.elementAt(fCnt);
					send_Message(sMessage); // ����� ����ڿ��� ���������� �˸�
					System.out.println(fId.elementAt(fCnt) + "�������" + fCnt + "��° ��ϿϷ�");
					totalId.addElement("f" + fCnt);
					fCnt++;
					tCnt++;
				}

			} catch (Exception e) {
				textArea.append("��Ʈ�� ���� ����\n");
				textArea.setCaretPosition(textArea.getText().length());
			}
				
		}
			
		public synchronized void InMessage(String str) {
			/*str = str.trim();*/

			String sMessage;
			String[] receive;

			receive = str.split("-");
			

			// ����� �޼��� ó��
			if (receive[0].equals("start")) {
				sender = receive[1];

				System.out.println("�׽�Ʈ2-0 start �� ���" + sender);
				
				if(sender.contains("m")) {
					System.out.println("�׽�Ʈ2-1 sender�� " +sender);  
					mIdSearch.add(sender);
					System.out.println("++++++++++++++++++" + mIdSearch.size());
					for(int i=0; i<mCnt; i++)
						if(mId.contains(sender)) {
							mState[i] = 1;
							System.out.println("�׽�Ʈ3-1 mId"+mState[i]);
							/*System.out.println("�׽�Ʈ3-2 mId "+i+"��°���"+mIdSearch.get(i));*/
						}
				}
				
				else if(sender.contains("f")) {
					System.out.println("�׽�Ʈ2-1 sender�� " +sender);  
					fIdSearch.add(sender);
					System.out.println("-------------------" + fIdSearch.size());
					for(int i=0; i<fCnt; i++)
						if(fId.contains(sender)) {
							fState[i] = 1;
							System.out.println("�׽�Ʈ3-1 ���� "+fState[i]);
							/*System.out.println("�׽�Ʈ3-2 fId"+i+"��°���"+fIdSearch.get(i));*/
						}
				}
				System.out.println("�׽�Ʈ4-1 �˻����� ����" + mIdSearch.size());
				System.out.println("�׽�Ʈ4-2 �˻����� ����" + fIdSearch.size());
				//match ���� ����
				//���Ⱑ �³�
				if(mIdSearch.size() < fIdSearch.size())
					match = mIdSearch.size();
					
				else
					match = fIdSearch.size();
					
				System.out.println("match ���� =" +match);
				random = new Random();
				
				for(int i=roomCnt; i<match; i++) {
					mRandom[i] = random.nextInt(match-roomCnt)+roomCnt;
					System.out.println("�׽�Ʈ5-0" + mRandom[i]);
					for(int j=roomCnt; j<i; j++) {
						if(mRandom[i] == mRandom[j]) {
							i = i-1;
							break;
						}
					}
					System.out.println("�׽�Ʈ5-1 " + "mrandom " + i +"�ε��� " + mRandom[i]);
				}
				
				for(int i=roomCnt; i<match; i++) {
					fRandom[i] = random.nextInt(match-roomCnt)+roomCnt;
					System.out.println("�׽�Ʈ5-0" + fRandom[i]);
					for(int j=roomCnt; j<i; j++) {
						if(fRandom[i] == fRandom[j]) {
							i = i-1;
							break;
						}
					}
					System.out.println("�׽�Ʈ5-2 " + "frandom " + i +"�ε��� " + fRandom[i]);
				}
				
				System.out.println("�׽�Ʈ6 match ����" +match + "roomCnt ����" + roomCnt);
					 
				//�˻��߿��� sender�� �����϶� 
				for(int i=roomCnt; i<match; i++) {
					sender = mIdSearch.get(mRandom[i]);
					receiver = fIdSearch.get(fRandom[i]);
					System.out.println("�׽�Ʈ7-1" + "�˻����� sender���� " + mRandom[i] + "==" + sender);
					System.out.println("�׽�Ʈ7-2" + "�˻����� receiver���� " +fRandom[i] + "==" + receiver);
					str = "IN-" + sender + "-" + receiver;
					System.out.println("�׽�Ʈ7-3 "+ str);
					System.out.println("�׽�Ʈ7-4 "+ mCnt);
					for(int j=0; j<tCnt; j++) {
						if(totalId.elementAt(j).equals(sender)) { 
							System.out.println("�׽�Ʈ7-5" + str + "�ε���" +j);
							broad_cast(str, j);
						}
					}
				}
				
				//�˻��߿��� sender�� �����϶� 
				for(int i=roomCnt; i<match; i++) {
					sender = fIdSearch.get(fRandom[i]);
					receiver = mIdSearch.get(mRandom[i]);
					System.out.println("�׽�Ʈ8-1 " + "�˻����� sender����" + fRandom[i] + "==" + sender);
					System.out.println("�׽�Ʈ8-2 " +"�˻����� receiver����" +mRandom[i] + "==" + receiver);
					str = "IN-" + sender + "-" + receiver;
					System.out.println("�׽�Ʈ8-3 "+ str);
					System.out.println("�׽�Ʈ8-4 "+ fCnt);
					for(int j=0; j<tCnt; j++) { //�ε��� ���� �ߺ� �ȵ�
						if(totalId.elementAt(j).equals(sender)) { 
							System.out.println("�׽�Ʈ8-5" + str + "�ε���" +j);
							broad_cast(str, j);
						}
					}
				}
				
				roomCnt = match;
				
				str = "IN-" + sender + "-" + receiver; //�޴� ��� 
				receive = str.split("-");
			}
			
			//������ ���濡�� �˸�
			if(receive[0].equals("end")) {
				String endId = receive[1];
				String endMatchId = null;
				
				System.out.println("�׽�Ʈ9-0" + str);
				
				for(int i=match-1; i>=0; i--) {
					if(mIdSearch.get(i).equals(endId)) {
						 System.out.println("�׽�Ʈ 9-1-1 " + mIdSearch.get(i));
						 System.out.println("�׽�Ʈ777 �̶��� i����" + i);
						 endMatchId = fIdSearch.get(i);
						 fIdSearch.set(i, "");
						 mIdSearch.set(i, "");
						 break;
					 }
					 
					else if(fIdSearch.get(i).equals(endId)) {
						 System.out.println("�׽�Ʈ 9-1-2 " + fIdSearch.get(i));
						 System.out.println("�׽�Ʈ888 �̶��� i����" + i);
						 endMatchId = mIdSearch.get(i);
						 mIdSearch.set(i, "");
						 fIdSearch.set(i, ""); 
						 break;
					 }
				}
				System.out.println("�׽�Ʈ9-2" + "������� = "+ endId + "�������� ���" + endMatchId);
				
				str = "OUT-" + endMatchId;
				for(int j=0; j<tCnt; j++) {
					if(totalId.elementAt(j).equals(endMatchId)) { 
						System.out.println("�׽�Ʈ9-3 " + str + "�ε���" +j);
						broad_cast(str, j);
					}
				}
			}
			
			//�޽��� �����°�
			if (receive[0].equals("to")) {
				str = receive[3];
				
				for(int i=0; i<tCnt; i++) {
					System.out.println("�׽�Ʈ10" + "�����»�� " + receive[1] + "�޴»�� " + receive[2] + receive[3] );
					for(int j=0; j<tCnt; j++) {
						if (totalId.elementAt(i).equals(receive[1])){
							if (totalId.elementAt(j).equals(receive[2])) {
								myIndex = i;  //myIndex�� totalId�� ����� ���� ���̵� �ε���
								yourIndex = j; //yourIndex�� totalId�� ����� ������ ���̵� �ε���
							} 
						}
					}
				}
			
				broad_cast("I-"+str, myIndex);
				broad_cast("YOU-"+str, yourIndex);
			}
		}
		
		public void broad_cast(String str, int index) {
			/*for (int i = 0; i < user_vc.size(); i++) {*/

				/* UserInfo imsi = (UserInfo) user_vc.elementAt(i); */
				UserInfo imsi = (UserInfo) user_vc.elementAt(index);
				imsi.send_Message(str);
			/*}*/
		}

		public void send_Message(String str) {
			try {
				// dos.writeUTF(str);
				byte[] bb;
				bb = str.getBytes();
				dos.write(bb); // .writeUTF(str);
			} catch (IOException e) {
				textArea.append("�޽��� �۽� ���� �߻�\n");
				textArea.setCaretPosition(textArea.getText().length());
			}
		}

		public void run() // ������ ����
		{
			while (true) {
				try {

					// ����ڿ��� �޴� �޼���
					byte[] b = new byte[1024];
					dis.read(b);
					String msg = new String(b);
					msg = msg.trim();
					// String msg = dis.readUTF();
					InMessage(msg);

				} catch (IOException e) {

					try {
						dos.close();
						dis.close();
						user_socket.close();
						vc.removeElement(this); // �������� ���� ��ü�� ���Ϳ��� �����
						textArea.append(vc.size() + " : ���� ���Ϳ� ����� ����� ��\n");
						textArea.append("����� ���� ������ �ڿ� �ݳ�\n");
						textArea.setCaretPosition(textArea.getText().length());

						break;

					} catch (Exception ee) {

					} // catch�� ��
				} // �ٱ� catch����
					
			}

		}// run�޼ҵ� ��

	} // ���� userinfoŬ������

}