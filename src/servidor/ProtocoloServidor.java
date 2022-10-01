package servidor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class ProtocoloServidor 
{
	private static long tamanoArchivo;
	
	public static void procesar(BufferedReader pIn, PrintWriter pOut, CyclicBarrier bar, String nameFile, int numeroThreads, int cantConexiones) throws IOException, NoSuchAlgorithmException, InterruptedException, BrokenBarrierException
	{
		String inputLine;
		String outputLine;
		String recieved;
		
		pOut.println(numeroThreads + "");
		pOut.println(cantConexiones + "");
		
		
		String log = "";
		
		log += "ID Client: " + numeroThreads +"\n";
		
		inputLine = pIn.readLine();
		System.out.println("Entry to process: " + inputLine);
		
		if(inputLine.equals("ack"))
		{	
			bar.await();
			log += "Name of the file: " + Servidor.getNombreArchivo() + ".txt \n";
			outputLine = readFile(nameFile);
			log += "Size of the file: " + tamanoArchivo + " MB \n";
			long timeI = System.currentTimeMillis();
			System.out.println("Sending file to client " + numeroThreads + "...");
			pOut.println(outputLine);
			
			recieved = pIn.readLine();
			if(recieved.equals("ack"))
			{
				long timeF = System.currentTimeMillis();
				long totalTime = timeI - timeF;
				pOut.println(nameFile);
				pOut.println(tamanoArchivo + "");
				System.out.println("File sended succesfully to client " + numeroThreads + "...");
				log += "File recieved succesfully \n";
				log += "Time to transfer the file: " + totalTime + " miliseconds \n";
				String password = outputLine;
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				byte[] digest = md.digest(password.getBytes(StandardCharsets.UTF_8));
				String sha256 = byte2str(digest);
				pOut.println(sha256);
				
				recieved = pIn.readLine();
				if(recieved.equals("ack"))
				{
					log += "Hash recieved succesfully \n";
				}
			}
		}
		
		Servidor.setLog(log);
	}
	
	/**
	 * 
	 * @param b an array of bytes that is going to be converted to String
	 * @return
	 */
	
	public static String byte2str( byte[] b )
	{
		String ret = "";
		for (int i = 0 ; i < b.length ; i++) {
			String g = Integer.toHexString(((char)b[i])&0x00ff);
			ret += (g.length()==1?"0":"") + g;
		}
		return ret;
	}
	/**
	 * 
	 * @param ar name of the archive that it's going to read
	 * @return
	 * @throws IOException
	 */
	
	public static String readFile(String ar) throws IOException
	{
		String archive = "";

		File doc = new File("home/servidor/aplicacion/ISIS3204/Data/" + ar + ".txt");
		
		tamanoArchivo = doc.length()/(1024*1024);

		BufferedReader obj = new BufferedReader(new FileReader(doc));

		String strng;
		while ((strng = obj.readLine()) != null)
			archive += strng;
		
		obj.close();

		return archive;
	}

}
