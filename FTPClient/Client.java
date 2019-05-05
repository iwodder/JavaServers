package FTPClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Scanner;

public class Client 
{
	private Socket s;
	private BufferedReader socketIn;
	private DataInputStream is;
	private DataOutputStream os;
	private BufferedInputStream bis;
	private BufferedOutputStream bos;
	private Scanner stdin;
	private static String ready = "READY";
	private static String success = "SUCCESS";
	private static String received = "FILE RECEIVED";
	private static String cancelled = "CANCELLED";
	
	public Client(String ipAdd, int port)
	{
		try 
		{
			s = new Socket(ipAdd, port);
			is = new DataInputStream(s.getInputStream());
			os = new DataOutputStream(s.getOutputStream());
			stdin = new Scanner(System.in);
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(os);
		}	
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	//Receive file from server
	public void getFile() throws IOException
	{
		//Declare variables
		FileOutputStream fos;
		long fileLen, totalLen;
		
		//Sent command to server
		os.writeUTF("GET");
		
		String response = is.readUTF();
		
		if(response.equals(ready))
		{
			//Generalize Q&A for which file to receive and the output file name using the consoles' input stream
			System.out.println("Which file would you like to get?");
			String file = stdin.nextLine();
			System.out.println("What will be the file name?");
			String newFileName = stdin.nextLine();
			File f = new File(newFileName);
			fos = new FileOutputStream(f);
			
			//Send file name being requested to server
			os.writeUTF(file);
			
			//Ascertain the number of bytes in the file, totalLen is used for reading from the DataInputStream
			fileLen = is.readLong();
			totalLen = fileLen;
			
			//Declare a buffer for holding the incoming data
			byte[] buffer = new byte[1024];
			int len;
			do
			{		
					//Will always read 1024
					len = is.read(buffer,0,buffer.length);
					
					//To only read the appropriate bytes we must subtract the total bytes already read from the file length
					if(totalLen < len)
					{
						fos.write(buffer,0,(int)totalLen);
						totalLen = totalLen - totalLen;
					}
					else
					{
						fos.write(buffer,0,len);
						totalLen = totalLen - len;
					}
					
			}
			while(totalLen > 0);
			
			//Close the output stream and send a message to the server about successfully receieving the file
			fos.close();
			os.writeUTF("FILE RECEIVED");
			System.out.println("Done! Saved to " + newFileName);
			
//			ANOTHER METHOD FOR RECEIVING FILES, RECEIVES THE ENTIRE FILE AT ONCE. ISSUES WITH IMAGE FILES.
//			fileLen = is.readLong();
//			System.out.println(fileLen);
//			byte[] buffer = new byte[(int)fileLen];
//			bis.read(buffer);
//			System.out.println(Arrays.toString(buffer));
//			fos.write(buffer);
//
//			ANOTHER METHOD FOR RECEIVING FILES, ONLY WORKS WITH TEXT FILES
//			do
//			{
//				b = (byte) bis.read();
//				System.out.print(b);
//				if(b != -1)
//				{
//					fos.write(b);
//				}
//			}
//			while(b > -1);
		}
	}

	//Delete file from server
	public void deleteFile() throws IOException
	{
		String response, file;
		os.writeUTF("DELETE");
		
		response = is.readUTF();
		if(response.equals(ready))
		{
			System.out.println("Which file do you want to delete?");
			file = stdin.nextLine();
			os.writeUTF(file);
			response = is.readUTF();
			System.out.println(response);
			response = stdin.nextLine();
			os.writeUTF(response);
			response = is.readUTF();
			if(response.equals(success))
			{
				System.out.println(response);
			}
			else
			{
				System.out.println(response);
			}
		}
	}
	//Send file to server
	public void sendFile() throws IOException
	{
		String response,file;
		File f;
		FileInputStream fis;
		byte[] buffer = new byte[1024];
		int len;
		
		os.writeUTF("RECEIVE");
		
		response = is.readUTF();
		if(response.equals(ready))
		{
			System.out.println("Which file do you want to send?");
			file = stdin.nextLine();
			f = new File(file);
			
			while(f.length() <= 0)
			{
				System.out.println("Please choose a file with great than 0 bytes.");
				file = stdin.nextLine();
				f = new File(file);
			}
			
			fis = new FileInputStream(f);
			
			//Send length of the file to be sent
			os.writeLong(f.length());
			//Send the name of the file
			os.writeUTF(file);
			
			response = is.readUTF();
			if(response.contains("File exists"))
			{
				System.out.println(response);
				response = stdin.nextLine();
				os.writeUTF(response);
				response = response.toUpperCase();
			}
			if(response.equals("Y"))
			{
				System.out.println("Sending...");
				while((len = fis.read(buffer, 0, buffer.length)) > 0)
				{
					os.write(buffer, 0, len);
				}
				System.out.println("Sent");
				fis.close();
				f = null;
	
				while(true)
				{
					response = is.readUTF();
					if(response.equals(received))
					{
						System.out.println("File Saved!");
						break;
					}
				}	
			}
			else
			{
				response = is.readUTF();
				System.out.println(response);
			}	
		}
		else
		{
			response = is.readUTF();
			System.out.println(response);
		}
	}
} 
