package FTPServer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.LoggingPermission;

import javax.xml.ws.Response;

public class Server 
{
	private ServerSocket ss;
	private Socket s;
	private static DataInputStream is;
	private static DataOutputStream os;
	private static BufferedInputStream bis;
	private static BufferedOutputStream bos;
	private String get = "GET";
	private String receive = "RECEIVE";
	private String delete = "DELETE";
	private String invalid = "INVALID";
	private static String received = "FILE RECEIVED";
	
	public Server(int port)
	{
		try
		{
			ss = new ServerSocket(port);
			s = ss.accept();	
			is = new DataInputStream(s.getInputStream());
			os = new DataOutputStream(s.getOutputStream());
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(os);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void run() throws IOException
	{
		System.out.println("Ready for connections...");
		while(true)
		{	
			System.out.println("Waiting for command...");
			try
			{
				String input = is.readUTF();
				System.out.println("From client: " + input);
				input = input.toUpperCase();
				switch(input)
				{
					case("GET"):
					{
						sendFile();
						break;
					}
					
					case("RECEIVE"):
					{
						receiveFile();
						break;
					}
					
					case("DELETE"):
					{
						deleteFile();
						break;
					}
					case("END"):
					{
						closeConnection();
					}
						
				}
			}
			catch (SocketException e)
			{
				
				bos.close();
				bis.close();
				os.close();
				is.close();
				s.close();
				ss.close();
				break;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	private static void sendFile() throws IOException
	{
		os.writeUTF("READY");
		
		String file = is.readUTF();
		
		System.out.println("Getting file: " + file);
		
		File f = new File(file);
		
		FileInputStream fis = new FileInputStream(f);
		
		BufferedInputStream bs = new BufferedInputStream(fis);
		
		long fileLen;
//		int b;
		fileLen = f.length();
		os.writeLong(fileLen);
		byte[] buffer = new byte[1024];
		int len;
		while((len = fis.read(buffer, 0, buffer.length)) > 0)
		{
			os.write(buffer, 0, len);
		}

		fis.close();
		System.out.println("File Sent");
		while(true)
		{
			String response = is.readUTF();
			if(response.equals(received))
			{
				break;
			}
		}
//		byte[] buffer = new byte[(int)fileLen];
//		bs.read(buffer, 0, buffer.length);
//		System.out.print(Arrays.toString(buffer));
//		os.write(buffer, 0, buffer.length);
		
//		do
//		{
//			b = (byte) bs.read();
//			System.out.print(b);
//			os.write(b);
//		} 
//		while(b > -1);		
	}

	private static void receiveFile() throws IOException
	{
		FileOutputStream fos;
		File f;
		long fileLen, totalLen;
		String file,response;
		byte[] buffer = new byte[1024];
		int len;
		
		os.writeUTF("READY");
		
		fileLen = is.readLong();
		file = is.readUTF();
		f = new File(file);
		if(f.exists())
		{
			os.writeUTF("File exists, overwrite? (Y/N)");
			response = is.readUTF();
			response = response.toUpperCase();
			if(response.equals("Y"))
			{
				System.out.println("Receiving...");
				fos = new FileOutputStream(f);
				totalLen = fileLen;
				
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
			
				fos.close();
				f = null;
				os.writeUTF("FILE RECEIVED");
			}
			else
			{
				fileLen = 0;
				file = null;
				f = null;
				os.writeUTF("CANCELLED");
			}
			
		}
		else
		{
			fos = new FileOutputStream(f);
			totalLen = fileLen;
			
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
		
			fos.close();
			f = null;
			os.writeUTF("FILE RECEIVED");
		}
		
		
		
	}
	
	private static void deleteFile() throws IOException
	{
		File f;
		String response, file;
		
		os.writeUTF("READY");
		
		file = is.readUTF();
		
		os.writeUTF("ARE YOU SURE YOU WANT TO DELETE: " + file + "? (Y/N)");
		
		response = is.readUTF();
		response = response.toUpperCase();
		if(response.equals("Y"))
		{
			f = new File(file);
			if(f.exists())
			{
				if(f.delete())
				{
					os.writeUTF("SUCCESS");
				}
				else
				{
					os.writeUTF("ERROR, TRY AGAIN LATER");
				}
			}
			else
			{
				os.writeUTF("NO SUCH FILE");
			}
		}
		else
		{
			os.writeUTF("CANCELLED");
		}
		
	}
	
	private static void closeConnection()
	{
		
	}
}
