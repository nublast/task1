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
	public jiemian(String name, String title, PrintWriter cout)  //构造方法
	{
		super("聊天室  "+name+"  "+title);
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

		JButton button_send = new JButton("发送");
		panel.add(button_send);
		button_send.addActionListener(this);

		JButton button_leave = new JButton("离线");
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

	public void setWriter(PrintWriter cout)                //设置字符输出流对象
	{
		this.cout = cout;
	}

	public void receive(String message)                    //显示对方发送消息
	{
		text_receiver.append(message+"\r\n");
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getActionCommand()=="离线"||text_sender.getText().equals("bye"))
		{
			if (this.cout!=null)
			{
				this.cout.println(name+"离线");
				this.cout.println("bye");
				this.cout = null;
			}
			text_receiver.append("我离线\n");
		}
		else                                               
		{
			if (this.cout!=null)
			{
				this.cout.println(name+" 说："+text_sender.getText());
				text_receiver.append("我说："+text_sender.getText()+"\n");//双方交流
				text_sender.setText("");
			}
			else
				text_receiver.append("已离线，不能再发送。\n");
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
	private jiemian chatframe;                          //聊天室的界面
	private ServerSocket server;                           
	public server(int port, String name)      
	{
		try
		{
			server = new ServerSocket(6666);              
			client = server.accept();                    
			//连接成功后返回对象
			BufferedReader cin = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter cout = new PrintWriter(client.getOutputStream(), true);

			chatframe = new jiemian(name,"  服务端 端口"+port,cout);  //创建聊天室的图形用户界面
			chatframe.receive("连接 "+cin.readLine());    
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
		new server(6666,"荆轲");               
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

	public jiekou(String host, int port, String name)  //交流两主机信息
	{
		try
		{
			client = new Socket(host,port); 
			BufferedReader cin = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter cout = new PrintWriter(client.getOutputStream(),true);
			chatframe = new jiemian(name," 客户端 主机"+host+" 端口"+port,cout);   //创建聊天室的图形用户界面
			cout.println(name);                            //发送网名
			chatframe.receive("连接 "+cin.readLine());     //显示网名
			String aline = "";
			do                                            
			{
				aline = cin.readLine();
				if (aline!=null && !aline.equals("bye"))   //接收到bay或者对方离线就无法发送
					chatframe.receive(aline);
			}while (aline!=null &&!aline.equals("bye"));
			chatframe.setWriter(null);                     
			cin.close();
			cout.close();
			client.close();   //关断开连接
		}
		catch(IOException e) {}
	}
	public static void main(String args[])
	{
		new jiekou("localhost",6666,"嬴政");         
	}

}
