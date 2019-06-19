import java.awt.event.*;
import javax.swing.*;
import java.io.*;

public class jiemian extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 9107439319164412450L;
	private JTextArea text_receiver;                     
	private JTextField text_sender;                        
	private PrintWriter cout;                             
	private String name;                                   
	private JMenuBar JM;                                                     
	private JScrollPane JSP;
	public jiemian(String name, String title, PrintWriter cout)  //���췽��
	{
		super("������  "+name+"  "+title);
		this.setSize(400,450);
		this.setLocation(365,255);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.JM=new JMenuBar();
		this.setJMenuBar(JM);
		this.text_receiver = new JTextArea();
		this.text_receiver.setEditable(false);   
		this.JSP=new JScrollPane(text_receiver);
		this.add(JSP);
		JPanel panel = new JPanel();
		this.add(panel,"South");
		this.text_sender = new JTextField(12);
		panel.add(this.text_sender);
		this.text_sender.addActionListener(this);  

		JButton button_send = new JButton("����");
		panel.add(button_send);
		button_send.addActionListener(this);

		JButton button_leave = new JButton("����");
		panel.add(button_leave);
		button_leave.addActionListener(this);

		this.setVisible(true);
		this.setWriter(cout);
		this.name = name;
	}

	public jiemian()
	{
		this("","",null);
	}

	public void setWriter(PrintWriter cout)                //�����ַ����������
	{
		this.cout = cout;
	}

	public void receive(String message)                    //��ʾ�Է�������Ϣ
	{
		text_receiver.append(message+"\r\n");
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand()=="����"||text_sender.getText().equals("bye"))
		{
			if (this.cout!=null)
			{
				this.cout.println(name+"����");
				this.cout.println("bye");
				this.cout = null;
			}
			text_receiver.append("������\n");
		}
		else                                               
		{
			if (this.cout!=null)
			{
				this.cout.println(name+" ˵��"+text_sender.getText());
				text_receiver.append("��˵��"+text_sender.getText()+"\n");//˫������
				text_sender.setText("");
			}
			else
				text_receiver.append("�����ߣ������ٷ��͡�\n");
		}
	}
	public static void main(String args[])
	{
		new jiemian();
	}
}
import java.net.*;
import java.io.*;

public class server implements Runnable
{
	private Socket client;                                 
	private Thread thread;
	private jiemian chatframe;                          //�����ҵĽ���
	private ServerSocket server;                           
	public server(int port, String name)      
	{
		try
		{
			server = new ServerSocket(6666);              
			client = server.accept();                    
			//���ӳɹ��󷵻ض���
			BufferedReader cin = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter cout = new PrintWriter(client.getOutputStream(), true);

			chatframe = new jiemian(name,"  ����� �˿�"+port,cout);  //���������ҵ�ͼ���û�����
			chatframe.receive("���� "+cin.readLine());    
			cout.println(name);                           

			String aline = "";
			do                                           
			{
				aline = cin.readLine();                    
				if (aline!=null  && !aline.equals("bye"))  
					chatframe.receive(aline);
			}while (aline!=null  && !aline.equals("bye"));

			chatframe.setWriter(null);                    

			cin.close();
			cout.close();

			client.close();                             
			server.close();                               
		}
		catch(IOException e)  {}
	}
	public void start() {
		thread = new Thread(this);
		thread.start();
	}
	public static void main(String args[])
	{
		new server(6666,"����");               
	}

	public void run() {
		Thread me=Thread.currentThread();
		while(me==thread){
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				 break;
			}
			
		}
	}
}

import java.net.*;
import java.io.*;
public class jiekou {
	private jiemian chatframe;                          
	private Socket client;                                 

	public jiekou(String host, int port, String name)  //������������Ϣ
	{
		try
		{
			client = new Socket(host,port); 
			BufferedReader cin = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter cout = new PrintWriter(client.getOutputStream(),true);
			chatframe = new jiemian(name," �ͻ��� ����"+host+" �˿�"+port,cout);   //���������ҵ�ͼ���û�����
			cout.println(name);                            //��������
			chatframe.receive("���� "+cin.readLine());     //��ʾ����
			String aline = "";
			do                                            
			{
				aline = cin.readLine();
				if (aline!=null && !aline.equals("bye"))   //���յ�bay���߶Է����߾��޷�����
					chatframe.receive(aline);
			}while (aline!=null &&!aline.equals("bye"));
			chatframe.setWriter(null);                     
			cin.close();
			cout.close();
			client.close();   //�ضϿ�����
		}
		catch(IOException e) {}
	}
	public static void main(String args[])
	{
		new jiekou("localhost",6666,"����");         
	}

}
